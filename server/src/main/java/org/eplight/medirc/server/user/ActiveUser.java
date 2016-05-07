package org.eplight.medirc.server.user;

import io.netty.channel.socket.SocketChannel;
import org.eplight.medirc.protocol.Main;
import org.eplight.medirc.protocol.SessionBasic;
import org.eplight.medirc.server.session.SessionUserFlag;

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
}
