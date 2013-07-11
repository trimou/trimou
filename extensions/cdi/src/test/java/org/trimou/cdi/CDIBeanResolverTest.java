package org.trimou.cdi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class CDIBeanResolverTest extends WeldSETest {

	@Test
	public void testInterpolation() {
		MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();
		assertEquals(
				"foo",
				engine.compileMustache("cdi_bean_resolver_weld_se",
						"{{appScopedBean.name}}").render(null));
	}

}
