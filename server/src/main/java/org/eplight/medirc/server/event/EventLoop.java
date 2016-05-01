package org.eplight.medirc.server.event;

import org.eplight.medirc.server.event.consumers.Consumer;
import org.eplight.medirc.server.event.events.Event;
import org.eplight.medirc.server.event.queue.EventQueue;

import java.util.ArrayList;

/**
 * Created by EpLightning on 30.03.2016.
 */
public class EventLoop {

    protected EventQueue queue;
    protected ArrayList<Consumer> consumers;

    public EventLoop(EventQueue queue) {
        this.queue = queue;
        this.consumers = new ArrayList<>();
    }

    public void registerConsumer(Consumer con) {
        consumers.add(con);
    }

    public EventQueue getQueue() {
        return queue;
    }

    public void run() {
        Event ev;

        while ((ev = this.queue.next()) != null) {
            fireEvent(ev);
        }
    }

    public void fireEvent(Event ev) {
        for (Consumer con : consumers) {
            con.consume(ev);
        }
    }
}
