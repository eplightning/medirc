package org.eplight.medirc.server.module.home;

import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.server.event.EventLoop;
import org.eplight.medirc.server.event.consumers.FunctionConsumer;
import org.eplight.medirc.server.event.dispatchers.function.MessageDispatcher;
import org.eplight.medirc.server.event.dispatchers.function.message.AuthedMessageFunction;
import org.eplight.medirc.server.event.events.ChannelInactiveEvent;
import org.eplight.medirc.server.event.events.UserAuthedEvent;
import org.eplight.medirc.server.module.Module;
import org.eplight.medirc.server.network.SocketAttributes;
import org.eplight.medirc.server.session.active.ActiveSessionsManager;
import org.eplight.medirc.server.user.User;
import org.eplight.medirc.server.user.Users;

import javax.inject.Inject;

public class HomeModule implements Module {

    @Inject
    protected ActiveSessionsManager activeSessions;

    @Inject
    protected EventLoop loop;

    @Inject
    protected MessageDispatcher dispatcher;

    @Inject
    protected Users users;

    public void onChannelInactive(ChannelInactiveEvent event) {
        User usr = event.getChannel().attr(SocketAttributes.USER_OBJECT).get();

        if (usr == null) {
            return;
        }

        // budowanie wiadomości ..
        Main.UserDisconnected msg = Main.UserDisconnected.newBuilder().setUser(usr.buildUserMessage()).build();

        // wysyłamy do każdego oprócz naszego subiekta
        users.broadcast(msg, usr);
    }

    public void onUserAuthed(UserAuthedEvent ev) {
        // budowanie wiadomości ..
        Main.UserConnected msg = Main.UserConnected.newBuilder().setUser(ev.getUser().buildUserMessage()).build();

        // wysyłamy do każdego oprócz naszego subiekta
        users.broadcast(msg, ev.getUser());
    }

    public void onSyncRequest(User usr, Main.SyncRequest msg) {
        // TODO: Active sessions
        // TODO: Archived sessions

        // użytkownicy
        Main.UserList.Builder msg3 = Main.UserList.newBuilder();

        for (User usr2 : users.values()) {
            if (!usr.equals(usr2)) {
                msg3.addUser(usr2.buildUserMessage());
            }
        }

        usr.getChannel().write(msg3.build());
        usr.getChannel().flush();
    }

    public void onCreateNewSession(User usr, Main.CreateNewSession msg) {
        // TODO: Do it
    }

    @Override
    public void start() {
        loop.registerConsumer(new FunctionConsumer<>(ChannelInactiveEvent.class, this::onChannelInactive));
        loop.registerConsumer(new FunctionConsumer<>(UserAuthedEvent.class, this::onUserAuthed));

        dispatcher.register(Main.SyncRequest.class, new AuthedMessageFunction<>(this::onSyncRequest));
        dispatcher.register(Main.CreateNewSession.class, new AuthedMessageFunction<>(this::onCreateNewSession));
    }

    @Override
    public void stop() {

    }
}
