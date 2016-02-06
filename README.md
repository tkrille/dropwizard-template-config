# Dropwizard Template Config

A Dropwizard Bundle that allows you to write your `config.yaml` as a
[Freemarker](http://freemarker.org) template. This is especially useful when you
need to access environment variables or system properties. In fact, this project
is the successor to the fabulous [dropwizard-environment-config]
(https://github.com/tkrille/dropwizard-environment-config) plugin.

## Setup

First add the dependency to your POM:

```xml
<dependency>
    <groupId>de.thomaskrille</groupId>
    <artifactId>dropwizard-template-config</artifactId>
    <version>1.1.0</version>
</dependency>
```

To enable, simply add the `TemplateConfigBundle` to the `Bootstrap` object in
your `initialize` method:

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

Look at `TemplateConfigBundleConfiguration`'s javadoc to see all available options.

**Heads up:** The Bundle gets the content of the `config.yaml` by wrapping any
previously defined `io.dropwizard.configuration.ConfigurationSourceProvider`.
So you must set any custom `ConfigurationSourceProvider` before adding
this `Bundle` to the `Bootstrap`.

## Quickstart

Environment variables and system properties can be specified in `config.yaml`
by using the following Freemarker magic:

```yaml
server:
  type: simple
  connector:
    type: http
    # replacing environment variables
    port: ${env.PORT}
logging:
  # with default values too
  level: ${env.LOG_LEVEL!'WARN'}
  appenders:
    # system properties also work
    - type: ${sys.log_appender!'console'}
```

See [Freemarker's Template Author's Guide]
(http://freemarker.org/docs/dgui.html) for more information on how to
write templates.

## Tutorial

Using this bundle you can write your `config.yaml` as [Freemarker]
(http://freemarker.org) template. Let's start with a simple example: replacing
environment variables. For all the Heroku users out there, we will try to use
Heroku's environment variables for the examples.

Environment variables can be accessed by using the `env` variable:

```yaml
server:
  type: simple
  connector:
    type: http
    port: ${env.PORT}
```

As you can see, to output the value of a variable you use `${variable}`. The
`env` variable is set during startup and contains the application's environment
as a `Map`. You can also specify a default value in case the environment
variable is missing. This useful for local tests on your development machine:

```yaml
server:
  type: simple
  connector:
    type: http
    port: ${env.PORT!8080}
```

Default values are separated from the variable name by a `!` and follow more or
less the well-known Java syntax for scalars. If there is no default value for
a missing variable an exception will be thrown by Freemarker and wrapped in
a `RuntimeException`.

Not only environment variables, but system properties are available, too:

```yaml
server:
  type: simple
  connector:
    type: http
    port: ${sys.http_port}
```

Here we are using the `sys` variable that's set on startup, too, and initialized
to `System.getProperties()`. System properties also work with default values:

```yaml
server:
  type: simple
  connector:
    type: http
    port: ${sys.http_port!8080}
```

You can even use properties with a `.` in their name:

```yaml
server:
  type: simple
  connector:
    type: http
    port: ${sys['my_app.http.port']}
```

This approach works for other problematic characters too, such as `-`. And of
course, it also works with environment variables.

You can output variables inline in values. This is helpful to specify the
database connection:

```yaml
database:
  driverClass: org.postgresql.Driver
  user: ${env.DB_USER}
  password: ${env.DB_PASSWORD}
  url: jdbc:postgresql://${env.DB_HOST!'localhost'}:${env.DB_PORT}/my-app-db
```

In fact, you can output anything anywhere, because Freemarker doesn't know
anything about YAML:

```yaml
#
# Given the following environment:
#
# SERVER_TYPE_LINE='type: simple'
# SERVER_CONNECTOR_TYPE_LINE='type: http'
#
server:
  ${env.SERVER_TYPE_LINE}
  connector:
    ${env.SERVER_CONNECTOR_TYPE_LINE}
    port: 8080
```

Be careful though, not to mess up YAML's data structure. Of course, you can use
any other Freemarker features beyond simple variable interpolation:

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
      port: ${env.PORT!8080}
<#if env.ENABLE_SSL == 'true'>
    - type: https
      port: ${env.SSL_PORT!8443}
      keyStorePath: ${env.SSL_KEYSTORE_PATH}
      keyStorePassword: ${env.SSL_KEYSTORE_PASS}
</#if>
```

The previous example conditionally enables HTTPS if the environment variable
`ENABLE_SSL` is `true`. Another advanced use case might be introducing
configuration profiles that can be switched by using an environment variable
like this:

```yaml
logging:
<#if env.PROFILE == 'production'>
  level: WARN
  loggers:
    com.example.my_app: INFO
    org.hibernate.SQL: OFF
  appenders:
    - type: syslog
      host: localhost
      facility: local0
<#elseif env.PROFILE == 'development'>
  level: INFO
  loggers:
    com.example.my_app: DEBUG
    org.hibernate.SQL: DEBUG
  appenders:
    - type: console
</#if>
```

Be careful to not overuse all this stuff. In the end, a configuration file
should stay as simple as possible and be easily readable. Extensively using
advanced Freemarker features might get in the way of this principle.

See [Freemarker's Template Author's Guide]
(http://freemarker.org/docs/dgui.html) for more information on how to
write templates.

## Migration from Dropwizard Environment Config

TODO: write me!

## Under the Hood

TODO: write me!

## Copyright Notice

This project is licensed under the Apache License, Version 2.0, January 2004,
and uses the following 3rd party software

- Dropwizard

    Copyright 2010-2015, Coda Hale, Yammer Inc.

- Freemarker

    Copyright 2002-2015, The FreeMarker Project, Attila Szegedi, Daniel Dekany,
    Jonathan Revusky


See LICENSE-3RD-PARTY and NOTICE-3RD-PARTY for the individual 3rd parties.
