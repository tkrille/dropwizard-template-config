package de.thomaskrille.dropwizard_template_config;

import io.dropwizard.Bundle;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TemplateConfigBundle implements Bundle {

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        final ConfigurationSourceProvider configurationSourceProvider = new TemplateConfigurationSourceProvider(
                bootstrap.getConfigurationSourceProvider(),
                new DefaultEnvironmentProvider());

        bootstrap.setConfigurationSourceProvider(configurationSourceProvider);
    }

    @Override
    public void run(final Environment environment) {
        // intentionally left empty
    }
}
