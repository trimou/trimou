package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.ArchiveType;
import org.trimou.Hammer;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.util.ImmutableMap;

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
        assertEquals(10, resolver.resolve(hammer, "age", null));
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
        resolver.invalidateMemberCache((input) -> input.getName().equals(ArchiveType.class.getName()));
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

    @Test
    public void testGetMembers() {
        assertNotNull(ReflectionResolver.findMethod(Charlie.class, "name"));
        assertNotNull(ReflectionResolver.findMethod(Charlie.class, "old"));
        assertNotNull(
                ReflectionResolver.findMethod(Charlie.class, "hasSomething"));
        assertNotNull(
                ReflectionResolver.findMethod(Charlie.class, "getAnotherName"));
        assertNotNull(
                ReflectionResolver.findMethod(Charlie.class, "anotherName"));
        assertNotNull(ReflectionResolver.findMethod(Charlie.class, "isOk"));
        assertNotNull(ReflectionResolver.findMethod(Charlie.class, "ok"));
        assertNotNull(ReflectionResolver.findMethod(Charlie.class, "info"));
        assertNull(ReflectionResolver.findMethod(Charlie.class, "getPrice"));
        assertNotNull(
                ReflectionResolver.findField(Charlie.class, "publicField"));
        assertNull(ReflectionResolver.findField(Charlie.class, "privateField"));
    }

    public static class Alpha {

        @SuppressWarnings("unused")
        private String privateField;

        // OK
        public String getName() {
            return null;
        }

        // OK
        public int isOld() {
            return 1;
        }

        // OK
        public boolean hasSomething() {
            return true;
        }

        // Not read method - private
        @SuppressWarnings("unused")
        private BigDecimal getPrice() {
            return null;
        }

        // OK
        public String getInfo() {
            return null;
        }

        // Not read method - protected
        protected String getProtected() {
            return null;
        }

    }

    public static class Bravo extends Alpha {

        public final String publicField = "foo";

        // Not read method - has param
        public String getWithParam(String param) {
            return null;
        }

        // Not read method - no return value
        public void getNoReturnValue() {
        }

        // OK
        public String getAnotherName() {
            return null;
        }

    }

    public static class Charlie extends Bravo {

        // OK
        public Boolean isOk() {
            return null;
        }

    }

}
