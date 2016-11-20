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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TemplateConfigurationSourceProvider implements ConfigurationSourceProvider {

    private final ConfigurationSourceProvider parentProvider;
    private final SystemPropertiesProvider systemPropertiesProvider;
    private final EnvironmentProvider environmentProvider;
    private final TemplateConfigBundleConfiguration configuration;

    TemplateConfigurationSourceProvider(
            final ConfigurationSourceProvider parentProvider,
            final EnvironmentProvider environmentProvider,
            final SystemPropertiesProvider systemPropertiesProvider,
            final TemplateConfigBundleConfiguration configuration
    ) {
        this.parentProvider = parentProvider;
        this.environmentProvider = environmentProvider;
        this.systemPropertiesProvider = systemPropertiesProvider;
        this.configuration = configuration;
    }

    @Override
    public InputStream open(final String path) throws IOException {
        try {
            return createConfigurationSourceStream(path);
        } catch (TemplateException e) {
            throw Throwables.propagate(e);
        }
    }

    private InputStream createConfigurationSourceStream(String path) throws IOException, TemplateException {
        Configuration freemarkerConfiguration = createFreemarkerConfiguration();
        Map<String, Object> dataModel = createDataModel();
        Template configTemplate = createFreemarkerTemplate(path, freemarkerConfiguration);
        byte[] processedConfigTemplate = processTemplate(dataModel, configTemplate);
        writeConfigFile(processedConfigTemplate);
        return new ByteArrayInputStream(processedConfigTemplate);
    }

    private Configuration createFreemarkerConfiguration() throws IOException {
        Configuration freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_22);
        freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerConfiguration.setNumberFormat("computer");
        freemarkerConfiguration.setDefaultEncoding(configuration.charset().name());
        Optional<String> resourceIncludePath = configuration.resourceIncludePath();
        Optional<String> fileIncludePath = configuration.fileIncludePath();
        if (resourceIncludePath.isPresent()) {
            String includePath = resourceIncludePath.get();
            if (!includePath.startsWith("/")) {
                includePath = "/" + includePath;
            }
            freemarkerConfiguration.setClassForTemplateLoading(getClass(), includePath);
        } else if (fileIncludePath.isPresent()) {
            File includeDir = new File(fileIncludePath.get());
            freemarkerConfiguration.setDirectoryForTemplateLoading(includeDir);
        }
        return freemarkerConfiguration;
    }

    private Map<String, Object> createDataModel() {
        Map<String, Object> dataModel = new HashMap<>();
        // We populate the dataModel with lowest-priority items first, so that higher-priority
        // items can overwrite existing entries.
        // Lowest priority is a flat copy of Java system properties, then a flat copy of
        // environment variables, then a flat copy of custom variables, and finally the "env", "sys",
        // and custom namespaces.
        Properties systemProperties = systemPropertiesProvider.getSystemProperties();
        for (String propertyName : systemProperties.stringPropertyNames()) {
            dataModel.put(propertyName, systemProperties.getProperty(propertyName));
        }
        dataModel.putAll(environmentProvider.getEnvironment());
        for (TemplateConfigVariablesProvider customProvider : configuration.customProviders()) {
            dataModel.putAll(customProvider.getVariables());
        }
        dataModel.put("env", environmentProvider.getEnvironment());
        dataModel.put("sys", systemPropertiesProvider.getSystemProperties());
        for (TemplateConfigVariablesProvider customProvider : configuration.customProviders()) {
            dataModel.put(customProvider.getNamespace(), customProvider.getVariables());
        }
        return dataModel;
    }

    private Template createFreemarkerTemplate(String path, Configuration freemarkerConfiguration) throws IOException {
        InputStream configurationSource = parentProvider.open(path);
        InputStreamReader configurationSourceReader = new InputStreamReader(configurationSource, configuration.charset());
        return new Template("config", configurationSourceReader, freemarkerConfiguration);
    }

    private byte[] processTemplate(Map<String, Object> dataModel, Template template) throws TemplateException, IOException {
        ByteArrayOutputStream processedTemplateStream = new ByteArrayOutputStream();
        template.process(dataModel, new OutputStreamWriter(processedTemplateStream, configuration.charset()));
        return processedTemplateStream.toByteArray();
    }

    private void writeConfigFile(byte[] processedTemplateBytes) throws IOException {
        Optional<String> outputPath = configuration.outputPath();
        if (outputPath.isPresent()) {
            File outputFile = new File(outputPath.get());
            Files.createParentDirs(outputFile);
            Files.write(processedTemplateBytes, outputFile);
        }
    }
}
