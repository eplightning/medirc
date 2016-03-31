package org.eplight.medirc.server.event.events;

import com.google.protobuf.Message;
import io.netty.channel.socket.SocketChannel;
import org.eplight.medirc.server.network.ServerType;

public class MessageEvent implements ServerEvent {

    protected ServerType type;
    protected Message msg;
    protected SocketChannel channel;

    public MessageEvent(ServerType type, Message msg, SocketChannel channel) {
        this.type = type;
        this.msg = msg;
        this.channel = channel;
    }

    @Override
    public ServerType getType() {
        return type;
    }

    public Message getMsg() {
        return msg;
    }

    public SocketChannel getChannel() {
        return channel;
    }
}
