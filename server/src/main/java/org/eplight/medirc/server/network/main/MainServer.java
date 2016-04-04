package org.eplight.medirc.server.network.main;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eplight.medirc.server.config.ConfigurationManager;
import org.eplight.medirc.server.event.queue.EventQueue;

public class MainServer {

    private static final Logger logger = LogManager.getLogger(MainServer.class);

    protected ConfigurationManager config;

    protected ServerBootstrap boot;

    public MainServer(ConfigurationManager config, EventQueue ev, EventLoopGroup bossGroup, EventLoopGroup childGroup)
            throws Exception {
        this.config = config;

        boot = new ServerBootstrap();
        boot.group(bossGroup, childGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new MainChannelInitializer(ev));

        try {
            boot.bind(config.getString("network.main.host"), config.getInt("network.main.port")).syncUninterruptibly();
        } catch (Exception e) {
            logger.error("Exception thrown while trying to bind port for main server ... ");
            throw e;
        }

        logger.info("Main server listening on " + config.getString("network.main.host") + ":" +
                config.getInt("network.main.port"));
    }
}
