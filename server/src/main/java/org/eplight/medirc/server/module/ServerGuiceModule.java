package org.eplight.medirc.server.module;

import com.google.inject.AbstractModule;
import org.eplight.medirc.server.config.ConfigurationManager;
import org.eplight.medirc.server.event.EventLoop;
import org.eplight.medirc.server.event.dispatchers.function.MessageDispatcher;
import org.eplight.medirc.server.network.NetworkManager;

/**
 * Created by EpLightning on 28.04.2016.
 */
public class ServerGuiceModule extends AbstractModule {

    private ConfigurationManager configurationManager;
    private EventLoop loop;
    private MessageDispatcher dispatcher;
    private NetworkManager networkManager;

    public ServerGuiceModule(ConfigurationManager configurationManager, EventLoop loop, MessageDispatcher dispatcher,
                             NetworkManager networkManager) {
        this.configurationManager = configurationManager;
        this.loop = loop;
        this.dispatcher = dispatcher;
        this.networkManager = networkManager;
    }

    @Override
    protected void configure() {
        bind(ConfigurationManager.class).toInstance(configurationManager);
        bind(EventLoop.class).toInstance(loop);
        bind(MessageDispatcher.class).toInstance(dispatcher);
        bind(NetworkManager.class).toInstance(networkManager);
    }
}
