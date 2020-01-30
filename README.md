# Dropwizard Template Config [![Circle CI](https://circleci.com/gh/tkrille/dropwizard-template-config/tree/master.svg?style=svg)](https://circleci.com/gh/tkrille/dropwizard-template-config/tree/master)

A Dropwizard Bundle that allows you to write your `config.yaml` as a [Freemarker](http://freemarker.org) template.
This is especially useful when you need to access environment variables or system properties.
In fact, this project is the successor to the fabulous [dropwizard-environment-config](https://github.com/tkrille/dropwizard-environment-config) plugin.

## Setup

First add the dependency to your POM:

```xml
<dependency>
    <groupId>de.thomaskrille</groupId>
    <artifactId>dropwizard-template-config</artifactId>
    <version>1.5.0</version>
</dependency>
```

Your project is assumed to separately pull in `dropwizard-core` version 0.7.1 or newer.
Compatibility has been tested through Dropwizard 1.0.0.

To enable, simply add the `TemplateConfigBundle` to the `Bootstrap` object in your `initialize` method:

```java
@Override
public void initialize(final Bootstrap<Configuration> bootstrap) {
    ...
    bootstrap.addBundle(new TemplateConfigBundle());
    ...
}
```

You can configure the bundle by passing an instance of `TemplateConfigBundleConfiguration` to the constructor:

```java
@Override
public void initialize(final Bootstrap<Configuration> bootstrap) {
    ...
    bootstrap.addBundle(new TemplateConfigBundle(
            new TemplateConfigBundleConfiguration().charset(Charsets.US_ASCII)
    ));
    ...
}
```

You can add your own variables to the template by adding your own implementations of the `TemplateConfigProvider`
to the constructor:

```java
@Override
public void initialize(final Bootstrap<Configuration> bootstrap) {
    ...
    bootstrap.addBundle(new TemplateConfigBundle(
        new TemplateConfigBundleConfiguration()
            .addCustomProvider(myCustomProvider1)
            .addCustomProvider(myCustomProvider2)
            .addCustomHashesProvider(myCustommHashesProvider2)
            .addCustomHashesProvider(myCustommHashesProvider2)
    ));
    ...
}
```

Look at `TemplateConfigBundleConfiguration`'s javadoc to see all available options.

**Heads up:** The Bundle gets the content of the `config.yaml` by wrapping any previously defined
`io.dropwizard.configuration.ConfigurationSourceProvider`.
So you must set any custom `ConfigurationSourceProvider` before adding this `Bundle` to the `Bootstrap`.

## Quickstart

Environment variables and system properties can be specified in `config.yaml` by using the following
Freemarker magic:

```yaml
server:
  type: simple
  connector:
    type: http
    # replacing environment variables
    port: ${PORT}
logging:
  # with default values too
  level: ${LOG_LEVEL!'WARN'}
  appenders:
    # system properties also work
    - type: ${log_appender!'console'}
```

See [Freemarker's Template Author's Guide](http://freemarker.org/docs/dgui.html) for more information
on how to write templates.

## Tutorial

Using this bundle you can write your `config.yaml` as [Freemarker](http://freemarker.org) template.
Let's start with a simple example: replacing environment variables.
For all the Heroku users out there, we will try to use Heroku's environment variables for the examples.
Let's focus on a single piece of the configuration shown in the [Quickstart](#quickstart):

```yaml
server:
  type: simple
  connector:
    type: http
    port: ${PORT}
```

You can specify a default value in case the environment variable is missing.
This is useful for local tests on your development machine:

```yaml
server:
  type: simple
  connector:
    type: http
    port: ${PORT!8080}
```

Default values are separated from the variable name by a `!` and follow more or less the well-known
Java syntax for scalars.
If there is no default value for a missing variable an exception will be thrown by Freemarker and wrapped
in a `RuntimeException`.
Java system properties (the contents of `System.getProperties()`) are available, too:

```yaml
server:
  type: simple
  connector:
    type: http
    port: ${http_port}
```

There are some limitations to the variables and properties you can access through these top-level variables.
For one, environment variables and system properties with the same name will collide (environment variables
will mask system properties in this case).
For another, names containing [characters such as `.` and `-` that have special meaning to Freemarker]
(http://freemarker.org/docs/dgui_template_exp.html#dgui_template_exp_var_toplevel) won't be available.
To alleviate these problems, this bundle also provides `Map`s for the environment (`env`) and system
properties (`sys`) at the top level:

```yaml
# Use `sys` to access a system property masked by an environment variable
port: ${sys.http_port}

# Use bracket notation to access a system property with a `.` in its name
port: ${sys['my_app.http.port']}

# Names with a dash (`-`) can be accessed via brackets or backslash
port: ${sys['http-port']}
port: ${http\-port}
```

You can output variables inline in values.
This is helpful to specify the database connection:

```yaml
database:
  driverClass: org.postgresql.Driver
  user: ${DB_USER}
  password: ${DB_PASSWORD}
  url: jdbc:postgresql://${DB_HOST!'localhost'}:${DB_PORT}/my-app-db
```

In fact, you can output anything anywhere, because Freemarker doesn't know anything about YAML:

```yaml
#
# Given the following environment:
#
# SERVER_TYPE_LINE='type: simple'
# SERVER_CONNECTOR_TYPE_LINE='type: http'
#
server:
  ${SERVER_TYPE_LINE}
  connector:
    ${SERVER_CONNECTOR_TYPE_LINE}
    port: 8080
```

Be careful though, not to mess up YAML's data structure.
Of course, you can use any other Freemarker features beyond simple variable interpolation:

```yaml
# Use with:
#
# ENABLE_SSL=true
# SSL_PORT=8443 (optional)
# SSL_KEYSTORE_PATH=/etc/my-app/keystore
# SSL_KEYSTORE_PASS=secret
#
# or
#
# ENABLE_SSL=false
#
server:
  applicationConnectors:
    - type: http
      port: ${PORT!8080}
<#if ENABLE_SSL == 'true'>
    - type: https
      port: ${SSL_PORT!8443}
      keyStorePath: ${SSL_KEYSTORE_PATH}
      keyStorePassword: ${SSL_KEYSTORE_PASS}
</#if>
```

The previous example conditionally enables HTTPS if the environment variable `ENABLE_SSL` is `true`.
Comments are available too:

```yaml
server:
  applicationConnectors:
    - type: http
      port: ${PORT!8080}
<#-- Un-comment to enable HTTPS
    - type: https
      port: ${SSL_PORT!8443}
      keyStorePath: ${SSL_KEYSTORE_PATH}
      keyStorePassword: ${SSL_KEYSTORE_PASS}
-->
```

Comments are writen between the `<#-- -->` Freemarker tags.
They can span multiple lines.
Another advanced use case might be introducing configuration profiles that can be switched by using
an environment variable like this:

```yaml
logging:
<#if PROFILE == 'production'>
  level: WARN
  loggers:
    com.example.my_app: INFO
    org.hibernate.SQL: OFF
  appenders:
    - type: syslog
      host: localhost
      facility: local0
<#elseif PROFILE == 'development'>
  level: INFO
  loggers:
    com.example.my_app: DEBUG
    org.hibernate.SQL: DEBUG
  appenders:
    - type: console
</#if>
```


You can include snippets from external config files via the `<#include>` tag.
For this to work, you have to set an include directory
based either on a resource in the classpath:

```java
@Override
public void initialize(final Bootstrap<Configuration> bootstrap) {
    ...
    bootstrap.addBundle(new TemplateConfigBundle(
            new TemplateConfigBundleConfiguration().resourceIncludePath("/config")
    ));
    ...
}
```

or on the local filesystem:

```java
@Override
public void initialize(final Bootstrap<Configuration> bootstrap) {
    ...
    bootstrap.addBundle(new TemplateConfigBundle(
            new TemplateConfigBundleConfiguration().fileIncludePath("./config")
    ));
    ...
}
```

Feel free to use sub-folders to organize your configurations.
While the argument to `fileIncludePath` may be relative to the current working
directory or absolute, the argument to `resourceIncludePath` will always
be made absolute by prepending a slash (`/`) if not present.
You can then include configuration templates from other files.
To extract the database config, for example, create a config like this:

```yaml
server:
  type: simple
  connector:
    type: http
    port: 8080

<#include "database.yaml">

logging:
  level: WARN
```

The `database.yaml` looks like this:

```yaml
database:
  driverClass: org.postgresql.Driver
  user: my-app
  password: secret
  url: jdbc:postgresql://localhost:5432/my-app-db
```

The result will look like this:

```yaml
server:
  type: simple
  connector:
    type: http
    port: 8080

database:
  driverClass: org.postgresql.Driver
  user: my-app
  password: secret
  url: jdbc:postgresql://localhost:5432/my-app-db

logging:
  level: WARN
```

Of course, you can also use any templating feature in the included file, like:

- accessing environment variables
- using conditionals
- including additional files

If you're not seeing the behavior you expect, it can be useful to inspect the rendered text of your template.
Since 1.3.0, you can provide an `outputPath` to which the bundle will write the filled-out text of the
config before passing it on to Dropwizard:

```java
@Override
public void initialize(final Bootstrap<Configuration> bootstrap) {
    ...
    bootstrap.addBundle(new TemplateConfigBundle(
            new TemplateConfigBundleConfiguration().outputPath("/tmp/config.yml")
    ));
    ...
}
```

Be careful to not overuse all this stuff.
In the end, a configuration file should stay as simple as possible and be easily readable.
Extensively using advanced Freemarker features might get in the way of this principle.

See [Freemarker's Template Author's Guide](http://freemarker.org/docs/dgui.html) for more information
on how to write templates.

## Migration from Dropwizard Environment Config

TODO: write me!

## Copyright Notice

This project is licensed under the Apache License, Version 2.0, January 2004, and uses the following
3rd party software:

- Dropwizard

    Copyright 2010-2015, Coda Hale, Yammer Inc.

- Freemarker

    Copyright 2002-2015, The FreeMarker Project, Attila Szegedi, Daniel Dekany,
    Jonathan Revusky

See LICENSE-3RD-PARTY and NOTICE-3RD-PARTY for the individual 3rd parties.
