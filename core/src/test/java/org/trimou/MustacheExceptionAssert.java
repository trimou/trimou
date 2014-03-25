package org.trimou;

import static org.junit.Assert.fail;

import org.trimou.exception.MustacheException;
import org.trimou.exception.ProblemCode;

/**
 *
 * @author Martin Kouba
 */
public class MustacheExceptionAssert extends ExceptionAssert<MustacheException> {

    private final ProblemCode expectedCode;

    private MustacheExceptionAssert(ProblemCode expectedCode) {
        super(MustacheException.class);
        this.expectedCode = expectedCode;
    }

    @Override
    protected void checkException(MustacheException e) {
        if (!e.getCode().equals(expectedCode)) {
            fail("Unexpected problem code: " + e.getCode());
        }
    }

    public static MustacheExceptionAssert expect(ProblemCode code) {
        return new MustacheExceptionAssert(code);
    }

}