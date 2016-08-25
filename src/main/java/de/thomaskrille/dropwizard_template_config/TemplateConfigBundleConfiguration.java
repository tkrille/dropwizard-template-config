package de.thomaskrille.dropwizard_template_config;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;

import java.nio.charset.Charset;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The configuration for a {@link TemplateConfigBundle}
 */
public class TemplateConfigBundleConfiguration {

    private Charset charset = Charsets.UTF_8;
    private Optional<String> resourceIncludePath = Optional.absent();
    private Optional<String> fileIncludePath = Optional.absent();
    private Optional<String> outputPath = Optional.absent();
    private Optional<Set<TemplateConfigProvider>> customProviders = Optional.absent();

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
     * Get the configured resource include path (Default: None)
     *
     * @deprecated Replaced by {@link #resourceIncludePath}.
     */
    @Deprecated
    public Optional<String> includePath() {
        return resourceIncludePath;
    }

    /**
     * Get the configured resource include path (Default: None)
     */
    public Optional<String> resourceIncludePath() {
        return resourceIncludePath;
    }

    /**
     * Get the configured file include path (Default: None)
     */
    public Optional<String> fileIncludePath() {
        return fileIncludePath;
    }

    /**
     * Get the configured output path for the processed config (Default: None)
     */
    public Optional<String> outputPath() {
        return outputPath;
    }

    /**
     * Get the set of custom providers used to add variables to the configuration template (Default: None)
     */
    public Optional<Set<TemplateConfigProvider>> customProviders() { return customProviders; }

    /**
     * Set the path to include config snippets from
     *
     * <p>Must not be {@code null}. By default there's no value set.
     *
     * @deprecated Replaced by {@link #resourceIncludePath(String)}.
     */
    @Deprecated
    public TemplateConfigBundleConfiguration includePath(String includePath) {
        this.resourceIncludePath = Optional.of(includePath);
        return this;
    }

    /**
     * Set the resource path to include config snippets from
     *
     * <p>Must not be {@code null}. By default there's no value set.
     * Only one of {@code resourceIncludePath} or {@code fileIncludePath}
     * may be specified.
     *
     * @throws IllegalStateException if fileIncludePath is set
     */
    public TemplateConfigBundleConfiguration resourceIncludePath(String path) {
        if (this.fileIncludePath.isPresent()) {
            throw new IllegalStateException(
                "A value for fileIncludePath is already present; " +
                "only one of resourceIncludePath or fileIncludePath may be specified."
            );
        }
        this.resourceIncludePath = Optional.of(path);
        return this;
    }

    /**
     * Set the file path to include config snippets from
     *
     * <p>Must not be {@code null}. By default there's no value set.
     * Only one of {@code resourceIncludePath} or {@code fileIncludePath}
     * may be specified.
     *
     * @throws IllegalStateException if resourceIncludePath is already set
     */
    public TemplateConfigBundleConfiguration fileIncludePath(String path) {
        if (this.resourceIncludePath.isPresent()) {
            throw new IllegalStateException(
                "A value for resourceIncludePath is already present; " +
                "only one of resourceIncludePath or fileIncludePath may be specified."
            );
        }
        this.fileIncludePath = Optional.of(path);
        return this;
    }

    /**
     * Set the path to output the filled-out config
     *
     * <p>Must not be {@code null}. By default there's no value set.
     */
    public TemplateConfigBundleConfiguration outputPath(String outputPath) {
        this.outputPath = Optional.of(outputPath);
        return this;
    }

    /**
     * Add a custom provider used to add your own variables to the configuration template.
     */
    public TemplateConfigBundleConfiguration withCustomProvider(TemplateConfigProvider customProvider) {
        if (!this.customProviders.isPresent()) {
            // Want the set to preserve the order things are inserted into it so we have predictable behavior when keys
            // have the same names in the template.
            Set<TemplateConfigProvider> customProviderSet = new LinkedHashSet<>();
            this.customProviders = Optional.of(customProviderSet);
        }
        this.customProviders.get().add(customProvider);
        return this;
    }
}
