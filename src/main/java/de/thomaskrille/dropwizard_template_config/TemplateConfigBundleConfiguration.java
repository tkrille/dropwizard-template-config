package de.thomaskrille.dropwizard_template_config;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;

import java.nio.charset.Charset;

/**
 * The configuration for a {@link TemplateConfigBundle}
 */
public class TemplateConfigBundleConfiguration {

    private Charset charset = Charsets.UTF_8;
    private Optional<String> includePath = Optional.absent();

    /**
     * Get the configured charset (Default: UTF-8)
     */
    public Charset charset() {
        return charset;
    }

    /**
     * Set the {@link Charset} used to load, process, and output the config template
     *
     * <p>The default is UTF-8.
     */
    public TemplateConfigBundleConfiguration charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * Get the configured include path (Default: None)
     */
    public Optional<String> includePath() {
        return includePath;
    }

    /**
     * Set the path to include config snippets from
     *
     * <p>Must be not {@code null}. By default there's no value set.
     */
    public TemplateConfigBundleConfiguration includePath(String includePath) {
        this.includePath = Optional.of(includePath);
        return this;
    }
}
