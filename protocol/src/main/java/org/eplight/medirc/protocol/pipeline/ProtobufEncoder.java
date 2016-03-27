package org.eplight.medirc.protocol.pipeline;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.eplight.medirc.protocol.MessageFactory;

public class ProtobufEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        short id = MessageFactory.getId(message.getClass());

        if (id == 0)
            return;

        byteBuf.writeShort(id)
               .writeBytes(message.toByteArray());
    }
}
