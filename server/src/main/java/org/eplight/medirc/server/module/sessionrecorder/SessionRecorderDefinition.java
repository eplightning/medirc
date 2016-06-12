package org.eplight.medirc.server.module.sessionrecorder;

import org.eplight.medirc.server.module.ModuleDefinition;

/**
 * Created by EpLightning on 12.06.2016.
 */
public class SessionRecorderDefinition implements ModuleDefinition {

    @Override
    public String getName() {
        return "Session events recorder";
    }

    @Override
    public Class getModuleClass() {
        return SessionRecorderModule.class;
    }
}
