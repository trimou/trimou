package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.trimou.MustacheExceptionAssert;
import org.trimou.exception.MustacheProblem;

import com.google.common.collect.Iterators;

/**
 *
 * @author Martin Kouba
 */
public class HelperValidatorTest {

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

        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(new Runnable() {
                    public void run() {
                        assertHelperNameParts("name key=''value'");
                    }
                }).check(new Runnable() {
                    public void run() {
                        assertHelperNameParts("name key=value'");
                    }
                }).check(new Runnable() {
                    public void run() {
                        assertHelperNameParts("name key='value foo");
                    }
                }).check(new Runnable() {
                    public void run() {
                        assertHelperNameParts("name key=value' foo");
                    }
                }).check(new Runnable() {
                    public void run() {
                        assertHelperNameParts("'name key=value");
                    }
                }).check(new Runnable() {
                    public void run() {
                        assertHelperNameParts("key=\"value \" and\"");
                    }
                });
    }

    @Test
    public void testGetFirstDeterminingEqualsCharPosition() {
        assertEquals(3,
                HelperValidator
                        .getFirstDeterminingEqualsCharPosition("foo=bar"));
        assertEquals(3,
                HelperValidator
                        .getFirstDeterminingEqualsCharPosition("foo='bar='"));
        assertEquals(1,
                HelperValidator
                        .getFirstDeterminingEqualsCharPosition("1='bar='"));
        assertEquals(-1,
                HelperValidator.getFirstDeterminingEqualsCharPosition("'m=n'"));
        assertEquals(-1,
                HelperValidator
                        .getFirstDeterminingEqualsCharPosition(" ' m=n'"));
        assertEquals(-1,
                HelperValidator.getFirstDeterminingEqualsCharPosition("'1'"));
        assertEquals(-1,
                HelperValidator
                        .getFirstDeterminingEqualsCharPosition("\"foo\""));
    }

    private void assertHelperNameParts(String name, String... parts) {
        assertTrue(
                "Parts: "
                        + Arrays.toString(parts)
                        + " != "
                        + Iterators.toString(HelperValidator.splitHelperName(
                                name, null)),
                Iterators.elementsEqual(
                        HelperValidator.splitHelperName(name, null),
                        Iterators.forArray(parts)));
    }

}
