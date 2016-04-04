package org.eplight.medirc.protocol.pipeline;

import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.eplight.medirc.protocol.MessageFactory;

public class ProtobufEncoder extends MessageToByteEncoder<MessageOrBuilder> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageOrBuilder input, ByteBuf byteBuf) throws Exception {
        Message message;

        if (input instanceof Message) {
            message = (Message) input;
        } else if (input instanceof Message.Builder) {
            message = ((Message.Builder) input).build();
        } else {
            // ??
            throw new Exception("This shouldn\'t happen");
        }

        short id = MessageFactory.getId(message.getClass());

        if (id == 0)
            return;

        byteBuf.writeShort(id)
               .writeBytes(message.toByteArray());
    }
}
