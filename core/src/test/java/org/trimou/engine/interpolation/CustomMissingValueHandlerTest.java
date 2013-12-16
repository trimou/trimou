package org.trimou.engine.interpolation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;

/**
 *
 * @author Martin Kouba
 */
public class CustomMissingValueHandlerTest extends AbstractEngineTest {

    @Before
    public void buildEngine() {
    }

    @Test
    public void testCustomMissingValueHandler() {

        final AtomicBoolean handlerInvoked = new AtomicBoolean(false);

        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .setMissingValueHandler(new MissingValueHandler() {

                    @Override
                    public void init(Configuration configuration) {
                    }

                    @Override
                    public Set<ConfigurationKey> getConfigurationKeys() {
                        return Collections.emptySet();
                    }

                    @Override
                    public Object handle(MustacheTagInfo info) {
                        handlerInvoked.set(true);
                        return "FOO";
                    }
                }).build();

        assertEquals("FOO", engine.compileMustache("custom_mvh", "{{foo.bar}}")
                .render(null));
        assertTrue(handlerInvoked.get());

    }

}
