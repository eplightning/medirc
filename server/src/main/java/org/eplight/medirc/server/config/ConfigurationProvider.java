package org.eplight.medirc.server.config;

public interface ConfigurationProvider {
    public String getString(String key);

    public int getInt(String key);

    public boolean hasKey(String key);
}
