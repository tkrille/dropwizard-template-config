package de.thomaskrille.dropwizard_template_config

import com.google.common.base.Charsets
import com.google.common.base.Optional
import org.apache.commons.io.IOUtils
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.containsString

class AdvancedInterpolationSpec extends Specification {

    def TestEnvironmentProvider environmentProvider = new TestEnvironmentProvider()

    def TemplateConfigurationSourceProvider templateConfigurationSourceProvider =
            new TemplateConfigurationSourceProvider(new TestConfigSourceProvider(),
                    environmentProvider,
                    new DefaultSystemPropertiesProvider(),
                    Charsets.UTF_8, Optional.absent(), Optional.absent())

    def 'replacing an environment variable inline works'() {
        given:
        def config = '''database:
                          driverClass: org.postgresql.Driver
                          user: ${env.DB_USER}
                          password: ${env.DB_PASSWORD}
                          url: jdbc:postgresql://${env.DB_HOST}:${env.DB_PORT}/my-app-db'''

        environmentProvider.put('DB_USER', 'user')
        environmentProvider.put('DB_PASSWORD', 'password')
        environmentProvider.put('DB_HOST', 'db-host')
        environmentProvider.put('DB_PORT', '12345')

        when:
        InputStream parsedConfig = templateConfigurationSourceProvider.open(config)
        String parsedConfigAsString = IOUtils.toString(parsedConfig)

        then:
        parsedConfigAsString containsString('database:')
        parsedConfigAsString containsString('driverClass: org.postgresql.Driver')
        parsedConfigAsString containsString('user: user')
        parsedConfigAsString containsString('password: password')
        parsedConfigAsString containsString('url: jdbc:postgresql://db-host:12345/my-app-db')
    }

    def 'inserting whole mappings works'() {
        given:
        def config = '''
                server:
                  ${env.SERVER_TYPE_LINE}
                  connector:
                    ${env.SERVER_CONNECTOR_TYPE_LINE}
                    port: 8080
                '''

        environmentProvider.put('SERVER_TYPE_LINE', 'type: simple')
        environmentProvider.put('SERVER_CONNECTOR_TYPE_LINE', 'type: http')

        when:
        InputStream parsedConfig = templateConfigurationSourceProvider.open(config)
        String parsedConfigAsString = IOUtils.toString(parsedConfig)

        then:
        parsedConfigAsString containsString('type: simple')
        parsedConfigAsString containsString('type: http')
    }

}
