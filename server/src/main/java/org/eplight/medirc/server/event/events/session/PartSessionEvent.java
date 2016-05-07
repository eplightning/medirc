package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.user.ActiveUser;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class PartSessionEvent extends AbstractSessionEvent {

    private String reason;
    private ActiveUser user;

    public PartSessionEvent(Session session, Object cause, String reason, ActiveUser user) {
        super(session, cause);
        this.reason = reason;
        this.user = user;
    }

    public PartSessionEvent(Session session, String reason, ActiveUser user) {
        super(session);
        this.reason = reason;
        this.user = user;
    }

    public String getReason() {
        return reason;
    }

    public ActiveUser getUser() {
        return user;
    }
}
