package org.eplight.medirc.server.module.sessionhandleruser;

import org.eplight.medirc.server.module.ModuleDefinition;

/**
 * Created by EpLightning on 07.05.2016.
 */
public class SessionHandlerUserDefinition implements ModuleDefinition {

    @Override
    public String getName() {
        return "Session event handler - user events";
    }

    @Override
    public Class getModuleClass() {
        return SessionHandlerUserModule.class;
    }
}
