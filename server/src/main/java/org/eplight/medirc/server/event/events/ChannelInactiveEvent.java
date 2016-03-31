package org.eplight.medirc.server.event.events;

import io.netty.channel.socket.SocketChannel;
import org.eplight.medirc.server.network.ServerType;

public class ChannelInactiveEvent implements ServerEvent {

    protected ServerType type;
    protected SocketChannel channel;

    public ChannelInactiveEvent(ServerType type, SocketChannel channel) {
        this.type = type;
        this.channel = channel;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    @Override
    public ServerType getType() {
        return null;
    }
}
