package org.eplight.medirc.server.config.providers;

import org.eplight.medirc.server.config.ConfigurationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfigurationProvider implements ConfigurationProvider {

    protected Properties properties;

    public PropertiesConfigurationProvider(InputStream stream) throws IOException {
        properties = new Properties();

        properties.load(stream);
    }

    @Override
    public String getString(String key) {
        return properties.getProperty(key);
    }

    @Override
    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    @Override
    public boolean hasKey(String key) {
        return properties.getProperty(key) != null;
    }

    @Override
    public boolean getBool(String key) {
        String prop = getString(key);

        return prop.equals("yes") || prop.equals("1") || prop.equals("true");
    }
}
