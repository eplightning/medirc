package org.eplight.medirc.client.instance.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.eplight.medirc.protocol.ConnectionConst;
import org.eplight.medirc.protocol.pipeline.ProtobufDecoder;
import org.eplight.medirc.protocol.pipeline.ProtobufEncoder;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private ClientChannelHandler handler;

    public ClientChannelInitializer(ClientChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(ConnectionConst.MAX_MESSAGE_LENGTH, 0,
                ConnectionConst.SIZE_FIELD_LENGTH, 0, ConnectionConst.SIZE_FIELD_LENGTH))
                .addLast(new ProtobufDecoder())
                .addLast(handler)
                .addLast(new LengthFieldPrepender(ConnectionConst.SIZE_FIELD_LENGTH))
                .addLast(new ProtobufEncoder());
    }
}
