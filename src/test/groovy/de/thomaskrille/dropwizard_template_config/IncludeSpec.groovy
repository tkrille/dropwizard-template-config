package de.thomaskrille.dropwizard_template_config

import com.google.common.base.*
import org.apache.commons.io.IOUtils
import spock.lang.Specification

class IncludeSpec extends Specification {

    def TestEnvironmentProvider environmentProvider = new TestEnvironmentProvider()

    def TemplateConfigurationSourceProvider templateConfigurationSourceProvider =
            new TemplateConfigurationSourceProvider(new TestConfigSourceProvider(),
                    environmentProvider,
                    new DefaultSystemPropertiesProvider(),
                    Charsets.UTF_8, Optional.of("/config-snippets"))

    def 'config snippets can be included from the classpath'() {
        given:
        def config = '''
                server:
                  type: simple
                  connector:
                    type: http
                    port: 8080

                <#include "database.yaml">

                logging:
                  level: WARN
                '''.stripIndent()

        when:
        def parsedConfig = templateConfigurationSourceProvider.open(config)


        then:
        def parsedConfigAsString = IOUtils.toString(parsedConfig)
        parsedConfigAsString == '''
                server:
                  type: simple
                  connector:
                    type: http
                    port: 8080

                database:
                  driverClass: org.postgresql.Driver
                  user: my-app
                  password: secret
                  url: jdbc:postgresql://localhost:5432/my-app-db

                logging:
                  level: WARN
                '''.stripIndent()
    }

    def 'config snippets can use templating features'(){
        given:
        def config = '''
                <#include "database-with-templating.yaml">
                '''.stripIndent()

        environmentProvider.put('DB_USER', 'my-app')
        environmentProvider.put('DB_PASSWORD', 'secret')
        environmentProvider.put('DB_HOST', 'localhost')
        environmentProvider.put('DB_PORT', '5432')

        when:
        def parsedConfig = templateConfigurationSourceProvider.open(config)

        then:
        def parsedConfigAsString = IOUtils.toString(parsedConfig)
        parsedConfigAsString == '''
                database:
                  driverClass: org.postgresql.Driver
                  user: my-app
                  password: secret
                  url: jdbc:postgresql://localhost:5432/my-app-db
                '''.stripIndent()
    }

    def 'relative include paths will be interpreted as absolute'(){
        given:
        def relativeIncludePath = "config-snippets"
        def TemplateConfigurationSourceProvider templateConfigurationSourceProvider =
                new TemplateConfigurationSourceProvider(new TestConfigSourceProvider(),
                        new DefaultEnvironmentProvider(),
                        new DefaultSystemPropertiesProvider(),
                        Charsets.UTF_8, Optional.of(relativeIncludePath))
        def config = '''
                <#include "database.yaml">
                '''.stripIndent()

        when:
        def parsedConfig = templateConfigurationSourceProvider.open(config)

        then:
        def parsedConfigAsString = IOUtils.toString(parsedConfig)
        parsedConfigAsString == '''
                database:
                  driverClass: org.postgresql.Driver
                  user: my-app
                  password: secret
                  url: jdbc:postgresql://localhost:5432/my-app-db
                '''.stripIndent()
    }

}
