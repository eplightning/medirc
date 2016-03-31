package org.eplight.medirc.server.event.queue;

import org.eplight.medirc.server.event.events.Event;

/**
 * Created by EpLightning on 30.03.2016.
 */
public interface EventQueue {
    void append(Event e);

    Event next();
}
