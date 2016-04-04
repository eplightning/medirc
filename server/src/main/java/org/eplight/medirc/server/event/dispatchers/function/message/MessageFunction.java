package org.eplight.medirc.server.event.dispatchers.function.message;

import com.google.protobuf.Message;
import io.netty.channel.socket.SocketChannel;
import org.eplight.medirc.server.event.dispatchers.function.DispatchFunction;
import org.eplight.medirc.server.event.events.MessageEvent;

public class MessageFunction<T extends Message> implements DispatchFunction<MessageEvent> {

    protected Handler<T> handler;

    public interface Handler<T extends Message> {

        void handleMessage(SocketChannel channel, T msg);
    }

    public MessageFunction(Handler<T> handler) {
        this.handler = handler;
    }

    // type need to be checked earlier (for example by MessageDispatcher)
    @SuppressWarnings("unchecked")
    @Override
    public void handleEvent(MessageEvent event) {
        handler.handleMessage(event.getChannel(), (T) event.getMsg());
    }
}
