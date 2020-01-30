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
    private String resourceIncludePath;
    private String fileIncludePath;
    private String outputPath;
    private Set<TemplateConfigVariablesProvider> customProviders = new LinkedHashSet<>();
    private Set<TemplateConfigHashesProvider> customHashesProviders = new LinkedHashSet<>();

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
        return Optional.fromNullable(resourceIncludePath);
    }

    /**
     * Get the configured resource include path (Default: None)
     */
    public Optional<String> resourceIncludePath() {
        return Optional.fromNullable(resourceIncludePath);
    }

    /**
     * Get the configured file include path (Default: None)
     */
    public Optional<String> fileIncludePath() {
        return Optional.fromNullable(fileIncludePath);
    }

    /**
     * Get the configured output path for the processed config (Default: None)
     */
    public Optional<String> outputPath() {
        return Optional.fromNullable(outputPath);
    }

    /**
     * Get the set of custom providers used to add variables to the configuration template (Default: Empty Set)
     */
    public Set<TemplateConfigVariablesProvider> customProviders() {
        return customProviders;
    }

    /**
     * Get the set of custom hashes providers used to add hashes to the configuration template (Default: Empty Set)
     */
    public Set<TemplateConfigHashesProvider> customHashesProviders() {
        return customHashesProviders;
    }

    /**
     * Set the path to include config snippets from
     *
     * <p>Must not be {@code null}. By default there's no value set.
     *
     * @deprecated Replaced by {@link #resourceIncludePath(String)}.
     */
    @Deprecated
    public TemplateConfigBundleConfiguration includePath(String includePath) {
        resourceIncludePath = includePath;
        fileIncludePath = null;
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
        if (fileIncludePath != null) {
            throw new IllegalStateException(
                "A value for fileIncludePath is already present; " +
                "only one of resourceIncludePath or fileIncludePath may be specified."
            );
        }
        this.resourceIncludePath = path;
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
        if (resourceIncludePath != null) {
            throw new IllegalStateException(
                "A value for resourceIncludePath is already present; " +
                "only one of resourceIncludePath or fileIncludePath may be specified."
            );
        }
        this.fileIncludePath = path;
        return this;
    }

    /**
     * Set the path to output the filled-out config
     *
     * <p>Must not be {@code null}. By default there's no value set.
     */
    public TemplateConfigBundleConfiguration outputPath(String outputPath) {
        this.outputPath = outputPath;
        return this;
    }

    /**
     * Add a custom provider used to add your own variables to the configuration template.
     */
    public TemplateConfigBundleConfiguration addCustomProvider(TemplateConfigVariablesProvider customProvider) {
        this.customProviders.add(customProvider);
        return this;
    }

    /**
     * Add a custom hashes provider used to add your own hashes to the configuration template.
     */
    public TemplateConfigBundleConfiguration addCustomHashesProvider(TemplateConfigHashesProvider customHashesProvider) {
        this.customHashesProviders.add(customHashesProvider);
        return this;
    }
}
