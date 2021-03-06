package org.eplight.medirc.server.event.consumers;

import org.eplight.medirc.server.event.events.Event;

/**
 * Created by EpLightning on 30.03.2016.
 */
public abstract class AbstractConsumer<T extends Event> implements Consumer {

    private Class<T> eventClass;

    public AbstractConsumer(Class<T> clazz) {
        this.eventClass = clazz;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void consume(Event e) {
        if (eventClass.isAssignableFrom(e.getClass()))
            handle((T) e);
    }

    abstract public void handle(T e);
}
