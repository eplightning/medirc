package org.eplight.medirc.server.modules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class SimpleModuleManager implements ModuleManager {

    private static final Logger logger = LogManager.getLogger(SimpleModuleManager.class);

    protected ArrayList<Module> modules;

    public SimpleModuleManager() {
        modules = new ArrayList<>();
    }

    public void register(Module module) {
        logger.info("Module registered: " + module.getClass().getName());
        modules.add(module);
    }

    @Override
    public void start() {
        for (Module mod : modules) {
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
