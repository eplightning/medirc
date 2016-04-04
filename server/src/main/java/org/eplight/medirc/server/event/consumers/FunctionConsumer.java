package org.eplight.medirc.server.event.consumers;

import org.eplight.medirc.server.event.events.Event;

public class FunctionConsumer<T extends Event> extends AbstractConsumer<T> {

    private Function<T> func;

    public interface Function<T> {
        void handle(T e);
    }

    public FunctionConsumer(Class<T> klazz, Function<T> func) {
        super(klazz);
        this.func = func;
    }

    @Override
    public void handle(T e) {
        func.handle(e);
    }
}
