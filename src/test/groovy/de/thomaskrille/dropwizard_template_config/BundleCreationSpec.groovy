package de.thomaskrille.dropwizard_template_config

import com.google.common.base.*
import spock.lang.Specification

class BundleCreationSpec extends Specification {

    def 'using the default constructor creates bundle with default config'() {
        when:
        def bundle = new TemplateConfigBundle()

        then:
        bundle.configuration.charset == Charsets.UTF_8
        bundle.configuration.resourceIncludePath == null
        bundle.configuration.fileIncludePath == null
        bundle.configuration.customProviders.size() == 0
    }

    def 'a specific configuration can be applied'() {
        when:
        def bundle = new TemplateConfigBundle(
                new TemplateConfigBundleConfiguration()
                        .charset(Charsets.US_ASCII)
                        .resourceIncludePath('includePath')
        )

        then:
        bundle.configuration.charset == Charsets.US_ASCII
        bundle.configuration.resourceIncludePath == 'includePath'
    }

    def 'custom providers can be added'() {
        when:
        def providerA = new TestCustomProvider("providerA")
        def providerB = new TestCustomProvider("providerB")
        def providerHashesA = new TestCustomHashesProvider("providerHashesA")
        def providerHashesB = new TestCustomHashesProvider("providerHashesB")
        def bundle = new TemplateConfigBundle(
                new TemplateConfigBundleConfiguration()
                        .addCustomProvider(providerB)
                        .addCustomProvider(providerA)
                        .addCustomHashesProvider(providerHashesA)
                        .addCustomHashesProvider(providerHashesB)
        )

        then:
        bundle.configuration.customProviders.containsAll([providerA, providerB])
        bundle.configuration.customProviders.size() == 2
        bundle.configuration.customHashesProviders.containsAll([providerHashesA, providerHashesB])
        bundle.configuration.customHashesProviders.size() == 2
    }
}
