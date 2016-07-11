package de.thomaskrille.dropwizard_template_config

import com.google.common.base.*
import org.apache.commons.io.IOUtils
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.isA

class IncludeSpec extends Specification {

    def static TestEnvironmentProvider environmentProvider = new TestEnvironmentProvider()

    def static TemplateConfigurationSourceProvider providerWithResourceIncludePath =
            new TemplateConfigurationSourceProvider(new TestConfigSourceProvider(),
                    environmentProvider,
                    new DefaultSystemPropertiesProvider(),
                    Charsets.UTF_8,
                    Optional.of("/config-snippets"), Optional.absent(), Optional.absent())

    def static TemplateConfigurationSourceProvider providerWithFileIncludePath =
            new TemplateConfigurationSourceProvider(new TestConfigSourceProvider(),
                    environmentProvider,
                    new DefaultSystemPropertiesProvider(),
                    Charsets.UTF_8,
                    Optional.absent(), Optional.of("src/test/resources/config-snippets/"), Optional.absent())

    def 'config snippets can be included from the classpath and filesystem'() {
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

        expect:
        def parsedConfig = provider.open(config)
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

        where:
        provider << [providerWithResourceIncludePath, providerWithFileIncludePath]
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

        expect:
        def parsedConfig = provider.open(config)
        def parsedConfigAsString = IOUtils.toString(parsedConfig)
        parsedConfigAsString == '''
                database:
                  driverClass: org.postgresql.Driver
                  user: my-app
                  password: secret
                  url: jdbc:postgresql://localhost:5432/my-app-db
                '''.stripIndent()

        where:
        provider << [providerWithResourceIncludePath, providerWithFileIncludePath]
    }

    def 'relative resource include paths will be interpreted as absolute'(){
        given:
        def relativeIncludePath = "config-snippets"
        def TemplateConfigurationSourceProvider templateConfigurationSourceProvider =
                new TemplateConfigurationSourceProvider(new TestConfigSourceProvider(),
                        new DefaultEnvironmentProvider(),
                        new DefaultSystemPropertiesProvider(),
                        Charsets.UTF_8, Optional.of(relativeIncludePath), Optional.absent(), Optional.absent())
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

    def 'specifying file and then resource include paths fails'(){
        given:
        def TemplateConfigBundleConfiguration config =
                new TemplateConfigBundleConfiguration()
                        .fileIncludePath("src/test/resources/config-snippets/")
        when:
        config.resourceIncludePath("/config-snippets")

        then:
        thrown(IllegalStateException)
    }

    def 'specifying resource and then file include paths fails'(){
        given:
        def TemplateConfigBundleConfiguration config =
                new TemplateConfigBundleConfiguration()
                        .resourceIncludePath("/config-snippets")

        when:
        config.fileIncludePath("src/test/resources/config-snippets/")

        then:
        thrown(IllegalStateException)
    }
}
