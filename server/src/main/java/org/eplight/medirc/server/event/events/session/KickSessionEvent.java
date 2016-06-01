package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.session.SessionKickReason;
import org.eplight.medirc.server.user.User;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class KickSessionEvent extends AbstractSessionEvent {

    private User kickedUser;
    private SessionKickReason kickReason;

    public KickSessionEvent(Session session, Object cause, User kickedUser, SessionKickReason reason) {
        super(session, cause);
        this.kickedUser = kickedUser;
        this.kickReason = reason;
    }

    public KickSessionEvent(Session session, User kickedUser, SessionKickReason reason) {
        super(session);
        this.kickedUser = kickedUser;
        this.kickReason = reason;
    }

    public User getKickedUser() {
        return kickedUser;
    }

    public SessionKickReason getReason() {
        return kickReason;
    }
}
