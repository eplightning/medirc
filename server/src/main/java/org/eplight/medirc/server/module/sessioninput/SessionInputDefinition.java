package org.eplight.medirc.server.module.sessioninput;

import org.eplight.medirc.server.module.ModuleDefinition;

/**
 * Created by EpLightning on 06.05.2016.
 */
public class SessionInputDefinition implements ModuleDefinition {

    @Override
    public String getName() {
        return "Session events validator";
    }

    @Override
    public Class getModuleClass() {
        return SessionInputModule.class;
    }
}
