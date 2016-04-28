package org.eplight.medirc.server;

import com.google.inject.Guice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eplight.medirc.server.config.ConfigurationManager;
import org.eplight.medirc.server.config.providers.PropertiesConfigurationProvider;
import org.eplight.medirc.server.event.EventLoop;
import org.eplight.medirc.server.event.consumers.DispatcherConsumer;
import org.eplight.medirc.server.event.dispatchers.function.MessageDispatcher;
import org.eplight.medirc.server.event.events.MessageEvent;
import org.eplight.medirc.server.event.queue.LinkedEventQueue;
import org.eplight.medirc.server.module.ServerGuiceModule;
import org.eplight.medirc.server.module.SimpleModuleManager;
import org.eplight.medirc.server.module.auth.AuthModuleDefinition;
import org.eplight.medirc.server.network.NetworkManager;
import org.eplight.medirc.server.user.User;

import java.util.Map;

public class ServerApplication {

    private static final Logger logger = LogManager.getLogger(ServerApplication.class);

    protected ConfigurationManager config;
    protected NetworkManager network;
    protected EventLoop loop;
    protected MessageDispatcher messageDispatcher;
    protected Map<Integer, User> users;
    protected SimpleModuleManager modules;

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

        loop = new EventLoop(new LinkedEventQueue());

        messageDispatcher = new MessageDispatcher();
        loop.registerConsumer(new DispatcherConsumer<>(messageDispatcher, MessageEvent.class));

        network = new NetworkManager(config, loop.getQueue());

        modules = new SimpleModuleManager(Guice.createInjector(new ServerGuiceModule(config, loop, messageDispatcher,
                network)));

        modules.register(new AuthModuleDefinition());

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shutdown();
            }
        }));

        logger.info("Application initialized");
    }

    public void run() throws Exception {
        logger.info("Starting module");
        modules.start();

        logger.info("Getting ready to accept connections ...");
        network.initServers();

        logger.info("Entering event loop ...");
        loop.run();

        logger.info("Event loop interrupted, closing application");
    }

    public void shutdown() {
        logger.info("Shutting down ...");

        modules.stop();

        network.stopServers();
    }
}
