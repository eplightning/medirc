package org.eplight.medirc.server.event.events;

import io.netty.channel.socket.SocketChannel;
import org.eplight.medirc.server.network.ServerType;

public class ChannelActiveEvent implements ServerEvent {

    protected SocketChannel channel;
    protected ServerType type;

    public ChannelActiveEvent(ServerType type, SocketChannel channel) {
        this.channel = channel;
        this.type = type;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    @Override
    public ServerType getServerType() {
        return type;
    }
}
