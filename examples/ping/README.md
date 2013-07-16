Trimou - Ping Example
=====================

This example web application aims to show the basic usage of Trimou and built-in extensions for [CDI](http://www.cdi-spec.org/), Servlet and [PrettyTime integration](http://ocpsoft.org/prettytime/).

* [Partials](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/webapp/WEB-INF/templates/pingLogServlet.html#L8)
* [Template inheritance](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/webapp/WEB-INF/templates/layout.html)
* Servlet integration
** [ServletContext template locator configuration](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/java/org/trimou/example/ping/MustacheEngineProducer.java#L29)
** [HttpServletRequestResolver in action](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/webapp/WEB-INF/templates/layout.html#L16)
* CDI integration
** [MustacheEngine producer](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/java/org/trimou/example/ping/MustacheEngineProducer.java)
** [CDI bean resolver in action](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/webapp/WEB-INF/templates/pingLogCdi.html#L8)
* PrettyTime integration
** [PrettyTime resolver in action](https://github.com/trimou/trimou/blob/master/examples/ping/src/main/webapp/WEB-INF/templates/pingRow.html#L5)

The app contains two Java EE components - a servlet and a JAX-RS resource. Both provide the same functionality:

1. Store a "ping" request
2. Get "ping" requests HTML page

However the JAX-RS resource is using CDI bean resolver whereas the servlet is not. Also note that the servlet is using the provided Writer instance.

Build, deploy, test
-------------------

1. build
    mvn clean install
2. deploy the artifact target/trimou-ping.war to your favourite Java EE 6+ container (if you want to deploy the app to a servlet container you'll have to budle CDI implementation first, e.g. [Weld Servlet (Uber Jar)](http://search.maven.org/#search|ga|1|a%3A%22weld-servlet%22))
3. test the application, e.g. using wget and curl tools

To test servlet components:
    curl -X POST http://localhost:8080/trimou-ping/ping
    wget -O - http://localhost:8080/trimou-ping/ping

To test JAX-RS components:
    curl -X POST http://localhost:8080/trimou-ping/rest/ping
    wget -O - http://localhost:8080/trimou-ping/rest/ping
