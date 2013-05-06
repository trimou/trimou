package org.trimou.tests.cdi.interceptor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.trimou.api.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.MapTemplateLocator;

import com.google.common.collect.ImmutableMap;

public class MustacheFactoryProducer {

	@Produces
	@ApplicationScoped
	public MustacheEngine produceMustacheFactory() {
		return MustacheEngineBuilder
				.newBuilder()
				.addTemplateLocator(
						new MapTemplateLocator(ImmutableMap.of("foo.txt",
								"{{foo}}"))).build();
	}

}
