package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class OptionsTest extends AbstractTest {

    @Test
    public void testParametes() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Appendable appendable, Options options) {
                        List<Object> params = options.getParameters();
                        assertEquals(3, params.size());
                        assertEquals("1", params.get(0));
                        assertEquals(10, params.get(1));
                        assertNull(params.get(2));
                    }
                }).build();
        engine.compileMustache("helper_params",
                "{{test \"1\" this.age nonexisting}}").render(new Hammer());
    }

    @Test
    public void testHash() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Appendable appendable, Options options) {
                        Map<String, Object> hash = options.getHash();
                        assertEquals(3, hash.size());
                        assertEquals("1", hash.get("first"));
                        assertEquals(10, hash.get("second"));
                        assertNull(hash.get("third"));
                    }
                }).build();
        engine.compileMustache("helper_params",
                "{{test first=\"1\" second=this.age third=nonexisting}}").render(new Hammer());
    }

    @Test
    public void testPush() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Appendable appendable, Options options) {
                        options.push("OK");
                        options.fn(appendable);
                    }
                }).build();
        assertEquals(
                "OK",
                engine.compileMustache("helper_params",
                        "{{#test}}{{this}}{{/test}}").render(new Hammer()));
    }

    @Test
    public void testPop() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("test", new AbstractHelper() {
                    @Override
                    public void execute(Appendable appendable, Options options) {
                        options.pop();
                    }
                }).build();
        try {
            engine.compileMustache("helper_params", "{{test}}").render(
                    new Hammer());
            fail();
        } catch (MustacheException e) {
            if (!e.getCode().equals(
                    MustacheProblem.RENDER_HELPER_INVALID_POP_OPERATION)) {
                fail();
            }
        }
    }

}
