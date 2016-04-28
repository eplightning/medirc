package org.eplight.medirc.server.module.auth;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.eplight.medirc.server.module.ModuleDefinition;

/**
 * Created by EpLightning on 28.04.2016.
 */
public class AuthModuleDefinition extends AbstractModule implements ModuleDefinition {

    @Override
    protected void configure() {
        bind(Users.class).in(Scopes.SINGLETON);
    }

    @Override
    public String getName() {
        return "Authentication module";
    }

    @Override
    public Class getModuleClass() {
        return AuthModule.class;
    }
}
