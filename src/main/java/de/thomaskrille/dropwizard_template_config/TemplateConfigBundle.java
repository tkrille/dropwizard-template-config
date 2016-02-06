package de.thomaskrille.dropwizard_template_config;

import io.dropwizard.Bundle;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.nio.charset.Charset;

/**
 * Dropwizard {@link io.dropwizard.Bundle} that wraps the currently configured
 * {@link io.dropwizard.configuration.ConfigurationSourceProvider} with a
 * {@link de.thomaskrille.dropwizard_template_config.TemplateConfigurationSourceProvider}
 * that allows you to write your {@code config.yaml} as a
 * <a href="http://freemarker.org/">Freemarker</a> template.
 */
public class TemplateConfigBundle implements Bundle {

    private final Charset charset;

    /**
     * Create a {@link TemplateConfigBundle} using the default configuration.
     */
    public TemplateConfigBundle() {
        this(new TemplateConfigBundleConfiguration());
    }

    /**
     * Create a {@link TemplateConfigBundle} using the given {@link Charset}.
     *
     * @param charset The {@link Charset} used to load, process, and output the config template.
     * @deprecated Use {@link TemplateConfigBundle#TemplateConfigBundle(TemplateConfigBundleConfiguration)} instead.
     */
    @Deprecated
    public TemplateConfigBundle(final Charset charset) {
        this.charset = charset;
    }

    /**
     * Create a {@link TemplateConfigBundle} using the given {@link TemplateConfigBundleConfiguration}.
     *
     * @param configuration The configuration for the new bundle. See {@link TemplateConfigBundleConfiguration}.
     */
    public TemplateConfigBundle(final TemplateConfigBundleConfiguration configuration) {
        this.charset = configuration.charset();
    }

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        final ConfigurationSourceProvider configurationSourceProvider = new TemplateConfigurationSourceProvider(
                bootstrap.getConfigurationSourceProvider(),
                new DefaultEnvironmentProvider(),
                new DefaultSystemPropertiesProvider(),
                charset);

        bootstrap.setConfigurationSourceProvider(configurationSourceProvider);
    }

    @Override
    public void run(final Environment environment) {
        // intentionally left empty
    }

}
