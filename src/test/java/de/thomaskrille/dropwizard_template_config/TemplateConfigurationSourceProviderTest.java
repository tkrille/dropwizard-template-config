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
    public void replacing_environment_variables_works() throws Exception {
        environmentProvider.put("APP_PORT", "8080");
        environmentProvider.put("ADMIN_PORT", "8081");
        environmentProvider.put("LOG_LEVEL", "INFO");

        InputStream parsedConfig = templateConfigurationSourceProvider.open("src/test/resources/config-env.yaml");
        String parsedConfigAsString = IOUtils.toString(parsedConfig);

        assertThat(parsedConfigAsString, containsString("port: 8080"));
        assertThat(parsedConfigAsString, containsString("port: 8081"));
        assertThat(parsedConfigAsString, containsString("level: INFO"));
    }

    @Test
    public void replacing_system_properties_works() throws Exception {
        System.setProperty("app_port", "8080");
        System.setProperty("admin_port", "8081");
        System.setProperty("log_level", "INFO");

        InputStream parsedConfig = templateConfigurationSourceProvider.open("src/test/resources/config-sys.yaml");
        String parsedConfigAsString = IOUtils.toString(parsedConfig);

        assertThat(parsedConfigAsString, containsString("port: 8080"));
        assertThat(parsedConfigAsString, containsString("port: 8081"));
        assertThat(parsedConfigAsString, containsString("level: INFO"));
    }
}
