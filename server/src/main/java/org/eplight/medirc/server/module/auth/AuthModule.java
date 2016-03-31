package org.eplight.medirc.server.module.auth;

import io.netty.channel.socket.SocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eplight.medirc.protocol.Basic;
import org.eplight.medirc.server.event.EventLoop;
import org.eplight.medirc.server.event.consumers.AbstractConsumer;
import org.eplight.medirc.server.event.dispatchers.function.MessageDispatcher;
import org.eplight.medirc.server.event.dispatchers.function.message.MessageFunction;
import org.eplight.medirc.server.event.events.ChannelInactiveEvent;
import org.eplight.medirc.server.module.Module;
import org.eplight.medirc.server.network.SocketAttributes;
import org.eplight.medirc.server.user.User;
import org.eplight.medirc.server.user.auth.Authentication;
import org.eplight.medirc.server.user.auth.HardcodedAuthentication;
import org.eplight.medirc.server.user.factory.HardcodedUserFactory;
import org.eplight.medirc.server.user.factory.UserFactory;

import java.util.Map;

public class AuthModule implements Module {

    private static final Logger logger = LogManager.getLogger(AuthModule.class);

    protected EventLoop loop;
    protected MessageDispatcher dispatcher;
    protected Map<Integer, User> users;
    protected Authentication authenticator;
    protected UserFactory userFactory;

    public AuthModule(EventLoop loop, MessageDispatcher messages, Map<Integer, User> users) {
        this.loop = loop;
        this.dispatcher = messages;
        this.users = users;

        userFactory = new HardcodedUserFactory();
        authenticator = new HardcodedAuthentication();

        loop.registerConsumer(new AbstractConsumer<ChannelInactiveEvent>(ChannelInactiveEvent.class) {
            @Override
            public void handle(ChannelInactiveEvent e) {
                onChannelInactive(e);
            }
        });

        messages.register(Basic.Handshake.class, new MessageFunction<>(this::onHandshake));
    }

    public void onChannelInactive(ChannelInactiveEvent event) {
        User usr = event.getChannel().attr(SocketAttributes.USER_OBJECT).get();

        if (usr == null) {
            return;
        }

        logger.info("Logging out user: " + usr.getName());

        users.remove(usr.getId());
    }

    public void onHandshake(SocketChannel channel, Basic.Handshake msg) {
        Basic.HandshakeAck.Builder ack = Basic.HandshakeAck.newBuilder();
        ack.setSuccess(false);

        Attribute<User> attribute = channel.attr(SocketAttributes.USER_OBJECT);

        if (attribute.get() != null) {
            ack.setErrorMessage("Authenticated user sent handshake request ..?");
            channel.writeAndFlush(ack.build());
            logger.error("Authenticated user sent handshake request ..?");
            return;
        }

        int id = authenticator.authenticate(msg);

        if (id == 0) {
            ack.setErrorMessage("Invalid credentials");
            channel.writeAndFlush(ack.build());
            logger.error("Authentication error, invalid credentials");
            return;
        }

        User usr = userFactory.create(id);

        if (usr == null) {
            ack.setErrorMessage("Internal server error");
            channel.writeAndFlush(ack.build());
            logger.error("Couldn't find user with ID " + id);
            return;
        }

        attribute.set(usr);
        users.put(id, usr);

        ack.setSuccess(true);
        ack.setName(usr.getName());
        channel.writeAndFlush(ack.build());

        logger.info("User successfully logged in: " + usr.getName());
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
