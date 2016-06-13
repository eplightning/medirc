package org.eplight.medirc.server.module.autovoice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eplight.medirc.protocol.SessionAuto;
import org.eplight.medirc.protocol.SessionUserFlag;
import org.eplight.medirc.server.event.EventLoop;
import org.eplight.medirc.server.event.consumers.FunctionConsumer;
import org.eplight.medirc.server.event.dispatchers.function.MessageDispatcher;
import org.eplight.medirc.server.event.dispatchers.function.message.AuthedMessageFunction;
import org.eplight.medirc.server.event.events.session.ChangeFlagsSessionEvent;
import org.eplight.medirc.server.event.events.session.JoinSessionEvent;
import org.eplight.medirc.server.event.events.session.PartSessionEvent;
import org.eplight.medirc.server.module.Module;
import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.session.active.ActiveSessionsManager;
import org.eplight.medirc.server.user.ActiveUser;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by EpLightning on 12.06.2016.
 */
public class AutoVoiceModule implements Module {

    private static final Logger logger = LogManager.getLogger(AutoVoiceModule.class);

    @Inject
    private EventLoop loop;

    @Inject
    private MessageDispatcher dispatcher;

    @Inject
    private ActiveSessionsManager activeSessionsManager;

    private HashMap<Integer, ArrayDeque<ActiveUser>> queueMap = new HashMap<>();

    private HashMap<Integer, ActiveUser> activeUsers = new HashMap<>();

    private ArrayDeque<ActiveUser> getQueue(int session) {
        if (!queueMap.containsKey(session))
            queueMap.put(session, new ArrayDeque<>());

        return queueMap.get(session);
    }

    private int getPosition(int session, int id) {
        ArrayDeque<ActiveUser> queue = getQueue(session);

        Iterator<ActiveUser> it = queue.iterator();

        int position = 1;

        while (it.hasNext()) {
            if (it.next().getId() == id) {
                return position;
            }

            position++;
        }

        return 0;
    }

    private void changeFlag(Session session, ActiveUser user, boolean state) {
        EnumSet<SessionUserFlag> flags = session.getFlags(user).clone();

        boolean changed = false;

        if (!state && flags.contains(SessionUserFlag.Voice)) {
            changed = true;
            flags.remove(SessionUserFlag.Voice);
        } else if (state && !flags.contains(SessionUserFlag.Voice)) {
            changed = true;
            flags.add(SessionUserFlag.Voice);
        }

        if (changed)
            loop.fireEvent(new ChangeFlagsSessionEvent(session, this, user, flags));
    }

    private void sendUpdates(ArrayDeque<ActiveUser> queue, Session session) {
        int position = 1;
        int length = queue.size();

        if (!activeUsers.containsKey(session.getId()) && queue.size() > 0) {
            ActiveUser usr = queue.pop();

            if (usr == null)
                return;

            length--;

            SessionAuto.SessionAutoInfo.Builder b = SessionAuto.SessionAutoInfo.newBuilder();

            b.setId(session.getId());
            b.setQueuePosition(0);
            b.setQueueUsers(length);
            b.setState(SessionAuto.SessionAutoState.AutoVoiced);

            activeUsers.put(session.getId(), usr);
            changeFlag(session, usr, true);

            usr.getChannel().writeAndFlush(b.build());
        }

        Iterator<ActiveUser> it = queue.iterator();

        while (it.hasNext()) {
            SessionAuto.SessionAutoInfo.Builder b = SessionAuto.SessionAutoInfo.newBuilder();

            b.setId(session.getId());
            b.setQueuePosition(position);
            b.setQueueUsers(length);
            b.setState(SessionAuto.SessionAutoState.AutoQueued);

            position++;

            it.next().getChannel().writeAndFlush(b.build());
        }
    }

    private void onRequest(ActiveUser user, SessionAuto.SessionAutoRequest msg) {
        Session sess = activeSessionsManager.findById(msg.getId());

        if (sess == null)
            return;

        if (!sess.isAllowedToJoin(user))
            return;

        if (!sess.getActiveUsers().contains(user))
            return;

        if (!sess.getAutoVoice())
            return;

        if (sess.getFlags(user).contains(SessionUserFlag.Voice))
            return;

        ArrayDeque<ActiveUser> queue = getQueue(sess.getId());

        if (queue.contains(user))
            return;

        queue.add(user);

        sendUpdates(queue, sess);
    }

    private void onCancel(ActiveUser user, SessionAuto.SessionAutoCancel msg) {
        Session sess = activeSessionsManager.findById(msg.getId());

        if (sess == null)
            return;

        if (!sess.isAllowedToJoin(user))
            return;

        if (!sess.getActiveUsers().contains(user))
            return;

        if (!sess.getAutoVoice())
            return;

        ArrayDeque<ActiveUser> queue = getQueue(sess.getId());

        if (activeUsers.get(sess.getId()) == user) {
            activeUsers.remove(sess.getId());

            changeFlag(sess, user, false);

            sendUpdates(queue, sess);

            SessionAuto.SessionAutoInfo.Builder b = SessionAuto.SessionAutoInfo.newBuilder();

            b.setId(sess.getId());
            b.setQueuePosition(0);
            b.setQueueUsers(0);
            b.setState(SessionAuto.SessionAutoState.AutoNone);

            user.getChannel().writeAndFlush(b.build());
        } else if (queue.contains(user)) {
            queue.remove(user);

            sendUpdates(queue, sess);
        }
    }

    private void onJoin(JoinSessionEvent ev) {
        Session sess = ev.getSession();

        if (sess.getAutoVoice()) {
            SessionAuto.SessionAutoInfo.Builder b = SessionAuto.SessionAutoInfo.newBuilder();

            b.setId(sess.getId());
            b.setQueuePosition(0);
            b.setQueueUsers(0);
            b.setState(SessionAuto.SessionAutoState.AutoNone);

            ev.getUser().getChannel().writeAndFlush(b.build());
        }
    }

    private void onPart(PartSessionEvent ev) {
        Session sess = ev.getSession();

        if (sess.getAutoVoice()) {
            ArrayDeque<ActiveUser> queue = getQueue(sess.getId());

            if (activeUsers.get(sess.getId()) == ev.getUser()) {
                activeUsers.remove(sess.getId());

                changeFlag(sess, ev.getUser(), false);

                sendUpdates(queue, sess);
            }

            if (queue.contains(ev.getUser())) {
                queue.remove(ev.getUser());

                sendUpdates(queue, sess);
            }
        }
    }

    private void onChangeFlags(ChangeFlagsSessionEvent ev) {
        if (ev.getCause() == this)
            return;

        Session sess = ev.getSession();

        if (sess.getAutoVoice()) {
            ActiveUser user = activeUsers.get(sess.getId());

            if (user != null && user.equals(ev.getUser())) {
                if (!ev.getFlags().contains(SessionUserFlag.Voice)) {
                    activeUsers.remove(sess.getId());

                    sendUpdates(getQueue(sess.getId()), sess);

                    SessionAuto.SessionAutoInfo.Builder b = SessionAuto.SessionAutoInfo.newBuilder();

                    b.setId(sess.getId());
                    b.setQueuePosition(0);
                    b.setQueueUsers(0);
                    b.setState(SessionAuto.SessionAutoState.AutoNone);

                    user.getChannel().writeAndFlush(b.build());
                }
            }
        }
    }

    @Override
    public void start() {
        dispatcher.register(SessionAuto.SessionAutoRequest.class, new AuthedMessageFunction<>(this::onRequest));
        dispatcher.register(SessionAuto.SessionAutoCancel.class, new AuthedMessageFunction<>(this::onCancel));

        loop.registerConsumer(new FunctionConsumer<>(JoinSessionEvent.class, this::onJoin));
        loop.registerConsumer(new FunctionConsumer<>(PartSessionEvent.class, this::onPart));
        loop.registerConsumer(new FunctionConsumer<>(ChangeFlagsSessionEvent.class, this::onChangeFlags));
    }

    @Override
    public void stop() {

    }
}
