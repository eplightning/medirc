package org.eplight.medirc.server.config;

import java.util.ArrayDeque;

public class ConfigurationManager {
    protected ArrayDeque<ConfigurationProvider> providers;

    public ConfigurationManager() {
        providers = new ArrayDeque<>();
    }

    public void addProvider(ConfigurationProvider provider) {
        providers.addFirst(provider);
    }

    public int getInt(String key) {
        for (ConfigurationProvider p : providers) {
            if (p.hasKey(key))
                return p.getInt(key);
        }

        return 0;
    }

    public String getString(String key) {
        for (ConfigurationProvider p : providers) {
            if (p.hasKey(key))
                return p.getString(key);
        }

        return null;
    }
}
