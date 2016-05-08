package org.eplight.medirc.client.instance.network;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import javafx.application.Platform;
import org.eplight.medirc.client.instance.network.dispatcher.MessageDispatcher;

import java.util.concurrent.atomic.AtomicLong;

public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private SocketChannel channel;
    private Runnable closeHandler;
    private MessageDispatcher dispatcher;
    private AtomicLong lastActivity;

    public ClientChannelHandler(MessageDispatcher dispatcher, Runnable closeHandler, AtomicLong lastActivity) {
        this.closeHandler = closeHandler;
        this.dispatcher = dispatcher;
        this.lastActivity = lastActivity;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Platform.runLater(closeHandler);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (!(obj instanceof Message)) {
            return;
        }

        lastActivity.set(System.currentTimeMillis());

        Platform.runLater(() -> dispatcher.dispatch((Message) obj));
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        channel = (SocketChannel) ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }

    public SocketChannel getChannel() {
        return channel;
    }
}
