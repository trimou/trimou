# Trimou - Trim Your Mustache Templates!

[![Trimou site](https://img.shields.io/badge/www-trimou.org-orange.svg)](http://trimou.org/)
[![Travis CI Build Status](https://travis-ci.org/trimou/trimou.png)](https://travis-ci.org/trimou/trimou)
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
Use the `MustacheEngineFactory` to build a `MustacheEngine` - a central point for template management.

```java
// Don't configure anything - use sensible defaults
MustacheEngine engine = MustacheEngineFactory.defaultEngine();
// Provide the template contents - no caching and no template locators used
Mustache mustache = engine.compileMustache("hello", "Hello {{this}}!");
// Renders "Hello world!"
System.out.println(mustache.render("world"));
```

## Some useful features

### Template locators and caching

Allow to automatically locate the template contents for the given template id so that it’s not necessary to supply the template contents every time the template is compiled. Moreover, if the template cache is enabled (default) the compiled template is automatically put in the cache and no compilation happens the next time the template is requested:

```java
MustacheEngine engine = MustacheEngineBuilder
        .newBuilder()
        .addTemplateLocator(FilesystemTemplateLocator.builder().setRootPath("/home/trimou/templates").setSuffix("html").build())
        .build();
// Whenever the template is needed, obtain the reference from the engine
Mustache mustache = engine.getMustache("foo");
```
See also [TemplateLocator](http://trimou.org/doc/latest.html#template_locator) and [Configuration properties](http://trimou.org/doc/latest.html#configuration).

### Helpers

Helpers are de-facto *tags which are able to consume multiple parameters and optional hash map*. Trimou's Helper API is inspired by Handlebars but it’s not 100% compatible. Mainly, it does not define the "inverse" section. On the other hand, any helper is able to validate its tag definition (see `org.trimou.handlebars.Helper.validate()`) and fail fast if there's invalid number of arguments etc.

#### Built-in helpers

Five built-in helpers are registered automatically: `if`, `unless`, `each`, `with` and `is`. Some of them have extended functionality, e.g. for `if` helper multiple params may be evaluated and an optional `else` (which supports simple value expressions) may be also specified:
```
{{#if item.active item.valid logic="or"}}
  {{item.name}} is active or valid!
{{/if}}
{{#if item.active else="{item.id} is inactive!"}}
  {{item.id}} with name {{item.name}} is active!
{{/if}}
```
`each` helper allows to supply an alias to access the value of the current iteration:
```
{{#each items as='item'}}
  {{! Show the current iteration index (the first element has index 1)}}
  {{index}}. 
  {{item.name}}
{{/each}}
```
`each` helper could be also used to iterate over multiple objects:
````
{{! First iterate over list1 and then iterate over list2}}
{{#each list1 list2}}
  {{name}}
{{/each}}
```

Trimou also provides a lot of useful helpers which are not registered automatically - see also [Built-in helpers](http://trimou.org/doc/latest.html#helpers).

##### ResourceBundleHelper

It's a way to use `java.util.ResourceBundle` in your templates:
```java
MustacheEngine engine = MustacheEngineBuilder
                           .newBuilder()
                           .registerHelper("msg", new ResourceBundleHelper("messages"))
                           .build();
```
Properties file:
```
helloworld=Hello world %s!
```
Template:
```
{{msg "helloworld" "Martin"}}
```

##### FormatHelper

A simple printf-style format helper.
The first param represents a format string and other params are arguments (referenced by the format specifiers):

```
{{fmt '%tA' now locale='en'}}
```

##### DateTimeFormatHelper

Format dates easily:
```java
MustacheEngine engine = MustacheEngineBuilder
                           .newBuilder()
                           .registerHelper("formatTime", new DateTimeFormatHelper())
                           .build();
```
Template:
```
{{formatTime now pattern="DD-MM-yyyy HH:mm"}}
```

##### LogHelper

First register the helper instance:
```java
MustacheEngineBuilder.newBuilder().registerHelper("log", LogHelper.builder().setDefaultLevel(Level.WARN).build()).build();
```
Than use the helper in the template:
```
{{#each items}}
  {{#unless active}}
    {{! Log a warning if an inactive item is found}}{{log "An inactive item found: {}" name}}
  {{/unless}}
{{/each}}
```

##### NumericExpressionHelper

A simple numeric expression helper:

```
{{#numExpr foo.price "90" op="gt"}}
foo.price evaluates to a number > 90
{{/numExpr}}
```

It's also possible to specify the default operator so that the `op` param may be ommitted:
```
{{#gt val 10}}
val > 10
{{/gt}}
```

##### InvokeHelper

Invokes public methods with parameters via reflection. In this case `java.util.concurrent.TimeUnit.valueOf("MILLISECONDS").toSeconds(1000l)`:

```
{{#invoke 'MILLISECONDS' class='java.util.concurrent.TimeUnit' m='valueOf'}}{{invoke 1000L m='toSeconds'}}{{/invoke}}
```

## Examples

* a really [simple example](https://github.com/trimou/trimou/tree/master/examples/simple)
* a little bit more complex [web application example](https://github.com/trimou/trimou/tree/master/examples/ping).
* a small example using [Spring Boot auto-configuration](https://github.com/trimou/trimou/tree/master/examples/spring-boot)

## Building Trimou

Simply run:

> $ mvn clean install

## Microbenchmarks

![Example results](https://github.com/trimou/trimou-benchmarks/blob/master/trimou-microbenchmarks.png)

See also https://github.com/trimou/trimou-benchmarks
