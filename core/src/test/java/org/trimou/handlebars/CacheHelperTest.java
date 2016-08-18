package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;
import org.trimou.Mustache;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class CacheHelperTest extends AbstractTest {

    @Test
    public void testHelper() throws InterruptedException {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelpers(HelpersBuilder.empty().addCache().build())
                .build();

        CacheHelper cacheHelper = (CacheHelper) engine.getConfiguration()
                .getHelpers().get(HelpersBuilder.CACHE);
        final AtomicInteger hits = new AtomicInteger();
        Mustache mustache = engine.compileMustache("cache_01",
                "{{#cache}}{{this.age}}{{/cache}}");
        Hammer hammer = new Hammer() {
            @Override
            public Integer getAge() {
                hits.incrementAndGet();
                return super.getAge();
            }

        };
        assertEquals("10", mustache.render(hammer));
        assertEquals("10", mustache.render(hammer));
        hammer.setAge(11);
        assertEquals("10", mustache.render(hammer));
        assertEquals(1, hits.get());

        // Test expiration
        cacheHelper.invalidateFragments();
        hammer.setAge(1);
        mustache = engine.compileMustache("cache_02",
                "{{#cache expire=1 unit='MILLISECONDS'}}{{this.age}}{{/cache}}");
        assertEquals("1", mustache.render(hammer));
        Thread.sleep(50);
        hammer.setAge(2);
        assertEquals("2", mustache.render(hammer));

        // Test guard
        cacheHelper.invalidateFragments();
        hammer.setAge(5);
        mustache = engine.compileMustache("cache_03",
                "{{#cache guard=this.age}}{{this.age}}{{/cache}}");
        assertEquals("5", mustache.render(hammer));
        assertEquals("5", mustache.render(hammer));
        hammer.setAge(50);
        assertEquals("50", mustache.render(hammer));

        // Test key
        cacheHelper.invalidateFragments();
        mustache = engine.compileMustache("cache_04",
                "{{#cache key=this.id}}{{this.age}}{{/cache}}");
        hammer.setAge(1000);
        Hammer hammer2 = new Hammer(2000) {
            @Override
            public Integer getAge() {
                hits.incrementAndGet();
                return super.getAge();
            }
        };
        Hammer hammer3 = new Hammer(3000) {
            @Override
            public Integer getAge() {
                hits.incrementAndGet();
                return super.getAge();
            }
        };
        assertEquals("1000", mustache.render(hammer));
        assertEquals("2000", mustache.render(hammer2));
        assertEquals("3000", mustache.render(hammer3));
        hammer.setAge(1001);
        hammer2.setAge(2001);
        hammer3.setAge(3001);
        assertEquals("1000", mustache.render(hammer));
        assertEquals("2000", mustache.render(hammer2));
        assertEquals("3000", mustache.render(hammer3));
        // Test invalidate
        cacheHelper.invalidateFragments(hammer.getId());
        assertEquals("1001", mustache.render(hammer));
        assertEquals("2000", mustache.render(hammer2));
        assertEquals("3000", mustache.render(hammer3));
    }

    @Test
    public void testHelperConfiguration() throws InterruptedException {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .setProperty(CacheHelper.FRAGMENT_CACHE_MAX_SIZE_KEY, 0)
                .registerHelpers(HelpersBuilder.empty().addCache().build())
                .build();
        Hammer hammer1 = new Hammer(1);
        Hammer hammer2 = new Hammer(2);
        Hammer hammer3 = new Hammer(3);
        Mustache mustache = engine.compileMustache("cache_config_01",
                "{{#cache key=this.id}}{{this.age}}{{/cache}}");
        assertEquals("1", mustache.render(hammer1));
        assertEquals("2", mustache.render(hammer2));
        assertEquals("3", mustache.render(hammer3));
        hammer1.setAge(11);
        hammer2.setAge(22);
        hammer3.setAge(33);
        assertEquals("11", mustache.render(hammer1));
        assertEquals("22", mustache.render(hammer2));
        assertEquals("33", mustache.render(hammer3));
    }

    @Test
    public void testValidation() {
        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> MustacheEngineBuilder.newBuilder()
                        .registerHelpers(
                                HelpersBuilder.empty().addCache().build())
                        .build()
                        .compileMustache("cache_validation_01", "{{cache}}"))
                .check(() -> MustacheEngineBuilder.newBuilder()
                        .registerHelpers(
                                HelpersBuilder.empty().addCache().build())
                        .build().compileMustache("cache_validation_02",
                                "{{#cache expire='foo'}}{{/cache}}"))
                .check(() -> MustacheEngineBuilder.newBuilder()
                        .registerHelpers(
                                HelpersBuilder.empty().addCache().build())
                        .build().compileMustache("cache_validation_03",
                                "{{#cache expire=1 unit='foo'}}{{/cache}}"));
    }

}
