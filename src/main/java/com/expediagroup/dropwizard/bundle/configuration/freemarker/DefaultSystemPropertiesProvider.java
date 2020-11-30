package com.expediagroup.dropwizard.bundle.configuration.freemarker;

import java.util.Properties;

public class DefaultSystemPropertiesProvider implements SystemPropertiesProvider {
    @Override
    public Properties getSystemProperties() {
        return System.getProperties();
    }
}
