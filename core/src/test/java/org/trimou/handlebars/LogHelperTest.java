package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;
import org.trimou.handlebars.LogHelper.Level;
import org.trimou.handlebars.LogHelper.Slf4jLoggerAdapter;

/**
 *
 * @author Martin Kouba
 */
public class LogHelperTest extends AbstractTest {

    @Test
    public void testLogHelper() {

        String name = "MyLogger";
        TestLoggerAdapter adapter = new TestLoggerAdapter(name);

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .registerHelpers(
                        HelpersBuilder
                                .empty()
                                .add("log",
                                        LogHelper.builder()
                                                .setDefaultLevel(Level.INFO)
                                                .setLoggerAdapter(adapter)
                                                .build()).build()).build();

        String msg = "Hello me!";
        engine.compileMustache("log_helper1",
                "{{log \"" + msg + "\" level=\"WARN\"}}").render(null);
        assertEquals(Level.WARN, adapter.getLevels().get(0));
        assertEquals(msg + " [log_helper1:1]", adapter.getMessages().get(0));
        assertEquals(adapter.getParameters().get(0).length, 0);

        msg = "Hello {}!";
        engine.compileMustache("log_helper2", "{{log this \"me\"}}")
                .render(msg);
        assertEquals(Level.INFO, adapter.getLevels().get(1));
        assertEquals(msg + " [log_helper2:1]", adapter.getMessages().get(1));
        assertEquals(adapter.getParameters().get(1).length, 1);

        engine = MustacheEngineBuilder
                .newBuilder()
                .registerHelpers(
                        HelpersBuilder
                                .empty()
                                .add("log",
                                        LogHelper.builder()
                                                .setAppendTemplateInfo(false)
                                                .setDefaultLevel(Level.INFO)
                                                .setLoggerAdapter(adapter)
                                                .build()).build()).build();
        engine.compileMustache("log_helper3", "{{log \"" + msg + "\"}}")
                .render(null);
        assertEquals(Level.INFO, adapter.getLevels().get(2));
        assertEquals(msg, adapter.getMessages().get(2));
        assertEquals(adapter.getParameters().get(2).length, 0);
    }

    @Test
    public void testLogHelperValidation() {

        final MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .registerHelpers(
                        HelpersBuilder.empty()
                                .add("log", LogHelper.builder().build())
                                .build()).build();

        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(new Runnable() {
                    public void run() {
                        engine.compileMustache("log_helper_validation01",
                                "{{#log \"Foo\"}}{{/log}}");
                    }
                }).check(new Runnable() {
                    public void run() {
                        engine.compileMustache("log_helper_validation02",
                                "{{log}}");
                    }
                });
    }

    private static class TestLoggerAdapter extends Slf4jLoggerAdapter {

        final List<Level> levels = new ArrayList<Level>();
        final List<String> messages = new ArrayList<String>();
        final List<Object[]> parameters = new ArrayList<Object[]>();

        public TestLoggerAdapter(String name) {
            super(name);
        }

        @Override
        public void log(Level level, String message, Object[] params) {
            super.log(level, message, params);
            levels.add(level);
            messages.add(message);
            parameters.add(params);
        }

        public List<Level> getLevels() {
            return levels;
        }

        public List<String> getMessages() {
            return messages;
        }

        public List<Object[]> getParameters() {
            return parameters;
        }

    }

}
