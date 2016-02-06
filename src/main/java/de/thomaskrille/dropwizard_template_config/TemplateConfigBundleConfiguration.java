package de.thomaskrille.dropwizard_template_config;

import com.google.common.base.Charsets;

import java.nio.charset.Charset;

/**
 * The configuration for a {@link TemplateConfigBundle}
 */
public class TemplateConfigBundleConfiguration {

    private Charset charset = Charsets.UTF_8;

    /**
     * Get the configured charset
     */
    public Charset charset() {
        return charset;
    }

    /**
     * Set the {@link Charset} used to load, process, and output the config template.
     */
    public TemplateConfigBundleConfiguration charset(Charset charset) {
        this.charset = charset;
        return this;
    }
}
