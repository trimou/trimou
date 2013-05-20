package org.trimou.example.ping;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.trimou.engine.MustacheEngine;

/**
 *
 * @author Martin Kouba
 */
@Path("/ping")
public class PingResource {

	@Inject
	private PingService service;

	@Inject
	private MustacheEngine engine;

	@Context
	private HttpServletRequest request;

	/**
	 *
	 * @return
	 */
	@GET
	@Produces(TEXT_HTML)
	public String getPingLog() {
		return engine.getMustache("pingLogCdi").render(null);
	}

	@POST
	public void ping() {
		service.ping(request.getRemoteAddr());
	}

}
