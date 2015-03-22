package de.thomaskrille.dropwizard_template_config;

import com.google.common.base.Charsets;
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
     * Creates a new {@link TemplateConfigBundle} using UTF-8 as the {@link Charset}.
     *
     * <p>
     * The {@link Charset} is used to load, process, and output the config template.
     * </p>
     */
    public TemplateConfigBundle() {
        this(Charsets.UTF_8);
    }

    /**
     * Creates a new {@link TemplateConfigBundle} using the given {@link Charset}.
     *
     * <p>
     * The {@link Charset} is used to load, process, and output the config template.
     * </p>
     *
     * @param charset
     *        The {@link Charset} used to load, process, and output the config template.
     */
    public TemplateConfigBundle(final Charset charset) {
        this.charset = charset;
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
