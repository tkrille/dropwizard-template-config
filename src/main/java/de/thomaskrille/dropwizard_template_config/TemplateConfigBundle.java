package de.thomaskrille.dropwizard_template_config;

import com.google.common.base.Optional;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.nio.charset.Charset;
import java.util.Set;

/**
 * Dropwizard {@link io.dropwizard.Bundle} that wraps the currently configured
 * {@link io.dropwizard.configuration.ConfigurationSourceProvider} with a
 * {@link de.thomaskrille.dropwizard_template_config.TemplateConfigurationSourceProvider}
 * that allows you to write your {@code config.yaml} as a
 * <a href="http://freemarker.org/">Freemarker</a> template.
 */
public class TemplateConfigBundle implements Bundle {

    private final Charset charset;
    private final Optional<String> resourceIncludePath;
    private final Optional<String> fileIncludePath;
    private final Optional<String> outputPath;
    private final Set<TemplateConfigVariablesProvider> customProviders;

    /**
     * Create a {@link TemplateConfigBundle} using the default configuration.
     */
    public TemplateConfigBundle() {
        this(new TemplateConfigBundleConfiguration());
    }

    /**
     * Create a {@link TemplateConfigBundle} using the given {@link TemplateConfigBundleConfiguration}.
     *
     * @param configuration The configuration for the new bundle. See {@link TemplateConfigBundleConfiguration}.
     */
    public TemplateConfigBundle(final TemplateConfigBundleConfiguration configuration) {
        charset = configuration.charset();
        resourceIncludePath = configuration.resourceIncludePath();
        fileIncludePath = configuration.fileIncludePath();
        outputPath = configuration.outputPath();
        customProviders = configuration.customProviders();
    }

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new TemplateConfigurationSourceProvider(
                bootstrap.getConfigurationSourceProvider(), new DefaultEnvironmentProvider(),
                new DefaultSystemPropertiesProvider(), charset,
                resourceIncludePath, fileIncludePath, outputPath, customProviders
        ));
    }

    @Override
    public void run(final Environment environment) {
        // intentionally left empty
    }

}
