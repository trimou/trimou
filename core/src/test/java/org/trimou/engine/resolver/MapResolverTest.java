package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractEngineTest;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class MapResolverTest extends AbstractEngineTest {

    @Test
    public void testResolution() {
        MapResolver resolver = new MapResolver();
        assertNull(resolver.resolve(null, "foo", null));
        assertNotNull(resolver.resolve(ImmutableMap.of("bar", "baz"), "bar",
                null));
        assertNull(resolver.resolve(ImmutableMap.of("bar", "baz"), "qux", null));
    }

    @Test
    public void testInterpolation() {
        Map<String, Integer> map = new HashMap<String, Integer>(2);
        map.put("foo", 1);
        map.put("bar", 2);
        Mapper mapper = new Mapper() {
            @Override
            public Object get(String key) {
                return "foo".equals(key) ? Integer.valueOf(10) : null;
            }
        };
        Map<String, Object> data = ImmutableMap.<String, Object> of("map", map, "mapper", mapper);
        String templateContents = "Hello {{map.foo}} or {{map.bar}}!|{{map.nonExisting}} {{mapper.foo}}";
        assertEquals("Hello 1 or 2!| 10",
                engine.compileMustache("map", templateContents).render(data));
    }

}
