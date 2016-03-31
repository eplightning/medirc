package org.eplight.medirc.server.event.dispatchers.function.message;

import com.google.protobuf.Message;
import io.netty.channel.socket.SocketChannel;

public interface MessageHandler<T extends Message> {

    public void handleMessage(SocketChannel channel, T msg);
}
