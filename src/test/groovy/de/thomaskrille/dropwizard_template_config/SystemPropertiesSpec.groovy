package de.thomaskrille.dropwizard_template_config

import com.google.common.base.Charsets
import com.google.common.base.Optional
import org.apache.commons.io.IOUtils
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.CoreMatchers.isA

class SystemPropertiesSpec extends Specification {

    def TestSystemPropertiesProvider systemPropertiesProvider = new TestSystemPropertiesProvider()

    def TemplateConfigurationSourceProvider templateConfigurationSourceProvider =
            new TemplateConfigurationSourceProvider(new TestConfigSourceProvider(),
                    new DefaultEnvironmentProvider(),
                    systemPropertiesProvider,
                    Charsets.UTF_8, Optional.absent())

    def 'replacing a system property works'() throws Exception {
        given:
        def config = '''server:
                          type: simple
                          connector:
                            type: http
                            port: ${sys.http_port}'''

        systemPropertiesProvider.put('http_port', '8080')

        when:
        def parsedConfig = templateConfigurationSourceProvider.open(config)
        def parsedConfigAsString = IOUtils.toString(parsedConfig)

        then:
        parsedConfigAsString containsString('server:')
        parsedConfigAsString containsString('type: http')
        parsedConfigAsString containsString('port: 8080')
    }

    def 'using a missing system property honors default value'() throws Exception {
        given:
        def config = '''server:
                          type: simple
                          connector:
                            type: http
                            port: ${sys.http_port!8080}'''

        when:
        def parsedConfig = templateConfigurationSourceProvider.open(config)
        def parsedConfigAsString = IOUtils.toString(parsedConfig)

        then:
        parsedConfigAsString containsString('server:')
        parsedConfigAsString containsString('type: http')
        parsedConfigAsString containsString('port: 8080')
    }

    def 'using a missing system property without default value fails'() throws Exception {
        given:
        def config = '''server:
                          type: simple
                          connector:
                            type: http
                            port: ${sys.http_port}'''

        when:
        templateConfigurationSourceProvider.open(config)

        then:
        def exception = thrown(RuntimeException)
        def exceptionsCause = exception.cause
        exceptionsCause isA(freemarker.core.InvalidReferenceException)
    }

    def 'referencing a system property with a dot in its name must use bracket syntax'() throws Exception {
        given:
        def config = '''server:
                          type: simple
                          connector:
                            type: http
                            port: ${sys['my_app.http.port']}'''

        systemPropertiesProvider.put('my_app.http.port', '8080')

        when:
        def parsedConfig = templateConfigurationSourceProvider.open(config)
        def parsedConfigAsString = IOUtils.toString(parsedConfig)

        then:
        parsedConfigAsString containsString('server:')
        parsedConfigAsString containsString('type: http')
        parsedConfigAsString containsString('port: 8080')
    }

}
