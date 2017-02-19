# Changelog

## 1.5.0 (2017-02-19)

### Features

- Add ability to define custom variables. Use
  `TemplateConfigBundleConfiguration.addCustomProvider(TemplateConfigVariablesProvider customProvider)`
  to add your custom variables provider. See the [tutorial](README.md#tutorial) for details.

### Changes

- Remove deprecated constructor `TemplateConfigBundle(java.nio.charset.Charset)`.

## 1.4.0 (2016-05-21)

### Features

- Allow inclusion of config snippets from the file system. Use
  `TemplateConfigBundleConfiguration.fileIncludePath(java.lang.String)` to set the base include
  directory. See the [tutorial](README.md#tutorial) for details.

### Changes

- Remove deprecated constructor `TemplateConfigurationSourceProvider.TemplateConfigurationSourceProvider`.

### Deprecations

- Method `TemplateConfigBundleConfiguration.includePath` has been replaced with
  `TemplateConfigBundleConfiguration.resourceIncludePath`. It will be removed
  in 1.7.0 or 2.0.0.

## 1.3.0 (2016-05-21)

### Features

- Environment variables and system properties are now accessible without the `env.` or `sys.` prefixes.
  So instead of writing `${env.PORT}` you can now just write `${PORT}`.
  Environment variables overwrite system properties, if they have the same name.
  `env.` and `sys.` are still supported for backwards compatibility and to access system properties
  with special characters in their name.

- Write the rendered `config.yaml` to a file for debugging and testing.
  Use `TemplateConfigBundleConfiguration.outputPath` to set the path to the file.
  It is not recommended to use this in production, as it could leak sensitive information.

## 1.2.0 (2016-03-03)

### Features

- Allow inclusion of config snippets from classpath. See the
  [tutorial](README.md#tutorial) for details.

### Changes

- Configuration of the bundle now uses a new configuration class. The
  former API has been deprecated. Please, update your code. See
  [the docs](https://github.com/tkrille/dropwizard-template-config#setup)
  for further details.

- Compile against Dropwizard 0.9.2

### Deprecations

- Constructor `TemplateConfigBundle(final Charset charset)` has been
  replaced with the new configuration means. It will be removed in
  1.5.0 or 2.0.0.

## 1.1.0 (2015-03-22)

### Features

- make encoding of configuration template and output configurable

    see https://github.com/tkrille/dropwizard-template-config#setup for how to
    configure this.

### Fixes

- default number format not suitable for YAML output

    you can now just write `${env.PORT!8080}` without having to specify the
    number as a `String`.

### Changes

- bump Freemarker to 2.3.22
- use `UTF-8` as default encoding for configuration templates

## 1.0.0 (2015-02-13)

**Initial Version**

### Features

- write your config.yaml as a Freemarker template.
