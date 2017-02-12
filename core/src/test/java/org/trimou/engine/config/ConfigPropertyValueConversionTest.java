package org.trimou.engine.config;

import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.resolver.AbstractResolver;
import org.trimou.engine.resolver.ReflectionResolver;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resolver.Resolver;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class ConfigPropertyValueConversionTest extends AbstractEngineTest {

    @Before
    public void buildEngine() {
    }

    @Test
    public void testDefaultConversion() {
        testValue(EngineConfigurationKey.TEMPLATE_CACHE_EXPIRATION_TIMEOUT,
                "foo", false);
        testValue(EngineConfigurationKey.TEMPLATE_CACHE_EXPIRATION_TIMEOUT,
                10L, true);
        testValue(EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT,
                false, false);
        testValue(EngineConfigurationKey.TEMPLATE_RECURSIVE_INVOCATION_LIMIT,
                10, true);
        testValue(ReflectionResolver.MEMBER_CACHE_MAX_SIZE_KEY, "nonsense",
                false);
        testValue(ReflectionResolver.MEMBER_CACHE_MAX_SIZE_KEY, 10L, true);

        // Invalid default value type
        final ConfigurationKey key = new ConfigurationKey() {

            @Override
            public Object getDefaultValue() {
                return new Date();
            }

            @Override
            public String get() {
                return "invalid_default_value_type";
            }
        };
        testValue(key, new Date(), false, new AbstractResolver(10) {

            @Override
            public Object resolve(Object contextObject, String name,
                    ResolutionContext context) {
                return null;
            }

            @Override
            public Set<ConfigurationKey> getConfigurationKeys() {
                return Collections.singleton(key);
            }
        });
    }

    private void testValue(ConfigurationKey key, Object value,
            boolean shouldBeValid) {
        testValue(key, value, shouldBeValid, null);
    }

    private void testValue(ConfigurationKey key, Object value,
            boolean shouldBeValid, Resolver dummyResolver) {
        try {
            MustacheEngineBuilder builder = MustacheEngineBuilder.newBuilder();
            if(dummyResolver != null) {
                builder.addResolver(dummyResolver);
            }
            builder.setProperty(key, value).build();
            if (!shouldBeValid) {
                fail("Should be invalid");
            }
        } catch (MustacheException e) {
            if (shouldBeValid) {
                fail("Should be valid");
            }
            if (!e.getCode().equals(
                    MustacheProblem.CONFIG_PROPERTY_INVALID_VALUE)) {
                fail();
            }
        }
    }
}
