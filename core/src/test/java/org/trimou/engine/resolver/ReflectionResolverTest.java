package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.ArchiveType;
import org.trimou.Hammer;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class ReflectionResolverTest extends AbstractEngineTest {

    @Test
    public void testResolution() {

        ReflectionResolver resolver = new ReflectionResolver();

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(resolver).build();

        Hammer hammer = new Hammer();
        assertNull(resolver.resolve(null, "whatever", null));
        assertNotNull(resolver.resolve(hammer, "age", null));
        // Methods have higher priority
        assertEquals(Integer.valueOf(10),
                resolver.resolve(hammer, "age", null));
        assertNull(resolver.resolve(hammer, "getAgeForName", null));
    }

    @Test
    public void testInterpolation() {
        int[] array = new int[] { 1, 2 };
        Map<String, Object> data = ImmutableMap.<String, Object> of("hammer",
                new Hammer(), "type", ArchiveType.class, "array", array);
        assertEquals("Hello Edgar of age 10, persistent: false and !",
                engine.compileMustache("reflection_resolver",
                        "Hello {{hammer.name}} of age {{hammer.age}}, persistent: {{hammer.persistent}} and {{hammer.invalidName}}!")
                .render(data));
        assertEquals(
                "NAIL|jar", engine
                        .compileMustache("reflection_resolver_fields",
                                "{{hammer.nail}}|{{type.JAR.suffix}}")
                        .render(data));
        assertEquals("jar,war,ear,",
                engine.compileMustache("reflection_resolver_static_method",
                        "{{#type.values}}{{this.suffix}},{{/type.values}}")
                .render(data));
        assertEquals("" + array.length,
                engine.compileMustache("reflection_resolver_array",
                        "{{array.length}}").render(data));
    }

    @Test
    public void testPublicMethodOnPackagePrivateClass() {
        Hammer data = new Hammer();
        Mustache mustache = engine.compileMustache(
                "reflection_resolver_accessibility",
                "{{#this.map.entrySet}}{{key}}={{value}}{{/this.map.entrySet}}");
        assertEquals("foo=10", mustache.render(data));
        assertEquals("foo=10", mustache.render(data));
    }

    @Test
    public void testMemberCacheInvalidation() {

        final ReflectionResolver resolver = new ReflectionResolver();

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(resolver).build();

        Hammer hammer = new Hammer();
        assertNotNull(resolver.resolve(hammer, "age", null));
        resolver.invalidateMemberCache(null);
        assertEquals(0, resolver.getMemberCacheSize());

        assertNotNull(resolver.resolve(hammer, "age", null));
        assertNotNull(resolver.resolve(ArchiveType.class, "JAR", null));
        resolver.invalidateMemberCache(new Predicate<Class<?>>() {
            @Override
            public boolean apply(Class<?> input) {
                return input.getName().equals(ArchiveType.class.getName());
            }
        });
        assertEquals(1, resolver.getMemberCacheSize());
    }

    @Test(expected = IllegalStateException.class)
    public void testMultipleInit() {

        ReflectionResolver resolver = new ReflectionResolver();

        // Just to init the resolver
        MustacheEngineBuilder.newBuilder()
                .omitServiceLoaderConfigurationExtensions()
                .addResolver(resolver).build();

        resolver.init(null);
    }

}
