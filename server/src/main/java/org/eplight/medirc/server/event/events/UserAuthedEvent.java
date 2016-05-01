package org.eplight.medirc.server.event.events;

import io.netty.channel.socket.SocketChannel;
import org.eplight.medirc.server.user.User;

public class UserAuthedEvent implements Event {

    private User usr;
    private SocketChannel channel;

    public UserAuthedEvent(User usr, SocketChannel channel) {
        this.usr = usr;
        this.channel = channel;
    }

    public User getUser() {
        return usr;
    }

    public SocketChannel getChannel() {
        return channel;
    }
}
