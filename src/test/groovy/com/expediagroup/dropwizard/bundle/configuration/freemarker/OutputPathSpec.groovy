package com.expediagroup.dropwizard.bundle.configuration.freemarker

import com.google.common.base.*
import com.google.common.io.Files
import org.apache.commons.io.IOUtils
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.CoreMatchers.equalTo

class OutputPathSpec extends Specification {
    def TestEnvironmentProvider environmentProvider = new TestEnvironmentProvider()

    def outputPath = System.getProperty('java.io.tmpdir') + '/outputPathSpec.yml'

    def TemplateConfigurationSourceProvider provider = new TemplateConfigurationSourceProvider(
            new TestConfigSourceProvider(),
            environmentProvider,
            new DefaultSystemPropertiesProvider(),
            new TemplateConfigBundleConfiguration()
                    .outputPath(outputPath)
    )

    def 'rendered output is written to configured outputPath'() {
        given:
        def config = '''
                server:
                  applicationConnectors:
                    - type: http
                      port: ${PORT!8080}
                '''

        when:
        def parsedConfig = provider.open(config)
        def parsedConfigAsString = IOUtils.toString(parsedConfig)
        def configOnDiskAsString = Files.toString(new File(outputPath), Charsets.UTF_8)

        then:
        parsedConfigAsString equalTo(configOnDiskAsString)
        configOnDiskAsString containsString('port: 8080')

        cleanup:
        new File(outputPath).delete()
    }
}
