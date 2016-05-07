package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.user.ActiveUser;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class JoinSessionEvent extends AbstractSessionEvent {

    private ActiveUser user;

    public JoinSessionEvent(Session session, ActiveUser user) {
        super(session);
        this.user = user;
    }

    public JoinSessionEvent(Session session, Object cause, ActiveUser user) {
        super(session, cause);
        this.user = user;
    }

    public ActiveUser getUser() {
        return user;
    }
}
