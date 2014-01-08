package org.trimou;

import static org.junit.Assert.fail;

import org.trimou.engine.MustacheEngine;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class AssertUtil {

    public static void assertCompilationFails(MustacheEngine engine,
            String templateName, String templateContents,
            MustacheProblem expectedProblem) {
        try {
            engine.compileMustache(templateName, templateContents);
            fail();
        } catch (MustacheException e) {
            if (!e.getCode().equals(expectedProblem)) {
                fail();
            }
        }
    }

}
