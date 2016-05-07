package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.user.User;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class KickSessionEvent extends AbstractSessionEvent {

    private User kickedUser;

    public KickSessionEvent(Session session, Object cause, User kickedUser) {
        super(session, cause);
        this.kickedUser = kickedUser;
    }

    public KickSessionEvent(Session session, User kickedUser) {
        super(session);
        this.kickedUser = kickedUser;
    }

    public User getKickedUser() {
        return kickedUser;
    }
}
