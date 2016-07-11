package de.thomaskrille.dropwizard_template_config;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.dropwizard.configuration.ConfigurationSourceProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TemplateConfigurationSourceProvider implements ConfigurationSourceProvider {

    private final Charset charset;
    private final Optional<String> resourceIncludePath;
    private final Optional<String> fileIncludePath;
    private final Optional<String> outputPath;
    private final SystemPropertiesProvider systemPropertiesProvider;
    private final ConfigurationSourceProvider parentProvider;
    private final EnvironmentProvider environmentProvider;

    TemplateConfigurationSourceProvider(final ConfigurationSourceProvider parentProvider,
                                        final EnvironmentProvider environmentProvider,
                                        final SystemPropertiesProvider systemPropertiesProvider,
                                        final Charset charset,
                                        Optional<String> resourceIncludePath,
                                        Optional<String> fileIncludePath,
                                        Optional<String> outputPath) {

        this.parentProvider = parentProvider;
        this.environmentProvider = environmentProvider;
        this.systemPropertiesProvider = systemPropertiesProvider;
        this.charset = charset;
        this.resourceIncludePath = resourceIncludePath;
        this.fileIncludePath = fileIncludePath;
        this.outputPath = outputPath;
    }

    @Override
    public InputStream open(final String path) throws IOException {
        try {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setNumberFormat("computer");
            configuration.setDefaultEncoding(charset.name());

            if (resourceIncludePath.isPresent()) {
                String includePath = this.resourceIncludePath.get();
                if (!includePath.startsWith("/")) {
                    includePath = "/" + includePath;
                }
                configuration.setClassForTemplateLoading(getClass(), includePath);
            }
            else if (fileIncludePath.isPresent()) {
                File includeDir = new File(this.fileIncludePath.get());
                configuration.setDirectoryForTemplateLoading(includeDir);
            }

            Map<String, Object> dataModel = new HashMap<>();

            // We populate the dataModel with lowest-priority items first, so that higher-priority
            // items can overwrite existing entries.
            // Lowest priority is a flat copy of Java system properties, then a flat copy of
            // environment variables, and finally the "env" and "sys" namespaces.
            Properties systemProperties = systemPropertiesProvider.getSystemProperties();
            for (String propertyName : systemProperties.stringPropertyNames()) {
                dataModel.put(propertyName, systemProperties.getProperty(propertyName));
            }
            dataModel.putAll(environmentProvider.getEnvironment());
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
