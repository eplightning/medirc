package org.eplight.medirc.server.event.events.session;

import org.eplight.medirc.server.event.events.Event;
import org.eplight.medirc.server.session.Session;

/**
 * Created by EpLightning on 07.05.2016.
 */
public interface SessionEvent extends Event {

    Session getSession();
    Object getCause();
}
