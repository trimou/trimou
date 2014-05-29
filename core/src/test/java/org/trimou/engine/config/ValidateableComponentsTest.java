package org.trimou.engine.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.listener.AbstractMustacheListener;
import org.trimou.engine.resolver.AbstractResolver;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resolver.Resolver;
import org.trimou.engine.validation.Validateable;

/**
 *
 * @author Martin Kouba
 */
public class ValidateableComponentsTest extends AbstractTest {

    @Test
    public void testValidateableResolver() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addResolver(new TestResolver(10, false))
                .addResolver(new TestResolver(11, true)).build();
        for (Resolver resolver : engine.getConfiguration().getResolvers()) {
            if (resolver instanceof TestResolver) {
                if (resolver.getPriority() != 11) {
                    fail();
                }
            }
        }
    }

    @Test
    public void testValidateableListener() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addMustacheListener(new TestListener(false))
                .addMustacheListener(new TestListener(true)).build();
        assertEquals(1, engine.getConfiguration().getMustacheListeners().size());
        assertTrue(engine.getConfiguration().getMustacheListeners().get(0) instanceof TestListener);
    }

    private class TestResolver extends AbstractResolver implements Validateable {

        private final boolean isValid;

        public TestResolver(int priority, boolean isValid) {
            super(priority);
            this.isValid = isValid;
        }

        @Override
        public Object resolve(Object contextObject, String name,
                ResolutionContext context) {
            return null;
        }

        @Override
        public boolean isValid() {
            return isValid;
        }

    }

    private class TestListener extends AbstractMustacheListener implements
            Validateable {

        private final boolean isValid;

        public TestListener(boolean isValid) {
            this.isValid = isValid;
        }

        @Override
        public boolean isValid() {
            return isValid;
        }

    }
}
