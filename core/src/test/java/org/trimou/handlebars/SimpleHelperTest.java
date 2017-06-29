package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.SimpleConfigurationKey;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class SimpleHelperTest {

    @Test
    public void testSimpleHelper() {
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .registerHelper("lc", SimpleHelpers.execute((o, c) -> {
                    String value = o.getParameters().get(0).toString();
                    o.append(value.toLowerCase());
                }))
                .registerHelper("var", o -> {
                    o.append("{{");
                    o.append(o.getParameters().get(0).toString());
                    o.append("}}");
                })
                .registerHelper(
                        "lc_validate",
                        SimpleHelpers
                                .builder()
                                .execute(
                                        (o, c) -> o.append(o.getParameters().get(0)
                                                .toString().toLowerCase()))
                                .validate(
                                        (d, c) -> {
                                            if (d.getParameters().isEmpty())
                                                throw new MustacheException(
                                                        MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
                                        }).build()).build();
        assertEquals("ok",
                engine.compileMustache("simple_helper_01", "{{lc 'OK'}}")
                        .render(null));
        assertEquals("{{ok}}",
                engine.compileMustache("simple_helper_02", "{{var 'ok'}}")
                        .render(null));
        MustacheExceptionAssert.expect(
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE).check(
                () -> engine.compileMustache("simple_helper_02",
                        "{{lc_validate}}").render(null));
    }

    @Test
    public void testSimpleHelperConfiguration() {
        final SimpleConfigurationKey key = new SimpleConfigurationKey(
                "test.key", 10L);
        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .registerHelper("test",
                        SimpleHelpers.builder().execute((o, c) -> o.append(c.getLongPropertyValue(key).toString()))
                                .addConfigurationKey(key).build()).build();
        assertEquals("10",
                engine.compileMustache("simple_helper_config_01", "{{test}}")
                        .render(null));
    }
}