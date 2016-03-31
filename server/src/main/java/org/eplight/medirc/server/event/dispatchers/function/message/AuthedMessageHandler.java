package org.eplight.medirc.server.event.dispatchers.function.message;

import com.google.protobuf.Message;
import org.eplight.medirc.server.user.User;

public interface AuthedMessageHandler<T extends Message> {

    public void handleMessage(User user, T msg);
}
