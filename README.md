# Trimou - Trim Your Mustache Templates!

[![Trimou site](https://img.shields.io/badge/www-trimou.org-orange.svg)](http://trimou.org/)
[![Maven Central](http://img.shields.io/maven-central/v/org.trimou/trimou-core.svg)](http://search.maven.org/#search|ga|1|trimou-core)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/trimou/trimou)
[![License](https://img.shields.io/badge/license-Apache%20License%202.0-yellow.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Trimou is a templating engine.
It's a [Mustache](https://github.com/mustache) implementation but **Helpers API** inspired by [Handlebars.js](http://handlebarsjs.com/) is also supported.
The goal is to provide a simple to use and easy to extend templating engine for any Java SE or Java EE application.

There are some ready-to-use extensions which provide integration with [CDI](http://www.cdi-spec.org/), Servlets, [PrettyTime](http://ocpsoft.org/prettytime/),  [HtmlCompressor](http://code.google.com/p/htmlcompressor/), [google-gson](http://code.google.com/p/google-gson/), [JSON Processing Object Model API (JSR 353)](https://jsonp.java.net/), [Spring MVC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html), [Dropwizard](https://dropwizard.github.io/dropwizard/) and [EL 3.0](https://uel.java.net/).

## Get Started

All the artifacts are available in the Maven Central Repository:

```xml
<dependency>
  <groupId>org.trimou</groupId>
  <artifactId>trimou-core</artifactId>
  <version>${version.trimou}</version>
</dependency>
```
Use the `MustacheEngineBuilder` to build a `MustacheEngine` - a central point for template management.

```java
// Build the engine - don't configure anything but use sensible defaults
MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();
// Compile the template - no caching and no template locators used
Mustache mustache = engine.compileMustache("Hello {{this}}!");
// Render "Hello world!"
System.out.println(mustache.render("world"));
```

## Examples

* a really [simple example](https://github.com/trimou/trimou/tree/master/examples/simple)
* a little bit more complex [web application example](https://github.com/trimou/trimou/tree/master/examples/ping).
* a small example using [Spring Boot auto-configuration](https://github.com/trimou/trimou/tree/master/examples/spring-boot)

## Building Trimou

Simply run:

> $ mvn clean install
