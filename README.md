Trimou - Trim Your Mustache Templates!
======

[![Join the chat at https://gitter.im/trimou/trimou](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/trimou/trimou?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![Travis CI Build Status](https://travis-ci.org/trimou/trimou.png)](https://travis-ci.org/trimou/trimou)
[![Maven Cetnral](http://img.shields.io/maven-central/v/org.trimou/trimou-core.svg)](http://search.maven.org/#search|ga|1|trimou-core)


Trimou is a Mustache implementation written in Java. **Helpers API** inspired by [Handlebars.js](http://handlebarsjs.com/) is also supported. The goal is to provide a simple to use and easy to extend templating engine for any Java SE or Java EE application. 

There are some ready-to-use extensions which provide integration with [CDI](http://www.cdi-spec.org/), Servlets, [PrettyTime](http://ocpsoft.org/prettytime/),  [HtmlCompressor](http://code.google.com/p/htmlcompressor/), [google-gson](http://code.google.com/p/google-gson/), [Spring MVC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) and [Dropwizard](https://dropwizard.github.io/dropwizard/).

See also the project site: [http://trimou.org](http://trimou.org "Trimou Site")

Don't forget to check the example web application: https://github.com/trimou/trimou/tree/master/examples/ping

Building Trimou
-------------

Simply run:

> $ mvn clean install

Microbenchmarks
---------------

![Example results](https://github.com/trimou/trimou-benchmarks/blob/master/trimou-microbenchmarks.png)

See also https://github.com/trimou/trimou-benchmarks
