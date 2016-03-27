package org.eplight.medirc.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eplight.medirc.server.config.ConfigurationManager;
import org.eplight.medirc.server.config.providers.PropertiesConfigurationProvider;
import org.eplight.medirc.server.network.NetworkManager;

public class ServerApplication {

    private static final Logger logger = LogManager.getLogger(ServerApplication.class);

    protected ConfigurationManager config;
    protected NetworkManager network;

    static public void main(String[] argv) {
        try {
            ServerApplication app = new ServerApplication(argv);
            app.run();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public ServerApplication(String[] argv) throws Exception {
        config = new ConfigurationManager();
        config.addProvider(new PropertiesConfigurationProvider(
                ServerApplication.class.getResourceAsStream("/default.properties")));

        network = new NetworkManager(config);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shutdown();
            }
        }));

        logger.info("Application initialized");
    }

    public void run() throws Exception {
        logger.info("Getting ready to accept connections ...");
        network.initServers();

        while (true) {
            Thread.sleep(1000);
        }
    }

    public void shutdown() {
        logger.info("Shutting down ...");

        network.stopServers();
    }
}
