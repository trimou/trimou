package org.trimou.servlet.resolver;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class HttpServletRequestResolverTest {

	@Test
	public void testNulltIsResolved() {
		HttpServletRequestResolver resolver = new HttpServletRequestResolver();
		assertNull(resolver.resolve("whatever", "request"));
		assertNull(resolver.resolve(null, "foo"));
	}

}
