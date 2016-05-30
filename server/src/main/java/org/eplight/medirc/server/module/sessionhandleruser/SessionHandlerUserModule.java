package org.eplight.medirc.server.module.sessionhandleruser;

import com.google.protobuf.MessageOrBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.protocol.SessionEvents;
import org.eplight.medirc.server.event.EventLoop;
import org.eplight.medirc.server.event.consumers.FunctionConsumer;
import org.eplight.medirc.server.event.events.session.*;
import org.eplight.medirc.server.image.Image;
import org.eplight.medirc.server.module.Module;
import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.session.SessionState;
import org.eplight.medirc.server.session.active.ActiveSessionsManager;
import org.eplight.medirc.server.user.ActiveUser;
import org.eplight.medirc.server.user.User;
import org.eplight.medirc.server.user.Users;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class SessionHandlerUserModule implements Module {

    private static final Logger logger = LogManager.getLogger(SessionHandlerUserModule.class);

    @Inject
    private EventLoop loop;

    @Inject
    private Users users;

    @Inject
    private ActiveSessionsManager activeSessionsManager;

    private void onChangeFlags(ChangeFlagsSessionEvent ev) {
        Session sess = ev.getSession();
        User user = ev.getUser();

        logger.info("Changing flags for user `" + user.getName() +"` in `" + sess.getName() + "`");

        sess.setFlags(user, ev.getFlags());

        SessionEvents.UserUpdated.Builder msg = SessionEvents.UserUpdated.newBuilder()
                .setSessionId(sess.getId())
                .setUser(user.buildSessionUserMessage(ev.getFlags()));

        sess.broadcast(msg.build());
    }

    private void onInvite(InviteSessionEvent ev) {
        Session sess = ev.getSession();
        User invited = ev.getInvitedUser();

        logger.info("Inviting user `" + invited.getName() +"` to `" + sess.getName() + "`");

        // TODO: Prawdziwy system zaproszeń
        sess.invite(invited);

        SessionEvents.NewParticipant.Builder msg = SessionEvents.NewParticipant.newBuilder()
                .setSessionId(sess.getId())
                .setUser(invited.buildSessionUserMessage(sess.getFlags(invited)));

        sess.broadcast(msg.build());

        // sesja aktywna?
        if (sess.getState() == SessionState.Started) {
            // aktywny?
            if (users.containsKey(invited.getId())) {
                ActiveUser activeInvited = users.get(invited.getId());

                Main.SessionInvite.Builder msg2 = Main.SessionInvite.newBuilder()
                        .setSession(sess.buildMessage(activeInvited));

                activeInvited.getChannel().writeAndFlush(msg2.build());
            }
        }
    }

    private void onJoin(JoinSessionEvent ev) {
        Session sess = ev.getSession();
        ActiveUser user = ev.getUser();

        logger.info("User `" + user.getName() +"` joining `" + sess.getName() + "`");

        sess.join(user);

        SessionEvents.Joined.Builder msg = SessionEvents.Joined.newBuilder()
                .setSessionId(sess.getId())
                .setUser(user.buildSessionUserMessage(sess.getFlags(user)));

        sess.broadcast(msg.build());

        sess.getActiveUsers().forEach(u -> {
            u.getChannel().writeAndFlush(Main.SessionUpdated.newBuilder().setSession(sess.buildMessage(u)));
        });
    }

    private void onKick(KickSessionEvent ev) {
        Session sess = ev.getSession();
        User user = ev.getKickedUser();

        logger.info("Kicking `" + user.getName() +"` from `" + sess.getName() + "`");

        sess.kick(user);

        SessionEvents.Kicked.Builder msg = SessionEvents.Kicked.newBuilder()
                .setSessionId(sess.getId())
                .setUser(user.buildSessionUserMessage(sess.getFlags(user)));

        sess.broadcast(msg.build());

        // aktywny?
        if (users.containsKey(user.getId())) {
            ActiveUser activeKicked = users.get(user.getId());

            Main.SessionKicked.Builder msg2 = Main.SessionKicked.newBuilder()
                    .setSession(sess.buildMessage(activeKicked));

            activeKicked.getChannel().writeAndFlush(msg2.build());

            loop.fireEvent(new PartSessionEvent(sess, ev.getCause(), "Wyrzucony", activeKicked));
        }
    }

    private void onMessage(MessageSessionEvent ev) {
        Session sess = ev.getSession();
        User user = ev.getUser();
        String text = ev.getText();

        logger.info("Message from `" + user.getName() +"` in `" + sess.getName() + "`");

        MessageOrBuilder msg;

        if (user == null) {
            msg = SessionEvents.ServerMessage.newBuilder()
                    .setSessionId(sess.getId())
                    .setText(text)
                    .build();
        } else {
            msg = SessionEvents.UserMessage.newBuilder()
                    .setSessionId(sess.getId())
                    .setUser(user.buildSessionUserMessage(sess.getFlags(user)))
                    .setText(text)
                    .build();
        }

        sess.broadcast(msg);
    }

    private void onPart(PartSessionEvent ev) {
        Session sess = ev.getSession();
        ActiveUser user = ev.getUser();
        String reason = ev.getReason();

        logger.info("User `" + user.getName() +"` parting `" + sess.getName() + "`");

        sess.part(user);

        SessionEvents.Parted.Builder msg = SessionEvents.Parted.newBuilder()
                .setSessionId(sess.getId())
                .setReason(reason)
                .setUser(user.buildSessionUserMessage(sess.getFlags(user)));

        sess.broadcast(msg.build());

        sess.getActiveUsers().forEach(u -> {
            u.getChannel().writeAndFlush(Main.SessionUpdated.newBuilder().setSession(sess.buildMessage(u)));
        });

        // don't send it to kicked person
        if (sess.isAllowedToJoin(user))
            user.getChannel().writeAndFlush(Main.SessionUpdated.newBuilder().setSession(sess.buildMessage(user)));
    }

    private void onSettings(SettingsSessionEvent ev) {
        Session sess = ev.getSession();
        SessionBasic.SessionData data = ev.getData();

        logger.info("Session configuration change: `" + sess.getName() + "`");

        boolean changes = false;
        SessionState oldState = sess.getState();

        if (!sess.getName().equals(data.getName())) {
            changes = true;
            sess.setName(data.getName());
        }

        if (!sess.getState().toProtobuf().equals(data.getState())) {
            changes = true;
            sess.setState(SessionState.fromProtobuf(data.getState()));
        }

        if (!changes)
            return;

        SessionEvents.SettingsChanged.Builder msg = SessionEvents.SettingsChanged.newBuilder()
                .setSessionId(sess.getId())
                .setData(sess.buildDataMessage());

        sess.broadcast(msg);

        // session list update for participants
        Set<ActiveUser> affectedUsers = users.values().stream()
                .filter(a -> sess.getParticipants().contains(a))
                .collect(Collectors.toSet());

        if (oldState == SessionState.SettingUp && sess.getState() == SessionState.Started) {
            for (ActiveUser u : affectedUsers) {
                u.getChannel().writeAndFlush(
                        Main.SessionInvite.newBuilder()
                        .setSession(sess.buildMessage(u))
                        .build()
                );
            }
        } else {
            for (ActiveUser u : affectedUsers) {
                u.getChannel().writeAndFlush(
                        Main.SessionUpdated.newBuilder()
                        .setSession(sess.buildMessage(u))
                        .build()
                );
            }
        }

        // session list update for owner
        ActiveUser owner = users.get(sess.getOwner().getId());

        if (owner != null) {
            owner.getChannel().writeAndFlush(
                    Main.SessionUpdated.newBuilder()
                    .setSession(sess.buildMessage(owner))
                    .build()
            );
        }

        // TODO: Handle moving active session to archived sessions container
        if (oldState == SessionState.Started && sess.getState() == SessionState.Finished) {
            activeSessionsManager.removeSession(sess.getId());
        }
    }

    private void onUploadImage(UploadImageSessionEvent ev) {
        Session sess = ev.getSession();
        Image img = ev.getImg();

        logger.info("Image uploaded: `" + sess.getName() + "`");

        SessionEvents.ImageAdded msg = SessionEvents.ImageAdded.newBuilder()
                .setSessionId(sess.getId())
                .setName(img.getName())
                .setId(img.getId())
                .setColorG(img.getColor().getG())
                .setColorB(img.getColor().getB())
                .setColorR(img.getColor().getR())
                .build();

        sess.broadcast(msg);
    }

    private void onRemoveImage(RemoveImageSessionEvent ev) {
        Session sess = ev.getSession();
        Image img = ev.getImg();

        logger.info("Image removed: `" + sess.getName() + "`");

        SessionEvents.ImageRemoved msg = SessionEvents.ImageRemoved.newBuilder()
                .setSessionId(sess.getId())
                .setId(img.getId())
                .build();

        sess.broadcast(msg);
    }

    private void onTransformImage(TransformImageSessionEvent ev) {
        Session sess = ev.getSession();
        Image img = ev.getImg();

        logger.info("Image transformed: `" + sess.getName() + "`");

        img.setTransformations(ev.getTransformations());

        SessionEvents.ImageTransformed msg = SessionEvents.ImageTransformed.newBuilder()
                .setSessionId(sess.getId())
                .setId(img.getId())
                .setTransformations(img.getTransformations().toProtobuf())
                .build();

        sess.broadcast(msg);
    }

    private void onAddImageFragment(AddImageFragmentSessionEvent ev) {
        Session sess = ev.getSession();
        Image img = ev.getImg();

        logger.info("Added image fragment: `" + sess.getName() + "`");

        img.getFragments().add(ev.getImgFragment());

        SessionEvents.ImageFragmentsChanged.Builder msg = SessionEvents.ImageFragmentsChanged.newBuilder();
        msg.setId(img.getId()).setSessionId(sess.getId());

        img.getFragments().forEach(f -> msg.addFragment(f.toProtobuf()));

        sess.broadcast(msg.build());
    }

    private void onFocusImage(FocusImageSessionEvent ev) {
        Session sess = ev.getSession();
        Image img = ev.getImg();

        logger.info("Image focused: `" + sess.getName() + "`");

        sess.broadcast(SessionEvents.ImageFocus
                .newBuilder().setId(img.getId()).setSessionId(sess.getId()).build());
    }

    @Override
    public void start() {
        loop.registerConsumer(new FunctionConsumer<>(ChangeFlagsSessionEvent.class, this::onChangeFlags));
        loop.registerConsumer(new FunctionConsumer<>(InviteSessionEvent.class, this::onInvite));
        loop.registerConsumer(new FunctionConsumer<>(JoinSessionEvent.class, this::onJoin));
        loop.registerConsumer(new FunctionConsumer<>(KickSessionEvent.class, this::onKick));
        loop.registerConsumer(new FunctionConsumer<>(MessageSessionEvent.class, this::onMessage));
        loop.registerConsumer(new FunctionConsumer<>(PartSessionEvent.class, this::onPart));
        loop.registerConsumer(new FunctionConsumer<>(SettingsSessionEvent.class, this::onSettings));
        loop.registerConsumer(new FunctionConsumer<>(UploadImageSessionEvent.class, this::onUploadImage));
        loop.registerConsumer(new FunctionConsumer<>(RemoveImageSessionEvent.class, this::onRemoveImage));
        loop.registerConsumer(new FunctionConsumer<>(TransformImageSessionEvent.class, this::onTransformImage));
        loop.registerConsumer(new FunctionConsumer<>(AddImageFragmentSessionEvent.class, this::onAddImageFragment));
        loop.registerConsumer(new FunctionConsumer<>(FocusImageSessionEvent.class, this::onFocusImage));
    }

    @Override
    public void stop() {

    }
}
