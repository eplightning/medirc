package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.session.SessionUserFlag;
import org.eplight.medirc.server.user.User;

import java.util.EnumSet;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class ChangeFlagsSessionEvent extends AbstractSessionEvent {

    private User user;
    private EnumSet<SessionUserFlag> flags;

    public ChangeFlagsSessionEvent(Session session, Object cause, User user, EnumSet<SessionUserFlag> flags) {
        super(session, cause);
        this.user = user;
        this.flags = flags;
    }

    public ChangeFlagsSessionEvent(Session session, User user, EnumSet<SessionUserFlag> flags) {
        super(session);
        this.user = user;
        this.flags = flags;
    }

    public User getUser() {
        return user;
    }

    public EnumSet<SessionUserFlag> getFlags() {
        return flags;
    }
}
