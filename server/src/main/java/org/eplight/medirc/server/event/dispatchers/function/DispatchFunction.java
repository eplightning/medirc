package org.eplight.medirc.server.event.dispatchers.function;

import org.eplight.medirc.server.event.events.Event;

public interface DispatchFunction<T extends Event> {

    public void handleEvent(T event);
}
