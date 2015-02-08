package de.thomaskrille.dropwizard_template_config;

import java.util.Map;

public class DefaultEnvironmentProvider implements EnvironmentProvider {

    @Override
    public Map<String, String> getEnvironment() {
        return System.getenv();
    }
}
