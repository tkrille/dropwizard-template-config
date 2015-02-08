package de.thomaskrille.dropwizard_template_config;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import io.dropwizard.configuration.FileConfigurationSourceProvider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class TemplateConfigurationSourceProviderTest {

    private final TestEnvironmentProvider environmentProvider = new TestEnvironmentProvider();
    private final TemplateConfigurationSourceProvider templateConfigurationSourceProvider =
            new TemplateConfigurationSourceProvider(
                    new FileConfigurationSourceProvider(), environmentProvider);

    @Test
    public void testOpen() throws Exception {
        environmentProvider.put("APP_PORT", "8080");
        environmentProvider.put("ADMIN_PORT", "8081");
        environmentProvider.put("LOG_LEVEL", "INFO");

        InputStream parsedConfig = templateConfigurationSourceProvider.open("src/test/resources/config.yaml");
        String parsedConfigAsString = IOUtils.toString(parsedConfig);

        assertThat(parsedConfigAsString, containsString("port: 8080"));
        assertThat(parsedConfigAsString, containsString("port: 8081"));
        assertThat(parsedConfigAsString, containsString("level: INFO"));
    }
}
