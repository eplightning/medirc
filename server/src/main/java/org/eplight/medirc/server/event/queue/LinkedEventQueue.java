package org.eplight.medirc.server.event.queue;

import org.eplight.medirc.server.event.events.Event;
import org.eplight.medirc.server.event.queue.EventQueue;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by EpLightning on 30.03.2016.
 */
public class LinkedEventQueue implements EventQueue {

    protected LinkedBlockingQueue<Event> queue;

    public LinkedEventQueue() {
        queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void append(Event e) {
        queue.add(e);
    }

    @Override
    public Event next() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }
}
