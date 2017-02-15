package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class ListIndexResolverTest extends AbstractEngineTest {

    @Test
    public void testInterpolation() {
        List<String> list = new ArrayList<>();
        list.add("foo");
        list.add("bar");
        Map<String, Object> data = ImmutableMap.of("list", list);
        String templateContents = "{{list.0}},{{list.1}},{{list.10}},{{list.a}}!";
        assertEquals("foo,bar,,!",
                engine.compileMustache("list", templateContents).render(data));
    }

}
