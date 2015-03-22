# Changelog

## Unreleased

### Fixes

- default number format not suitable for YAML output

    you can now just write `${env.PORT!8080}` without having to specify the
    number as a `String`.

### Changes

- bump Freemarker to 2.3.22

## 1.0.0 (2015-02-13)

**Initial Version**

### Features

- write your config.yaml as a Freemarker template.
