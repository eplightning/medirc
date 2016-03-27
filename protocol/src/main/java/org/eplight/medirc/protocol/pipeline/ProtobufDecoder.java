package org.eplight.medirc.protocol.pipeline;

import com.google.protobuf.Parser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.eplight.medirc.protocol.MessageFactory;

import java.util.List;

public class ProtobufDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 2)
            throw new Exception("Client's message is too short");

        short id = byteBuf.readShort();
        Parser parser = MessageFactory.getParser(id);

        if (parser == null)
            throw new Exception("Unknown protobuf message ID");

        int length = byteBuf.readableBytes();
        int offset;
        byte[] array;

        if (byteBuf.hasArray()) {
            array = byteBuf.array();
            offset = byteBuf.arrayOffset() + byteBuf.readerIndex();
        } else {
            array = new byte[length];
            byteBuf.readBytes(array, 0, length);
            offset = 0;
        }

        list.add(parser.parseFrom(array, offset, length));
    }
}
