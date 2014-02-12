package org.trimou.example.ping;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.listener.SimpleStatsCollector;

/**
 *
 * @author Martin Kouba
 */
@Path("/stats")
public class StatsResource {

    @Inject
    private MustacheEngine engine;

    @Inject
    private SimpleStatsCollector statsCollector;

    /**
     *
     * @return
     */
    @GET
    @Produces(TEXT_HTML)
    public String getStats() {
        return engine.getMustache("stats").render(
                statsCollector.getSimpleStats());
    }

}
