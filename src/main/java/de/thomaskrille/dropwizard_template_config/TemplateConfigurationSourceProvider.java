package de.thomaskrille.dropwizard_template_config;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import io.dropwizard.configuration.ConfigurationSourceProvider;

public class TemplateConfigurationSourceProvider implements ConfigurationSourceProvider {

    private final ConfigurationSourceProvider parentProvider;
    private final EnvironmentProvider environmentProvider;

    public TemplateConfigurationSourceProvider(final ConfigurationSourceProvider parentProvider,
            final EnvironmentProvider environmentProvider) {

        this.parentProvider = parentProvider;
        this.environmentProvider = environmentProvider;
    }

    @Override
    public InputStream open(final String path) throws IOException {
        final InputStream configurationTemplate = parentProvider.open(path);

        final Mustache mustache = new DefaultMustacheFactory()
                .compile(new InputStreamReader(configurationTemplate), "configuration");

        final Map<String, Object> scope = new HashMap<>(1);
        scope.put("env", environmentProvider.getEnvironment());
        // TODO: add system properties and other useful maps

        final String parsedConfiguration = mustache.execute(new StringWriter(), scope).toString();

        return new ByteArrayInputStream(parsedConfiguration.getBytes());
    }
}
