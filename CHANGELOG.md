# Changelog

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
