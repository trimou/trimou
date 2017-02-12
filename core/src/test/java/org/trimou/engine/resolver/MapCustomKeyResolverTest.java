package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.priority.Priorities;
import org.trimou.util.ImmutableMap;
import org.trimou.util.Strings;

/**
 *
 * @author Martin Kouba
 */
public class MapCustomKeyResolverTest extends AbstractTest {

    @Test
    public void testCustomKey() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                // Resolve integer keys
                .addResolver(
                        new MapCustomKeyResolver(
                                Priorities
                                        .rightAfter(ThisResolver.THIS_RESOLVER_PRIORITY)) {

                            @Override
                            protected boolean matches(String name) {
                                return Strings.containsOnlyDigits(name);
                            }

                            @Override
                            protected Object convert(String name) {
                                return Integer.valueOf(name);
                            }
                        }).build();
        Map<Integer, String> dataMap = new HashMap<>();
        dataMap.put(1, "Hello");
        dataMap.put(2, "there");
        assertEquals(
                "Hello there!",
                engine.compileMustache("map_custom_key_resolver",
                        "{{#data}}{{1}}{{3}} {{2}}{{/data}}!").render(
                        ImmutableMap.of("data", dataMap)));
    }

    @Test
    public void testEnumKey() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addResolver(new EnumKeyResolver()).build();
        Map<EnumKey, String> dataMap = new HashMap<>();
        dataMap.put(EnumKey.ONE, "Hello");
        dataMap.put(EnumKey.TWO, "there");
        assertEquals(
                "Hello there!",
                engine.compileMustache("map_enum_key_resolver",
                        "{{#data}}{{ONE}} {{TWO}}{{/data}}!").render(
                        ImmutableMap.of("data", dataMap)));
    }

    private static enum EnumKey {
        ONE,
        TWO, ;
    }

    private static class EnumKeyResolver extends MapCustomKeyResolver {

        public EnumKeyResolver() {
            super(Priorities.rightAfter(ThisResolver.THIS_RESOLVER_PRIORITY));
        }

        private static List<String> keys;

        static {
            keys = new ArrayList<>(EnumKey.values().length);
            for (EnumKey key : EnumKey.values()) {
                keys.add(key.toString());
            }
        }

        @Override
        protected boolean matches(String name) {
            return keys.contains(name);
        }

        @Override
        protected Object convert(String name) {
            return EnumKey.valueOf(name);
        }

    }

}
