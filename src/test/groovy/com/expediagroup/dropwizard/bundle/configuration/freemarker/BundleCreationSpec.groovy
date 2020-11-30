package com.expediagroup.dropwizard.bundle.configuration.freemarker

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
        def bundle = new TemplateConfigBundle(
                new TemplateConfigBundleConfiguration()
                        .addCustomProvider(providerB)
                        .addCustomProvider(providerA)
        )

        then:
        bundle.configuration.customProviders.containsAll([providerA, providerB])
        bundle.configuration.customProviders.size() == 2
    }
}
