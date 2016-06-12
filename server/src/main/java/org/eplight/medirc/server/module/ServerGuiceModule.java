package org.eplight.medirc.server.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.eplight.medirc.server.config.ConfigurationManager;
import org.eplight.medirc.server.database.JDBCConnectionProvider;
import org.eplight.medirc.server.event.EventLoop;
import org.eplight.medirc.server.event.dispatchers.function.MessageDispatcher;
import org.eplight.medirc.server.image.ImageManager;
import org.eplight.medirc.server.image.repo.ImageRepository;
import org.eplight.medirc.server.image.repo.JDBCImageRepository;
import org.eplight.medirc.server.network.NetworkManager;
import org.eplight.medirc.server.session.active.ActiveSessionsManager;
import org.eplight.medirc.server.session.repository.JDBCSessionRepository;
import org.eplight.medirc.server.session.repository.SessionRepository;
import org.eplight.medirc.server.user.Users;
import org.eplight.medirc.server.user.auth.Authentication;
import org.eplight.medirc.server.user.auth.JDBCAuthentication;
import org.eplight.medirc.server.user.factory.JDBCUserRepository;
import org.eplight.medirc.server.user.factory.UserRepository;

import java.sql.Connection;

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

        bind(Connection.class).toProvider(JDBCConnectionProvider.class).in(Scopes.SINGLETON);
        bind(Users.class).in(Scopes.SINGLETON);
        bind(ActiveSessionsManager.class).in(Scopes.SINGLETON);
        bind(SessionRepository.class).to(JDBCSessionRepository.class).in(Scopes.SINGLETON);
        bind(Authentication.class).to(JDBCAuthentication.class).in(Scopes.SINGLETON);
        bind(UserRepository.class).to(JDBCUserRepository.class).in(Scopes.SINGLETON);
        bind(ImageRepository.class).to(JDBCImageRepository.class).in(Scopes.SINGLETON);
        bind(ImageManager.class).in(Scopes.SINGLETON);
    }
}
