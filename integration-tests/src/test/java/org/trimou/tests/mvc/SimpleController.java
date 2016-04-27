package org.trimou.tests.mvc;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.annotation.Controller;
import javax.mvc.annotation.View;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 *
 * @author Martin Kouba
 */
@Path("simple")
public class SimpleController {

    @Inject
    private Models models;

    @GET
    @Controller
    @Produces("text/html")
    @View("simple.trimou")
    public void hello(@QueryParam("user") String user) {
        models.put("user", user);
    }
}
