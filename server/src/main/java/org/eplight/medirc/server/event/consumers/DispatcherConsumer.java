package org.eplight.medirc.server.event.consumers;

import org.eplight.medirc.server.event.dispatchers.function.Dispatcher;
import org.eplight.medirc.server.event.events.Event;

/**
 * Created by EpLightning on 30.03.2016.
 */
public class DispatcherConsumer<T extends Event> extends AbstractConsumer<T> {

    protected Dispatcher dispatcher;

    public DispatcherConsumer(Dispatcher dispatcher, Class<T> clazz) {
        super(clazz);
        this.dispatcher = dispatcher;
    }

    @Override
    public void handle(T e) {
        dispatcher.dispatch(e);
    }
}
