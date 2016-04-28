package org.eplight.medirc.server.module;

import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class SimpleModuleManager implements ModuleManager {

    private static final Logger logger = LogManager.getLogger(SimpleModuleManager.class);

    protected ArrayList<ModuleDefinition> definitions;
    protected ArrayList<Module> modules;
    protected Injector parentInjector;

    public SimpleModuleManager(Injector parentInjector) {
        modules = new ArrayList<>();
        definitions = new ArrayList<>();
        this.parentInjector = parentInjector;
    }

    public void register(ModuleDefinition def) {
        logger.info("Module registered: " + def.getName() + " (" + def.getModuleClass().getName() + ")");
        definitions.add(def);
    }

    @Override
    public void start() {
        ArrayList<com.google.inject.Module> guiceModules = new ArrayList<>();

        for (ModuleDefinition def : definitions) {
            if (def instanceof com.google.inject.Module) {
                guiceModules.add((com.google.inject.Module) def);
            }
        }

        Injector injector = parentInjector.createChildInjector(guiceModules);

        for (ModuleDefinition def : definitions) {
            Module mod = (Module) injector.getInstance(def.getModuleClass());
            modules.add(mod);
            mod.start();
        }
    }

    @Override
    public void stop() {
        for (Module mod : modules) {
            mod.stop();
        }
    }
}
