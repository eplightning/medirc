package org.eplight.medirc.server.module.autovoice;

import org.eplight.medirc.server.module.ModuleDefinition;

/**
 * Created by EpLightning on 12.06.2016.
 */
public class AutoVoiceDefinition implements ModuleDefinition {

    @Override
    public String getName() {
        return "Auto voicing support";
    }

    @Override
    public Class getModuleClass() {
        return AutoVoiceModule.class;
    }
}
