package org.eplight.medirc.server.event.dispatchers.function.message;

import com.google.protobuf.Message;
import org.eplight.medirc.server.event.dispatchers.function.DispatchFunction;
import org.eplight.medirc.server.event.events.MessageEvent;

public class MessageFunction<T extends Message> implements DispatchFunction<MessageEvent> {

    protected MessageHandler<T> handler;

    public MessageFunction(MessageHandler<T> handler) {
        this.handler = handler;
    }

    // type need to be checked earlier (for example by MessageDispatcher)
    @SuppressWarnings("unchecked")
    @Override
    public void handleEvent(MessageEvent event) {
        handler.handleMessage(event.getChannel(), (T) event.getMsg());
    }
}
