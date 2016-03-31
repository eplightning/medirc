package org.eplight.medirc.server.network;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.eplight.medirc.server.config.ConfigurationManager;
import org.eplight.medirc.server.event.queue.EventQueue;
import org.eplight.medirc.server.network.main.MainServer;

public class NetworkManager {

    protected ConfigurationManager config;
    protected EventQueue ev;

    protected EventLoopGroup bossGroup;
    protected EventLoopGroup childGroup;

    protected MainServer main;

    public NetworkManager(ConfigurationManager config, EventQueue ev) {
        this.config = config;
        this.ev = ev;
        bossGroup = new NioEventLoopGroup();
        childGroup = new NioEventLoopGroup();
    }

    public void initServers() throws Exception {
        main = new MainServer(config, ev, bossGroup, childGroup);
    }

    public void stopServers() {
        bossGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
    }

    public MainServer getMain() {
        return main;
    }
}
