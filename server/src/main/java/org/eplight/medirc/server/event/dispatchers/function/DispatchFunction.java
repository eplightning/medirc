package org.eplight.medirc.server.event.dispatchers.function;

import org.eplight.medirc.server.event.events.Event;

public interface DispatchFunction<T extends Event> {

    void handleEvent(T event);
}
