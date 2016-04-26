package org.eplight.medirc.client.instance.network.dispatcher;

import com.google.protobuf.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDispatcher {

    private Map<Class, List<DispatchFunction>> routing = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void dispatch(Message msg) {
        List<DispatchFunction> functions = routing.get(msg.getClass());

        if (functions != null) {
            for (DispatchFunction func : functions) {
                func.handle(msg);
            }
        }
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
