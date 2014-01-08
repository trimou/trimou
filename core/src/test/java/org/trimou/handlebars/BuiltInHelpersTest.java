package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;
import static org.trimou.AssertUtil.assertCompilationFails;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Hammer;
import org.trimou.exception.MustacheProblem;

import com.google.common.collect.ImmutableSet;

/**
 *
 * @author Martin Kouba
 */
public class BuiltInHelpersTest extends AbstractEngineTest {

    @Test
    public void testEachHelper() {
        assertEquals(
                "foo,bar",
                engine.compileMustache("each_helper1",
                        "{{#each this}}{{this}}{{#iterHasNext}},{{/iterHasNext}}{{/each}}")
                        .render(new Object[] { "foo", "bar" }));
        assertEquals(
                "foo,bar",
                engine.compileMustache("each_helper2",
                        "{{#each this}}{{this}}{{#iterHasNext}},{{/iterHasNext}}{{/each}}")
                        .render(ImmutableSet.of("foo", "bar")));
        assertCompilationFails(engine, "each_helper_fail1",
                "{{#each}}{{this}}{{/each}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
        assertCompilationFails(engine, "each_helper_fail2", "{{each}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
    }

    @Test
    public void testIfHelper() {
        assertEquals(
                "hello",
                engine.compileMustache("if_helper1", "{{#if this}}hello{{/if}}")
                        .render(Boolean.TRUE));
        assertEquals(
                "",
                engine.compileMustache("if_helper2", "{{#if this}}hello{{/if}}")
                        .render(false));
        assertCompilationFails(engine, "if_helper_fail1",
                "{{#if}}{{this}}{{/if}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
        assertCompilationFails(engine, "if_helper_fail2", "{{if}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
    }

    @Test
    public void testUnlessHelper() {
        assertEquals(
                "hello",
                engine.compileMustache("unless_helper1",
                        "{{#unless this}}hello{{/unless}}").render(
                        Boolean.FALSE));
        assertEquals(
                "",
                engine.compileMustache("unless_helper2",
                        "{{#unless this}}hello{{/unless}}").render(true));
        assertCompilationFails(engine, "unless_helper_fail1",
                "{{#unless}}{{this}}{{/unless}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
        assertCompilationFails(engine, "unless_helper_fail2", "{{unless}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
    }

    @Test
    public void testWithHelper() {
        assertEquals(
                "10",
                engine.compileMustache("with_helper1",
                        "{{#with this}}{{age}}{{/with}}").render(new Hammer()));
        assertCompilationFails(engine, "with_helper_fail1",
                "{{#with}}{{this}}{{/with}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
        assertCompilationFails(engine, "with_helper_fail2", "{{with}}",
                MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE);
    }

}
