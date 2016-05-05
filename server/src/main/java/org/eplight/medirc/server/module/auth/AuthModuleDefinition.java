package org.eplight.medirc.server.module.auth;

import org.eplight.medirc.server.module.ModuleDefinition;

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
