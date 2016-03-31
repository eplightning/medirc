package org.eplight.medirc.server.event.dispatchers.function;

import com.google.protobuf.Message;
import org.eplight.medirc.server.event.events.Event;
import org.eplight.medirc.server.event.events.MessageEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by EpLightning on 30.03.2016.
 */
public class MessageDispatcher implements Dispatcher {

    protected HashMap<Class, ArrayList<DispatchFunction>> routing;

    public MessageDispatcher() {
        routing = new HashMap<>();
    }

    public void register(Class<? extends Message> klazz, DispatchFunction func) {
        ArrayList<DispatchFunction> functions = routing.get(klazz);

        if (functions == null) {
            functions = new ArrayList<>();
            routing.put(klazz, functions);
        }

        functions.add(func);
    }

    @Override
    public void dispatch(Event ev) {
        MessageEvent msgEvent = (MessageEvent) ev;

        ArrayList<DispatchFunction> functions = routing.get(msgEvent.getMsg().getClass());

        if (functions != null) {
            for (DispatchFunction func : functions) {
                func.handleEvent(ev);
            }
        }
    }
}
