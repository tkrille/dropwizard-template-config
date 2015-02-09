package de.thomaskrille.dropwizard_template_config;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Throwables;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.dropwizard.configuration.ConfigurationSourceProvider;

public class TemplateConfigurationSourceProvider implements ConfigurationSourceProvider {

    private ConfigurationSourceProvider parentProvider;
    private EnvironmentProvider environmentProvider;

    public TemplateConfigurationSourceProvider(final ConfigurationSourceProvider parentProvider,
            final EnvironmentProvider environmentProvider) {

        this.parentProvider = parentProvider;
        this.environmentProvider = environmentProvider;
    }

    @Override
    public InputStream open(final String path) throws IOException {
        try {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_21);
            // configuration.setDefaultEncoding("UTF-8");
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            Map<String, Object> dataModel = new HashMap<>(2);
            dataModel.put("env", environmentProvider.getEnvironment());
            dataModel.put("sys", System.getProperties());

            ByteArrayOutputStream processedTemplateStream = new ByteArrayOutputStream();
            Reader configTemplate = new InputStreamReader(parentProvider.open(path));

            new Template("config", configTemplate, configuration)
                    .process(dataModel, new OutputStreamWriter(processedTemplateStream));

            return new ByteArrayInputStream(processedTemplateStream.toByteArray());
        } catch (TemplateException e) {
            throw Throwables.propagate(e);
        } finally {
            parentProvider = null;
            environmentProvider = null;
        }
    }
}
