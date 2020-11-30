package com.expediagroup.dropwizard.bundle.configuration.freemarker;

import java.util.Properties;

public class TestSystemPropertiesProvider implements SystemPropertiesProvider {

    private final Properties data = new Properties();

    public void put(String name, String value) {
        data.put(name, value);
    }

    @Override
    public Properties getSystemProperties() {
        return data;
    }
}
