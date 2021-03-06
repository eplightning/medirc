package org.eplight.medirc.client.instance.network.dispatcher;

import com.google.protobuf.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDispatcher {

    private Map<Class, List<DispatchFunction>> routing = new HashMap<>();

    private List<MessageDispatcher> children = new ArrayList<>();

    public void addChildDispatcher(MessageDispatcher dispatcher) {
        children.add(dispatcher);
    }

    public void removeChildDispatcher(MessageDispatcher dispatcher) {
        children.remove(dispatcher);
    }

    @SuppressWarnings("unchecked")
    public void dispatch(Message msg) {
        List<DispatchFunction> functions = routing.get(msg.getClass());

        if (functions != null) {
            for (DispatchFunction func : functions) {
                func.handle(msg);
            }
        }

        children.forEach(a -> a.dispatch(msg));
    }

    public void register(Class<? extends Message> klazz, DispatchFunction func) {
        List<DispatchFunction> list = routing.get(klazz);

        if (list == null) {
            list = new ArrayList<>();
            routing.put(klazz, list);
        }

        list.add(func);
    }

}
