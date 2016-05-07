package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.server.session.Session;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class SettingsSessionEvent extends AbstractSessionEvent {

    private SessionBasic.SessionData data;

    public SettingsSessionEvent(Session session, Object cause, SessionBasic.SessionData data) {
        super(session, cause);
        this.data = data;
    }

    public SettingsSessionEvent(Session session, SessionBasic.SessionData data) {
        super(session);
        this.data = data;
    }

    public SessionBasic.SessionData getData() {
        return data;
    }
}
