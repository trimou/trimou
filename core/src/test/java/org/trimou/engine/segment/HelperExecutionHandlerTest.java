package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.trimou.MustacheExceptionAssert;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class HelperExecutionHandlerTest {

    @Test
    public void testSplitHelperName() {

        assertHelperNameParts("name foo.bar", "name", "foo.bar");
        assertHelperNameParts("name  hash1=\"DD-MM-yyyy HH:mm\"", "name",
                "hash1=\"DD-MM-yyyy HH:mm\"");
        assertHelperNameParts("name key='value'", "name", "key='value'");
        assertHelperNameParts("name key=''value''", "name", "key=''value''");
        assertHelperNameParts("key='value foo=' 'bar'", "key='value foo='",
                "'bar'");
        assertHelperNameParts("'key'='value foo=' 'bar'", "'key'='value foo='",
                "'bar'");
        // String literal may contain anything
        assertHelperNameParts("foo='bar = \n baz' qux", "foo='bar = \n baz'",
                "qux");
        // List/array literal
        assertHelperNameParts("[ 'foo', 'bar', qux] foo", "[ 'foo', 'bar', qux]",
                "foo");
        assertHelperNameParts("1 20 ['', '1'] foo", "1", "20", "['', '1']",
                "foo");
        assertHelperNameParts("'[1, 2]'", "'[1, 2]'");
        assertHelperNameParts("'[1, '2', 'alpha']'", "'[1, '2', 'alpha']'");

        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> assertHelperNameParts("name key=''value'"))
                .check(() -> assertHelperNameParts("name key=value'"))
                .check(() -> assertHelperNameParts("name key='value foo"))
                .check(() -> assertHelperNameParts("name key=value' foo"))
                .check(() -> assertHelperNameParts("'name key=value"))
                .check(() -> assertHelperNameParts("key=\"value \" and\""));
    }

    @Test
    public void testGetFirstDeterminingEqualsCharPosition() {
        assertEquals(3, HelperExecutionHandler
                .getFirstDeterminingEqualsCharPosition("foo=bar"));
        assertEquals(3, HelperExecutionHandler
                .getFirstDeterminingEqualsCharPosition("foo='bar='"));
        assertEquals(1, HelperExecutionHandler
                .getFirstDeterminingEqualsCharPosition("1='bar='"));
        assertEquals(-1, HelperExecutionHandler
                .getFirstDeterminingEqualsCharPosition("'m=n'"));
        assertEquals(-1, HelperExecutionHandler
                .getFirstDeterminingEqualsCharPosition(" ' m=n'"));
        assertEquals(-1, HelperExecutionHandler
                .getFirstDeterminingEqualsCharPosition("'1'"));
        assertEquals(-1, HelperExecutionHandler
                .getFirstDeterminingEqualsCharPosition("\"foo\""));
    }

    private void assertHelperNameParts(String name, String... parts) {
        List<String> result = new ArrayList<>();
        Iterator<String> iterator = HelperExecutionHandler.splitHelperName(name,
                null);
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        List<String> expected = new ArrayList<>();
        for (String part : parts) {
            expected.add(part);
        }
        assertTrue("Parts: " + expected + " != " + result,
                expected.containsAll(result) && result.containsAll(expected));
    }

}
