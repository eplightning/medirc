package org.eplight.medirc.server.config;

public interface ConfigurationProvider {
    String getString(String key);

    int getInt(String key);

    boolean hasKey(String key);
}
