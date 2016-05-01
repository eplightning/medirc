package org.eplight.medirc.server.user;

import io.netty.channel.socket.SocketChannel;
import org.eplight.medirc.protocol.Main;

public class User {

    protected SocketChannel channel;
    protected int id;
    protected String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Main.User buildUserMessage() {
        return Main.User.newBuilder()
                .setId(getId())
                .setName(getName())
                .build();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
