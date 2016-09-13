package de.thomaskrille.dropwizard_template_config

import com.google.common.base.*
import spock.lang.Specification

class BundleCreationSpec extends Specification {

    def 'using the default constructor creates bundle with default config'() {
        when:
        def bundle = new TemplateConfigBundle()

        then:
        bundle.charset == Charsets.UTF_8
        bundle.resourceIncludePath == Optional.absent()
        bundle.fileIncludePath == Optional.absent()
        bundle.customProviders.size() == 0
    }

    def 'a specific configuration can be applied'() {
        when:
        def bundle = new TemplateConfigBundle(
                new TemplateConfigBundleConfiguration()
                        .charset(Charsets.US_ASCII)
                        .resourceIncludePath('includePath')
        )

        then:
        bundle.charset == Charsets.US_ASCII
        bundle.resourceIncludePath.get() == 'includePath'
    }

    def 'custom providers can be added'() {
        when:
        def providerA = new TestCustomProvider("providerA")
        def providerB = new TestCustomProvider("providerB")
        def bundle = new TemplateConfigBundle(
                new TemplateConfigBundleConfiguration().addCustomProvider(providerB).addCustomProvider(providerA)
        )

        then:
        bundle.customProviders.containsAll([providerA, providerB])
        bundle.customProviders.size() == 2
    }
}
