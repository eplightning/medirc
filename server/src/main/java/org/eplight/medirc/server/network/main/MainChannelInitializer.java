package org.eplight.medirc.server.network.main;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.eplight.medirc.protocol.pipeline.ProtobufDecoder;

public class MainChannelInitializer extends ChannelInitializer<SocketChannel> {

    public final static int MAX_MESSAGE_LENGTH = 128 * 1024;
    public final static int SIZE_FIELD_LENGTH = 4;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(MAX_MESSAGE_LENGTH, 0, SIZE_FIELD_LENGTH, 0,
                                         SIZE_FIELD_LENGTH))
                                .addLast(new ProtobufDecoder())
                                .addLast(new MainChannelHandler(socketChannel))
                                .addLast(new LengthFieldPrepender(SIZE_FIELD_LENGTH))
                                .addLast(new ProtobufEncoder());
    }
}
