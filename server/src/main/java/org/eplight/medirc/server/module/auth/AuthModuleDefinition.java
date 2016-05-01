package org.eplight.medirc.server.module.auth;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.eplight.medirc.server.module.ModuleDefinition;
import org.eplight.medirc.server.user.Users;

/**
 * Created by EpLightning on 28.04.2016.
 */
public class AuthModuleDefinition implements ModuleDefinition {

    @Override
    public String getName() {
        return "Authentication module";
    }

    @Override
    public Class getModuleClass() {
        return AuthModule.class;
    }
}
