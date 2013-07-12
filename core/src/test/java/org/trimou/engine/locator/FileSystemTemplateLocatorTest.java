package org.trimou.engine.locator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class FileSystemTemplateLocatorTest extends PathTemplateLocatorTest {

    @Test
    public void testLocator() throws IOException {

        TemplateLocator locator = new FileSystemTemplateLocator(1,
                "src/test/resources/locator/file", "foo");

        // Just to init the locator
        MustacheEngineBuilder.newBuilder().addTemplateLocator(locator).build();

        Set<String> ids = locator.getAllIdentifiers();
        assertEquals(4, ids.size());
        assertTrue(ids.contains("index"));
        assertTrue(ids.contains("home"));
        assertTrue(ids.contains("sub/bar"));
        assertTrue(ids.contains("sub/subsub/qux"));

        assertEquals("{{foo}}", read(locator.locate("index")));
        assertEquals("bar", read(locator.locate("home")));
        assertEquals("{{foo}}", read(locator.locate("sub/bar")));
        assertEquals("{{bar}}", read(locator.locate("sub/subsub/qux")));
    }

    @Test
    public void testLocatorNoSuffix() throws IOException {

        TemplateLocator locator = new FileSystemTemplateLocator(1,
                "src/test/resources/locator/file");

        // Just to init the locator
        MustacheEngineBuilder.newBuilder().addTemplateLocator(locator).build();

        Set<String> ids = locator.getAllIdentifiers();
        assertEquals(5, ids.size());
        assertTrue(ids.contains("index.foo"));
        assertTrue(ids.contains("home.foo"));
        assertTrue(ids.contains("detail.html"));
        assertTrue(ids.contains("sub/bar.foo"));
        assertTrue(ids.contains("sub/subsub/qux.foo"));

        assertEquals("{{foo}}", read(locator.locate("index.foo")));
        assertEquals("bar", read(locator.locate("home.foo")));
        assertEquals("<html/>", read(locator.locate("detail.html")));
        assertEquals("{{foo}}", read(locator.locate("sub/bar.foo")));
        assertEquals("{{bar}}", read(locator.locate("sub/subsub/qux.foo")));
    }

    @Test
    public void testInvalidRootDir() {
        try {
            new FileSystemTemplateLocator(1, "_?dsss");
            fail();
        } catch (MustacheException e) {
            if (!e.getCode().equals(
                    MustacheProblem.TEMPLATE_LOCATOR_INVALID_CONFIGURATION)) {
                fail("Invalid problem code: " + e);
            }
        }
    }

    @Test
    public void testCustomVirtualPathSeparator() throws IOException {

        TemplateLocator locator = new FileSystemTemplateLocator(1,
                "src/test/resources/locator/file", "foo");

        // Just to init the locator
        MustacheEngineBuilder
                .newBuilder()
                .addTemplateLocator(locator)
                .setProperty(PathTemplateLocator.VIRTUAL_PATH_SEPARATOR_KEY,
                        "*").build();

        Set<String> names = locator.getAllIdentifiers();
        assertEquals(4, names.size());
        assertTrue(names.contains("index"));
        assertTrue(names.contains("home"));
        assertTrue(names.contains("sub*bar"));
        assertTrue(names.contains("sub*subsub*qux"));

        assertEquals("{{foo}}", read(locator.locate("index")));
        assertEquals("bar", read(locator.locate("home")));
        assertEquals("{{foo}}", read(locator.locate("sub*bar")));
        assertEquals("{{bar}}", read(locator.locate("sub*subsub*qux")));
    }

}
