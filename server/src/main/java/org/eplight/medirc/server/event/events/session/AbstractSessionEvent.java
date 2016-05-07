package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.session.Session;

/**
 * Created by EpLightning on 07.05.2016.
 */
abstract public class AbstractSessionEvent implements SessionEvent {

    protected Session session;
    protected Object cause;

    public AbstractSessionEvent(Session session, Object cause) {
        this.session = session;
        this.cause = cause;
    }

    public AbstractSessionEvent(Session session) {
        this.session = session;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public Object getCause() {
        return cause;
    }
}
