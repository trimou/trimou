package org.trimou.tests.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class MustacheEngineProducer {

    @Produces
    @ApplicationScoped
    public MustacheEngine produceMustacheEngine() {
        return MustacheEngineBuilder.newBuilder().build();
    }

}
