package org.trimou.lambda.i18n;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.Configuration;
import org.trimou.engine.config.ConfigurationKey;
import org.trimou.engine.locale.LocaleSupport;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class ResourceBundleLambdaTest extends AbstractEngineTest {

    @Override
    @Before
    public void buildEngine() {

        ResourceBundleLambda resourceBundleLambda = new ResourceBundleLambda(
                "messages");

        engine = MustacheEngineBuilder.newBuilder()
                .setLocaleSupport(new LocaleSupport() {
                    @Override
                    public Locale getCurrentLocale() {
                        return new Locale("en");
                    }

                    @Override
                    public void init(Configuration configuration) {
                    }

                    @Override
                    public Set<ConfigurationKey> getConfigurationKeys() {
                        return Collections.emptySet();
                    }
                }).addGlobalData("bundle", resourceBundleLambda)
                .registerCallback(resourceBundleLambda).build();
    }

    @Test
    public void testInterpolation() {

        String templateContents = "{{#bundle}}{{key}}{{/bundle}}";
        Mustache mustache = engine.compileMustache("bundle_lambda",
                templateContents);

        assertEquals("Hello", mustache.render(ImmutableMap.<String, Object> of(
                "key", "echo_one")));
    }

}
