package org.eplight.medirc.server.network.main;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

public class MainChannelHandler extends ChannelInboundHandlerAdapter {

    protected SocketChannel channel;

    public MainChannelHandler(SocketChannel socketChannel) {
        channel = socketChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO: Nowy klient
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (!(obj instanceof Message)) {
            // TODO: To chyba nie moze tutaj dojsc?
        }

        Message msg = (Message) obj;
        // TODO: Obsluga wiadomosci
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // TODO: Zamykamy polaczenie
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO: Czyscimy
    }

    public SocketChannel getChannel() {
        return channel;
    }
}
