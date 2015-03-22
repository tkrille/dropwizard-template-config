package de.thomaskrille.dropwizard_template_config;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

import freemarker.template.*;
import io.dropwizard.configuration.ConfigurationSourceProvider;

public class TemplateConfigurationSourceProvider implements ConfigurationSourceProvider {

    private final Charset charset;
    private final SystemPropertiesProvider systemPropertiesProvider;
    private final ConfigurationSourceProvider parentProvider;
    private final EnvironmentProvider environmentProvider;

    /**
     * @deprecated Don't use this class directly, use the
     * {@link de.thomaskrille.dropwizard_template_config.TemplateConfigBundle}.
     */
    @Deprecated
    public TemplateConfigurationSourceProvider(final ConfigurationSourceProvider parentProvider,
                                               final EnvironmentProvider environmentProvider,
                                               final SystemPropertiesProvider systemPropertiesProvider) {

        this(parentProvider, environmentProvider, systemPropertiesProvider, Charsets.UTF_8);
    }

    TemplateConfigurationSourceProvider(final ConfigurationSourceProvider parentProvider,
            final EnvironmentProvider environmentProvider,
            final SystemPropertiesProvider systemPropertiesProvider,
            final Charset charset) {

        this.parentProvider = parentProvider;
        this.environmentProvider = environmentProvider;
        this.systemPropertiesProvider = systemPropertiesProvider;
        this.charset = charset;
    }

    @Override
    public InputStream open(final String path) throws IOException {
        try {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setNumberFormat("computer");
            configuration.setDefaultEncoding(charset.name());

            Map<String, Object> dataModel = new HashMap<>(2);
            dataModel.put("env", environmentProvider.getEnvironment());
            dataModel.put("sys", systemPropertiesProvider.getSystemProperties());

            ByteArrayOutputStream processedTemplateStream = new ByteArrayOutputStream();
            Reader configTemplate = new InputStreamReader(parentProvider.open(path), charset);

            new Template("config", configTemplate, configuration)
                    .process(dataModel, new OutputStreamWriter(processedTemplateStream, charset));

            return new ByteArrayInputStream(processedTemplateStream.toByteArray());
        } catch (TemplateException e) {
            throw Throwables.propagate(e);
        }
    }
}
