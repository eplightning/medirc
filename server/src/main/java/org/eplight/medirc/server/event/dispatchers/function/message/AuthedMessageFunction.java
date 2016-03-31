package org.eplight.medirc.server.event.dispatchers.function.message;

import com.google.protobuf.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eplight.medirc.server.event.dispatchers.function.DispatchFunction;
import org.eplight.medirc.server.event.events.MessageEvent;
import org.eplight.medirc.server.network.SocketAttributes;
import org.eplight.medirc.server.user.User;

public class AuthedMessageFunction<T extends Message> implements DispatchFunction<MessageEvent> {

    private static final Logger logger = LogManager.getLogger(AuthedMessageHandler.class);

    protected AuthedMessageHandler<T> handler;

    public AuthedMessageFunction(AuthedMessageHandler<T> handler) {
        this.handler = handler;
    }

    // type need to be checked earlier (for example by MessageDispatcher)
    @SuppressWarnings("unchecked")
    @Override
    public void handleEvent(MessageEvent event) {
        User usr = event.getChannel().attr(SocketAttributes.USER_OBJECT).get();

        if (usr == null) {
            logger.info("Unauthenticated client sent message requiring authentication");
            return;
        }

        handler.handleMessage(usr, (T) event.getMsg());
    }
}
