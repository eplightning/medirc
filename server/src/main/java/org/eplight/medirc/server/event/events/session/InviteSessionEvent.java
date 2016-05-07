package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.user.User;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class InviteSessionEvent extends AbstractSessionEvent {

    private User invitedUser;

    public InviteSessionEvent(Session session, Object cause, User invitedUser) {
        super(session, cause);
        this.invitedUser = invitedUser;
    }

    public InviteSessionEvent(Session session, User invitedUser) {
        super(session);
        this.invitedUser = invitedUser;
    }

    public User getInvitedUser() {
        return invitedUser;
    }
}
