package org.eplight.medirc.server.user;

import io.netty.channel.socket.SocketChannel;
import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.protocol.SessionUserFlag;

import java.util.EnumSet;

public class ActiveUser implements User {

    protected SocketChannel channel;
    protected User user;

    public ActiveUser(SocketChannel channel, User user) {
        this.channel = channel;
        this.user = user;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    @Override
    public Main.User buildUserMessage() {
        return user.buildUserMessage();
    }

    @Override
    public SessionBasic.SessionUser buildSessionUserMessage(EnumSet<SessionUserFlag> flags) {
        return user.buildSessionUserMessage(flags);
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public void setName(String name) {
        user.setName(name);
    }

    @Override
    public int getId() {
        return user.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof User)) return false;

        User user = (User) o;

        return getId() == user.getId();
    }
}
