package org.eplight.medirc.server.network;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.eplight.medirc.server.config.ConfigurationManager;
import org.eplight.medirc.server.network.main.MainServer;

public class NetworkManager {

    protected ConfigurationManager config;

    protected EventLoopGroup bossGroup;
    protected EventLoopGroup childGroup;

    protected MainServer main;

    public NetworkManager(ConfigurationManager config) {
        this.config = config;
        bossGroup = new NioEventLoopGroup();
        childGroup = new NioEventLoopGroup();
    }

    public void initServers() throws Exception {
        main = new MainServer(config, bossGroup, childGroup);
    }

    public void stopServers() {
        bossGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
    }

    public MainServer getMain() {
        return main;
    }
}
