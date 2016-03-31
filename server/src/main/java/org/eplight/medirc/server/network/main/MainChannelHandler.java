package org.eplight.medirc.server.network.main;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import org.eplight.medirc.server.event.events.MessageEvent;
import org.eplight.medirc.server.event.queue.EventQueue;
import org.eplight.medirc.server.network.ServerType;
import org.eplight.medirc.server.network.SocketAttributes;

public class MainChannelHandler extends ChannelInboundHandlerAdapter {

    protected EventQueue ev;

    public MainChannelHandler(EventQueue ev) {
        this.ev = ev;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().attr(SocketAttributes.LAST_ACTIVITY).set(System.currentTimeMillis());

        // TODO: EventChannelActive
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (!(obj instanceof Message)) {
            return;
        }

        ctx.channel().attr(SocketAttributes.LAST_ACTIVITY).set(System.currentTimeMillis());

        Message msg = (Message) obj;

        ev.append(new MessageEvent(ServerType.MAIN, msg, (SocketChannel) ctx.channel()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().attr(SocketAttributes.PIPELINE_ERROR).setIfAbsent(cause.toString());
        ctx.channel().close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO: EventChannelClosed
    }
}
