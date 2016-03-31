package org.eplight.medirc.server.network.main;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eplight.medirc.server.event.events.ChannelActiveEvent;
import org.eplight.medirc.server.event.events.ChannelInactiveEvent;
import org.eplight.medirc.server.event.events.MessageEvent;
import org.eplight.medirc.server.event.queue.EventQueue;
import org.eplight.medirc.server.network.ServerType;
import org.eplight.medirc.server.network.SocketAttributes;

public class MainChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(MainChannelHandler.class);

    protected EventQueue ev;

    public MainChannelHandler(EventQueue ev) {
        this.ev = ev;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("New connection in main server from: " + ctx.channel().remoteAddress());
        ctx.channel().attr(SocketAttributes.LAST_ACTIVITY).set(System.currentTimeMillis());

        ev.append(new ChannelActiveEvent(ServerType.MAIN, (SocketChannel) ctx.channel()));
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
        logger.info("Connection lost in main server: " + ctx.channel().remoteAddress());

        String pipelineError = ctx.channel().attr(SocketAttributes.PIPELINE_ERROR).get();

        if (pipelineError != null) {
            logger.info("Pipeline error: " + pipelineError);
        }

        ev.append(new ChannelInactiveEvent(ServerType.MAIN, (SocketChannel) ctx.channel()));
    }
}
