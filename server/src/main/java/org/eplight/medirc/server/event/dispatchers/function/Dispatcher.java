package org.eplight.medirc.server.event.dispatchers.function;

import org.eplight.medirc.server.event.events.Event;

/**
 * Created by EpLightning on 30.03.2016.
 */
public interface Dispatcher {

    void dispatch(Event ev);
}
