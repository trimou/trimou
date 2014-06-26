package org.trimou.example.ping;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.trimou.engine.listener.SimpleStatsCollector;

/**
 * A producer for {@link SimpleStatsCollector}.
 *
 * @author Martin Kouba
 */
public class SimpleStatsCollectorProducer {

    @ApplicationScoped
    @Produces
    public SimpleStatsCollector produceStatsCollector() {
        return new SimpleStatsCollector();
    }

}
