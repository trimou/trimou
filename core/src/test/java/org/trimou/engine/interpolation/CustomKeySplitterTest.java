package org.trimou.engine.interpolation;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;

/**
 *
 * @author Martin Kouba
 */
public class CustomKeySplitterTest extends AbstractTest {

    @Test
    public void testCustomKeySplitter() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .setKeySplitter(new KeySplitter() {
                    @Override
                    public void init(Configuration configuration) {
                    }

                    @Override
                    public Set<ConfigurationKey> getConfigurationKeys() {
                        return Collections.emptySet();
                    }

                    @Override
                    public Iterator<String> split(String key) {
                        return Iterators.singletonIterator(key);
                    }
                }).build();

        assertEquals("WORKS",
                engine.compileMustache("custom_key_splitter", "{{foo.bar}}")
                        .render(ImmutableMap.of("foo.bar", "WORKS")));
    }

}
