package de.thomaskrille.dropwizard_template_config;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;

import freemarker.template.*;
import com.google.common.io.Files;
import io.dropwizard.configuration.ConfigurationSourceProvider;

public class TemplateConfigurationSourceProvider implements ConfigurationSourceProvider {

    private final Charset charset;
    private final Optional<String> includePath;
    private final Optional<String> outputPath;
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

        this(parentProvider, environmentProvider, systemPropertiesProvider,
                Charsets.UTF_8, Optional.<String>absent(), Optional.<String>absent());
    }

    TemplateConfigurationSourceProvider(final ConfigurationSourceProvider parentProvider,
                                        final EnvironmentProvider environmentProvider,
                                        final SystemPropertiesProvider systemPropertiesProvider,
                                        final Charset charset,
                                        Optional<String> includePath,
                                        Optional<String> outputPath) {

        this.parentProvider = parentProvider;
        this.environmentProvider = environmentProvider;
        this.systemPropertiesProvider = systemPropertiesProvider;
        this.charset = charset;
        this.includePath = includePath;
        this.outputPath = outputPath;
    }

    @Override
    public InputStream open(final String path) throws IOException {
        try {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setNumberFormat("computer");
            configuration.setDefaultEncoding(charset.name());

            if(includePath.isPresent()) {
                String includePath = this.includePath.get();
                if(!includePath.startsWith("/")) {
                    includePath = "/" + includePath;
                }
                configuration.setClassForTemplateLoading(getClass(), includePath);
            }

            Map<String, Object> dataModel = new HashMap<>(2);
            dataModel.put("env", environmentProvider.getEnvironment());
            dataModel.put("sys", systemPropertiesProvider.getSystemProperties());

            ByteArrayOutputStream processedTemplateStream = new ByteArrayOutputStream();
            Reader configTemplate = new InputStreamReader(parentProvider.open(path), charset);

            new Template("config", configTemplate, configuration)
                    .process(dataModel, new OutputStreamWriter(processedTemplateStream, charset));
            byte[] processedTemplateBytes = processedTemplateStream.toByteArray();

            if (outputPath.isPresent()) {
                File outputFile = new File(outputPath.get());
                Files.createParentDirs(outputFile);
                Files.write(processedTemplateBytes, outputFile);
            }

            return new ByteArrayInputStream(processedTemplateBytes);
        } catch (TemplateException e) {
            throw Throwables.propagate(e);
        }
    }
}
