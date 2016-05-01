package org.eplight.medirc.server.module.home;

import org.eplight.medirc.server.module.ModuleDefinition;

public class HomeModuleDefinition implements ModuleDefinition {

    @Override
    public String getName() {
        return "Obsługa głównego okna";
    }

    @Override
    public Class getModuleClass() {
        return HomeModule.class;
    }
}
