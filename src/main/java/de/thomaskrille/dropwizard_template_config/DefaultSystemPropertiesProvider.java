package de.thomaskrille.dropwizard_template_config;

import java.util.Properties;

public class DefaultSystemPropertiesProvider implements SystemPropertiesProvider {
    @Override
    public Properties getSystemProperties() {
        return System.getProperties();
    }
}
