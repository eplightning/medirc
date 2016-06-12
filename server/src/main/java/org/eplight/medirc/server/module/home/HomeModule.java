package org.eplight.medirc.server.module.home;

import com.google.protobuf.ByteString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.server.event.EventLoop;
import org.eplight.medirc.server.event.consumers.FunctionConsumer;
import org.eplight.medirc.server.event.dispatchers.function.MessageDispatcher;
import org.eplight.medirc.server.event.dispatchers.function.message.AuthedMessageFunction;
import org.eplight.medirc.server.event.events.ChannelInactiveEvent;
import org.eplight.medirc.server.event.events.UserAuthedEvent;
import org.eplight.medirc.server.module.Module;
import org.eplight.medirc.server.network.SocketAttributes;
import org.eplight.medirc.server.session.ArchivedSessionsManager;
import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.session.SessionSerializer;
import org.eplight.medirc.server.session.active.ActiveSessionsManager;
import org.eplight.medirc.server.session.repository.SessionRepository;
import org.eplight.medirc.server.session.repository.SessionRepositoryException;
import org.eplight.medirc.server.user.ActiveUser;
import org.eplight.medirc.server.user.User;
import org.eplight.medirc.server.user.Users;

import javax.inject.Inject;

public class HomeModule implements Module {

    private static final Logger logger = LogManager.getLogger(HomeModule.class);

    @Inject
    private ActiveSessionsManager activeSessions;

    @Inject
    private ArchivedSessionsManager archivedSessions;

    @Inject
    private EventLoop loop;

    @Inject
    private MessageDispatcher dispatcher;

    @Inject
    private SessionRepository repo;

    @Inject
    private SessionSerializer sessionSerializer;

    @Inject
    private Users users;

    private void onChannelInactive(ChannelInactiveEvent event) {
        ActiveUser usr = event.getChannel().attr(SocketAttributes.USER_OBJECT).get();

        if (usr == null) {
            return;
        }

        // budowanie wiadomości ..
        Main.UserDisconnected msg = Main.UserDisconnected.newBuilder().setUser(usr.buildUserMessage()).build();

        // wysyłamy do każdego oprócz naszego subiekta
        users.broadcast(msg, usr);
    }

    private void onUserAuthed(UserAuthedEvent ev) {
        // budowanie wiadomości ..
        Main.UserConnected msg = Main.UserConnected.newBuilder().setUser(ev.getUser().buildUserMessage()).build();

        // wysyłamy do każdego oprócz naszego subiekta
        users.broadcast(msg, ev.getUser());
    }

    private void onSyncRequest(ActiveUser usr, Main.SyncRequest msg) {
        logger.info("User `" + usr.getName() + "` sent synchronization request");

        // aktywne sesje
        Main.ActiveSessions.Builder msg1 = Main.ActiveSessions.newBuilder();

        for (Session s : activeSessions.findForUser(usr)) {
            msg1.addSession(s.buildMessage(usr));
        }

        // archiwalne
        Main.ArchivedSessions.Builder msg2 = Main.ArchivedSessions.newBuilder();

        for (Session s : archivedSessions.findForUser(usr)) {
            msg2.addSession(s.buildMessage(usr));
        }

        // użytkownicy
        Main.UserList.Builder msg3 = Main.UserList.newBuilder();

        for (User usr2 : users.values()) {
            if (!usr.equals(usr2)) {
                msg3.addUser(usr2.buildUserMessage());
            }
        }

        usr.getChannel().write(msg1.build());
        usr.getChannel().write(msg2.build());
        usr.getChannel().write(msg3.build());
        usr.getChannel().flush();
    }

    private void onCreateNewSession(ActiveUser usr, Main.CreateNewSession msg) {
        logger.info("User `" + usr.getName() + "` creating new session");
        Main.NewSessionResponse.Builder response = Main.NewSessionResponse.newBuilder();

        if (msg.getName().isEmpty()) {
            response.setCreated(false);
            response.setError("Nieprawidłowa nazwa");
            return;
        }

        Session s = repo.create(msg.getName(), usr);

        try {
            repo.persist(s);
        } catch (SessionRepositoryException e) {
            response.setCreated(false);
            response.setError("Błąd serwera: " + e.getMessage());
            usr.getChannel().writeAndFlush(response.build());
            return;
        }

        activeSessions.addSession(s);

        logger.info("User `" + usr.getName() + "` created new session: " + s.getName());

        response.setCreated(true);
        usr.getChannel().writeAndFlush(response.build());

        usr.getChannel().writeAndFlush(
                Main.SessionInvite.newBuilder()
                .setSession(s.buildMessage(usr))
                .build()
        );
    }

    private void onUserAutocomplete(ActiveUser usr, Main.UserAutocomplete msg) {
        if (msg.getName().isEmpty() || msg.getName().length() < 1) {
            return;
        }

        Main.UserAutocompleteResponse.Builder response = Main.UserAutocompleteResponse.newBuilder();

        // max 5 użytkowników zaczynających się od podanego ciągu znaków
        users.values().stream()
                .filter(a -> a.getName().startsWith(msg.getName()))
                .filter(a -> !a.equals(usr))
                .limit(5)
                .forEach(a -> response.addUser(a.buildUserMessage()));

        usr.getChannel().writeAndFlush(response.build());
    }

    private void onDownloadSession(ActiveUser usr, Main.DownloadSession msg) {
        Session sess = archivedSessions.findById(msg.getId());

        Main.DownloadSessionAcknowledge.Builder response = Main.DownloadSessionAcknowledge.newBuilder();

        response.setId(msg.getId());
        response.setSuccess(false);

        // not found
        if (sess == null) {
            usr.getChannel().writeAndFlush(response.build());
            return;
        }

        // permissions
        if (!sess.isAllowedToSee(usr)) {
            usr.getChannel().writeAndFlush(response.build());
            return;
        }

        byte[] data = sessionSerializer.serialize(sess);

        if (data == null) {
            usr.getChannel().writeAndFlush(response.build());
            return;
        }

        response.setSuccess(true);
        response.setFilename("session-" + sess.getId() + ".db");

        int blockSize = 8192;

        int blocks = (int) Math.ceil((double) data.length / (double) blockSize);

        response.setBlocks(blocks);
        response.setBlockSize(blockSize);

        usr.getChannel().writeAndFlush(response.build());

        for (int i = 0; i < blocks; i++) {
            Main.DownloadSessionBlock.Builder b = Main.DownloadSessionBlock.newBuilder();

            b.setBlock(i);
            b.setRemaining(blocks - (i + 1));
            b.setId(sess.getId());

            int remainingSize = data.length - blockSize * i;

            b.setData(ByteString.copyFrom(data, blockSize * i, remainingSize));

            usr.getChannel().writeAndFlush(b.build());
        }
    }

    @Override
    public void start() {
        loop.registerConsumer(new FunctionConsumer<>(ChannelInactiveEvent.class, this::onChannelInactive));
        loop.registerConsumer(new FunctionConsumer<>(UserAuthedEvent.class, this::onUserAuthed));

        dispatcher.register(Main.SyncRequest.class, new AuthedMessageFunction<>(this::onSyncRequest));
        dispatcher.register(Main.CreateNewSession.class, new AuthedMessageFunction<>(this::onCreateNewSession));
        dispatcher.register(Main.UserAutocomplete.class, new AuthedMessageFunction<>(this::onUserAutocomplete));
        dispatcher.register(Main.DownloadSession.class, new AuthedMessageFunction<>(this::onDownloadSession));
    }

    @Override
    public void stop() {

    }
}
