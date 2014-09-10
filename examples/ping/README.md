Trimou - Ping Example
=====================

This example web application aims to show the basic usage of Trimou, built-in extensions for [CDI](http://www.cdi-spec.org/), Servlet, [PrettyTime](http://ocpsoft.org/prettytime/) and [HtmlCompressor](http://code.google.com/p/htmlcompressor/), and [Rewrite](http://ocpsoft.org/rewrite/) integration.

* [Partials](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/webapp/templates/pingLogServlet.html#L8)
* [Template inheritance](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/webapp/templates/layout.html)
* Servlet built-in extension
    * [ServletContext template locator configuration](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/java/org/trimou/example/ping/MustacheEngineProducer.java)
    * [HttpServletRequestResolver in action](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/webapp/WEB-INF/templates/layout.html#L16)
* CDI built-in extension
    * [MustacheEngine producer](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/java/org/trimou/example/ping/MustacheEngineProducer.java)
    * [CDI bean resolver in action](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/webapp/templates/pingLogCdi.html#L8)
* PrettyTime built-in extension
    * [PrettyTime helper in action](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/webapp/templates/pingRow.html#L5)
* Minify built-in extension
    * [Minify html listener configuration](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/java/org/trimou/example/ping/MustacheEngineProducer.java)
* Rewrite integration
    * [MustacheConfigurationProvider](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/java/org/trimou/example/ping/MustacheConfigurationProvider.java)

The app contains two Java EE components - a servlet and a JAX-RS resource. Both provide the same functionality:

1. Store a "ping" request
2. Get "ping" requests HTML page

However, the JAX-RS resource is using CDI bean resolver whereas the servlet is not. Also note that the servlet is using the provided Writer instance.

Rewrite configuration enables you to get rendered templates directly at some specific path - in our example the web app root.

Build, deploy, test
-------------------

1. build
    mvn clean install
2. deploy the artifact target/trimou-ping.war to your favourite Java EE 6+ container (if you want to deploy the app to a servlet container you'll have to budle CDI implementation first, e.g. [Weld Servlet Uber Jar](http://search.maven.org/#search|ga|1|a%3A%22weld-servlet%22))
3. test the application, e.g. using wget and curl tools

To test servlet components:

    curl -X POST http://localhost:8080/trimou-ping/ping
    wget -O - http://localhost:8080/trimou-ping/ping

To test JAX-RS components:

    curl -X POST http://localhost:8080/trimou-ping/rest/ping
    wget -O - http://localhost:8080/trimou-ping/rest/ping

To test Rewrite configuration:

    wget -O - http://localhost:8080/trimou-ping/pingLogCdi.html
    (you should get the same result as for the JAX-RS resource)

To display simple stats:

    wget -O - http://localhost:8080/trimou-ping/rest/stats
