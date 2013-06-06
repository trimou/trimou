package org.trimou.engine.resolver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.resolver.IndexResolver;

/**
 *
 * @author Martin Kouba
 */
public class IndexResolverTest extends AbstractEngineTest {

	@Test
	public void testNotAnIndex() {

		IndexResolver indexResolver = new IndexResolver() {

			@Override
			public int getPriority() {
				return 0;
			}

			@Override
			public Object resolve(Object baseObject, String key) {
				return null;
			}
		};
		assertTrue(indexResolver.notAnIndex("-1"));
		assertTrue(indexResolver.notAnIndex("size"));
		assertTrue(indexResolver.notAnIndex(".1"));
		assertTrue(indexResolver.notAnIndex("1,5"));
		assertFalse(indexResolver.notAnIndex("1500"));
	}

}
