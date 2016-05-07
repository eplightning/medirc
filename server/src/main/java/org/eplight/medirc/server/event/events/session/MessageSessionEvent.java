package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.session.Session;
import org.eplight.medirc.server.user.User;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class MessageSessionEvent extends AbstractSessionEvent {

    private User user;
    private String text;

    public MessageSessionEvent(Session session, Object cause, User user, String text) {
        super(session, cause);
        this.user = user;
        this.text = text;
    }

    public MessageSessionEvent(Session session, User user, String text) {
        super(session);
        this.user = user;
        this.text = text;
    }

    public MessageSessionEvent(Session session, Object cause, String text) {
        super(session, cause);
        this.text = text;
    }

    public MessageSessionEvent(Session session, String text) {
        super(session);
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }
}
