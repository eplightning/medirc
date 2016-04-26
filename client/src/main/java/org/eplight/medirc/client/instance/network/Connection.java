package org.eplight.medirc.client.instance.network;

import com.google.protobuf.MessageOrBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.application.Platform;
import org.eplight.medirc.client.instance.network.dispatcher.MessageDispatcher;
import org.eplight.medirc.protocol.Basic;

import java.util.concurrent.atomic.AtomicLong;

public class Connection {

    private ErrorFunction error;
    private SuccessFunction success;
    private String username;
    private String password;
    private String address;
    private int port;

    private EventLoopGroup group;
    private ClientChannelHandler handler;
    private AtomicLong lastActivity;

    public Connection(ErrorFunction error, SuccessFunction success, String username,
                      String password, String address, int port) {
        this.error = error;
        this.success = success;
        this.username = username;
        this.password = password;
        this.address = address;
        this.port = port;

        group = new NioEventLoopGroup();
        lastActivity = new AtomicLong();
    }

    public void connect(MessageDispatcher dispatcher, Runnable closeHandler) {
        Bootstrap b = new Bootstrap();

        handler = new ClientChannelHandler(dispatcher, closeHandler, lastActivity);

        b.group(group);
        b.channel(NioSocketChannel.class);
        b.handler(new ClientChannelInitializer(handler));

        ChannelFuture future = b.connect(address, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    Platform.runLater(() -> error.error(channelFuture.cause().getMessage()));
                } else {
                    Basic.Handshake.Builder msg = Basic.Handshake.newBuilder();
                    msg.getSimpleBuilder().setUsername(username)
                                          .setPassword(password);

                    channelFuture.channel().writeAndFlush(msg.build());
                }
            }
        });
    }

    public void clearCredentials() {
        // TODO: Wyczyścić password i username ze względów bezpieczeństwa
    }

    public SocketChannel getChannel() {
        return handler.getChannel();
    }

    public void shutdown() {
        group.shutdownGracefully();
    }

    public ChannelFuture writeAndFlush(MessageOrBuilder msg) {
        return handler.getChannel().writeAndFlush(msg);
    }

    public interface ErrorFunction {
        void error(String error);
    }

    public interface SuccessFunction {
        void success();
    }
}
