Trimou - Trim Your Mustache Templates!
======

[![Trimou site](https://img.shields.io/badge/www-trimou.org-orange.svg)](http://trimou.org/)
[![Travis CI Build Status](https://travis-ci.org/trimou/trimou.png)](https://travis-ci.org/trimou/trimou)
[![Maven Central](http://img.shields.io/maven-central/v/org.trimou/trimou-core.svg)](http://search.maven.org/#search|ga|1|trimou-core)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/trimou/trimou)


Trimou is a [Mustache](https://github.com/mustache) implementation written in Java. **Helpers API** inspired by [Handlebars.js](http://handlebarsjs.com/) is also supported. The goal is to provide a simple to use and easy to extend templating engine for any Java SE or Java EE application. 

There are some ready-to-use extensions which provide integration with [CDI](http://www.cdi-spec.org/), Servlets, [PrettyTime](http://ocpsoft.org/prettytime/),  [HtmlCompressor](http://code.google.com/p/htmlcompressor/), [google-gson](http://code.google.com/p/google-gson/), [Spring MVC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) and [Dropwizard](https://dropwizard.github.io/dropwizard/).

Get Started
-------------

All the artifacts are available in the Maven Central Repository:

```xml
<dependency>
  <groupId>org.trimou</groupId>
  <artifactId>trimou-core</artifactId>
  <version>1.8.1.Final</version>
</dependency>
```
And now use the `MustacheEngine` to compile a `Mustache` template and render the output:

```java
// We don't configure anything - use sensible defaults
MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();
// We provide the template contents - no caching and no template locators used
Mustache mustache = engine.compileMustache("hello", "Hello {{this}}!");
// Renders "Hello world!"
System.out.println(mustache.render("world"));
```

Examples
-------------

* a really [simple example](https://github.com/trimou/trimou/tree/master/examples/simple) 
* a little bit more complex [web application example](https://github.com/trimou/trimou/tree/master/examples/ping). 

Building Trimou
-------------

Simply run:

> $ mvn clean install

Microbenchmarks
---------------

![Example results](https://github.com/trimou/trimou-benchmarks/blob/master/trimou-microbenchmarks.png)

See also https://github.com/trimou/trimou-benchmarks
