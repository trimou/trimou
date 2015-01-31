package org.trimou.tests.locator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.trimou.tests.IntegrationTestUtils.createTestArchiveBase;
import static org.trimou.tests.IntegrationTestUtils.resolve;

import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.ClassPathTemplateLocator;
import org.trimou.engine.locator.TemplateLocator;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class ClassPathTemplateLocatorIntegrationTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return createTestArchiveBase()
                .addAsLibrary(
                        ShrinkWrap
                                .create(JavaArchive.class)
                                .addAsResource(
                                        new StringAsset("{{! lib 1}}<html/>"),
                                        "templates/foo.html")
                                .addAsResource(
                                        new StringAsset("{{! lib 1}}<xml/>"),
                                        "META-INF/templates/bar.html"))
                .addAsLibrary(
                        ShrinkWrap
                                .create(JavaArchive.class)
                                .addAsResource(
                                        new StringAsset("{{! lib 2}}<html/>"),
                                        "templates/foo.html")
                                .addAsResource(
                                        new StringAsset("{{! lib 2}}<xml/>"),
                                        "META-INF/templates/bar.html"))
                .addAsLibraries(resolve("org.trimou:trimou-core"));
    }

    @Test
    public void testLocator() {

        TemplateLocator locator1 = ClassPathTemplateLocator.builder(10)
                .setRootPath("templates").setSuffix("html").build();
        TemplateLocator locator2 = ClassPathTemplateLocator.builder(9)
                .setScanClasspath(false).build();
        TemplateLocator locator3 = ClassPathTemplateLocator.builder(8)
                .setSuffix("html").setRootPath("META-INF/templates")
                .setScanClasspath(false).build();

        // Just to init the locators
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(locator1).addTemplateLocator(locator2)
                .addTemplateLocator(locator3).build();

        Set<String> locator1Ids = locator1.getAllIdentifiers();
        assertEquals(0, locator1Ids.size());
        Mustache foo = engine.getMustache("foo");
        assertNotNull(foo);
        assertEquals("<html/>", foo.render(null));

        Set<String> locator2Ids = locator2.getAllIdentifiers();
        assertEquals(0, locator2Ids.size());
        foo = engine.getMustache("templates/foo.html");
        assertNotNull(foo);
        assertEquals("<html/>", foo.render(null));

        Set<String> locator3Ids = locator3.getAllIdentifiers();
        assertEquals(0, locator3Ids.size());
        Mustache bar = engine.getMustache("bar");
        assertNotNull(bar);
        assertEquals("<xml/>", bar.render(null));
    }

}
