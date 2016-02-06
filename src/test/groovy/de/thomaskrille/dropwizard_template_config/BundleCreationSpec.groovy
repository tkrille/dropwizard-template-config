package de.thomaskrille.dropwizard_template_config

import com.google.common.base.*
import spock.lang.Specification

class BundleCreationSpec extends Specification {

    def 'using the default constructor creates bundle with default config'() {
        when:
        def bundle = new TemplateConfigBundle()

        then:
        bundle.charset == Charsets.UTF_8
    }

    def 'a specific configuration can be applied'() {
        when:
        def bundle = new TemplateConfigBundle(new TemplateConfigBundleConfiguration().charset(Charsets.US_ASCII))

        then:
        bundle.charset == Charsets.US_ASCII
    }
}
