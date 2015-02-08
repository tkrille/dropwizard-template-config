package de.thomaskrille.dropwizard_template_config;

import java.util.Map;

public interface EnvironmentProvider {
    Map<String, String> getEnvironment();
}
