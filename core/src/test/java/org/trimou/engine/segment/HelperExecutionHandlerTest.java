package org.trimou.engine.segment;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.trimou.MustacheExceptionAssert;
import org.trimou.engine.MustacheTagInfo;
import org.trimou.engine.context.ExecutionContext;
import org.trimou.exception.MustacheProblem;

import com.google.common.collect.Iterators;

/**
 *
 * @author Martin Kouba
 */
public class HelperExecutionHandlerTest {

    @Test
    public void testSplitHelperName() {
        assertHelperNameParts("name foo", "name", "foo");
        assertHelperNameParts("name  hash1=\"DD-MM-yyyy HH:mm\"", "name",
                "hash1=\"DD-MM-yyyy HH:mm\"");
        assertHelperNameParts("name key='value'", "name", "key='value'");

        assertHelperNameParts("name key=''value''", "name", "key=''value''");

        MustacheExceptionAssert
                .expect(MustacheProblem.COMPILE_HELPER_VALIDATION_FAILURE)
                .check(new Runnable() {
                    public void run() {
                        assertHelperNameParts("name key=''value'", "name",
                                "key=''value'");
                    }
                }).check(new Runnable() {
                    public void run() {
                        assertHelperNameParts("name key=value'", "name",
                                "key=value'");
                    }
                }).check(new Runnable() {
                    public void run() {
                        assertHelperNameParts("name key='value foo", "name",
                                "key='value foo");
                    }
                }).check(new Runnable() {
                    public void run() {
                        assertHelperNameParts("name key=value' foo", "name",
                                "key=value' foo");
                    }
                }).check(new Runnable() {
                    public void run() {
                        assertHelperNameParts("'name key=value",
                                "'name key=value");
                    }
                });
    }

    private void assertHelperNameParts(String name, String... parts) {
        assertTrue(
                "Parts: "
                        + Arrays.toString(parts)
                        + " != "
                        + Iterators.toString(HelperExecutionHandler
                                .splitHelperName(name, new DummySegment(name))),
                Iterators.elementsEqual(HelperExecutionHandler.splitHelperName(
                        name, new DummySegment(name)), Iterators
                        .forArray(parts)));
    }

    static class DummySegment implements HelperAwareSegment {

        private final String text;

        public DummySegment(String text) {
            this.text = text;
        }

        @Override
        public SegmentType getType() {
            return null;
        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public Origin getOrigin() {
            return null;
        }

        @Override
        public MustacheTagInfo getTagInfo() {
            return null;
        }

        @Override
        public String getLiteralBlock() {
            return null;
        }

        @Override
        public void execute(Appendable appendable, ExecutionContext context) {

        }

        @Override
        public void fn(Appendable appendable, ExecutionContext context) {

        }

        @Override
        public String toString() {
            return "SECTION:" + text + " [template: foo, line: 15, idx: 20]";
        }

    }

}
