package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
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
        assertHelperNameParts("foo=[1, '2']", "foo=[1, '2']");

        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> assertHelperNameParts("name key=''value'"))
                .check(() -> assertHelperNameParts("name key=value'"))
                .check(() -> assertHelperNameParts("name key='value foo"))
                .check(() -> assertHelperNameParts("name key=value' foo"))
                .check(() -> assertHelperNameParts("'name key=value"))
                .check(() -> assertHelperNameParts("key=\"value \" and\""));

        // this behavior was somehow working if the literal inside literal did not contains spaces,
        // and is still working with both literal parsing mode :
        // old
        assertHelperNameParts("if \"var eq 'value_without_space'\"", false,
                "if", "\"var eq 'value_without_space'\"");
        // new
        assertHelperNameParts("if \"var eq 'value_without_space'\"", true,
                "if", "\"var eq 'value_without_space'\"");

        // assert on diverging behavior between correctLiteralParsing = false | true
        // 1. you cannot close anymore ' with a " (they are not considered the same anymore).
        // example : foo='bar"
        // old : working
        assertHelperNameParts("foo='bar\"", false, "foo='bar\"");
        // new : not valid
        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> assertHelperNameParts("foo='bar\"", true));

        // 2. you may use the other literal closing/opening inside a literal, without closing it
        // example : key='value" foo=' 'bar'
        // old : invalid
        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(() -> assertHelperNameParts("key='value\" foo=' 'bar'", false));
        // new : working
        assertHelperNameParts("key='value\" foo=' 'bar'", true, "key='value\" foo='",
                "'bar'");

        // 3. you may use space inside a literal of literal
        // example : if "var eq 'value with space'"
        // old : unexpected parsing (4 parts, cutting on spaces inside the literal)
        assertHelperNameParts("if \"var eq 'value with space'\"", false,
                "if", "\"var eq 'value", "with", "space'\"");
        // new : expected, 2 parts (if + condition)
        assertHelperNameParts("if \"var eq 'value with space'\"", true,
                "if", "\"var eq 'value with space'\"");

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
        assertHelperNameParts(name, false, parts);
    }

    private void assertHelperNameParts(String name, boolean correctLiteralParsing, String... parts) {
        List<String> result = new ArrayList<>();
        Iterator<String> iterator = HelperExecutionHandler.splitHelperName(name,
                null, correctLiteralParsing);
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        List<String> expected = new ArrayList<>();
        Collections.addAll(expected, parts);
        assertTrue("Parts: " + expected + " != " + result,
                expected.containsAll(result) && result.containsAll(expected));
    }
}
