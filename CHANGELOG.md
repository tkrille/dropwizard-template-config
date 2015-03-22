# Changelog

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
