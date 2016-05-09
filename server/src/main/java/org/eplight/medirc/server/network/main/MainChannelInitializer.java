package org.eplight.medirc.server.network.main;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.eplight.medirc.protocol.pipeline.ProtobufDecoder;
import org.eplight.medirc.protocol.pipeline.ProtobufEncoder;
import org.eplight.medirc.server.event.queue.EventQueue;

import static org.eplight.medirc.protocol.ConnectionConst.MAX_MESSAGE_LENGTH;
import static org.eplight.medirc.protocol.ConnectionConst.SIZE_FIELD_LENGTH;

public class MainChannelInitializer extends ChannelInitializer<SocketChannel> {

    protected EventQueue ev;

    public MainChannelInitializer(EventQueue ev) {
        this.ev = ev;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(MAX_MESSAGE_LENGTH, 0, SIZE_FIELD_LENGTH, 0,
                                         SIZE_FIELD_LENGTH))
                                .addLast(new ProtobufDecoder())
                                .addLast(new MainChannelHandler(this.ev))
                                .addLast(new LengthFieldPrepender(SIZE_FIELD_LENGTH))
                                .addLast(new ProtobufEncoder());
    }
}
