package org.eplight.medirc.server.event.consumers;

import org.eplight.medirc.server.event.events.Event;

/**
 * Created by EpLightning on 30.03.2016.
 */
public interface Consumer {

    void consume(Event e);
}
