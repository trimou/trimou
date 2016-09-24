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
    private final String messageSubstring;

    private MustacheExceptionAssert(ProblemCode expectedCode) {
        super(MustacheException.class);
        this.expectedCode = expectedCode;
        this.messageSubstring = null;
    }

    private MustacheExceptionAssert(ProblemCode expectedCode, String messageSubstring) {
        super(MustacheException.class);
        this.expectedCode = expectedCode;
        this.messageSubstring = messageSubstring;
    }

    @Override
    protected void checkException(MustacheException e) {
        if (!e.getCode().equals(expectedCode)) {
            fail("Unexpected problem code: " + e.getCode());
        }
        if (messageSubstring != null && !e.getMessage().contains(messageSubstring)) {
            fail(String.format("expected message with substring [%s], got [%s]", messageSubstring, e.getMessage()));
        }
    }

    public static MustacheExceptionAssert expect(ProblemCode code) {
        return new MustacheExceptionAssert(code);
    }

    public static MustacheExceptionAssert expect(ProblemCode code, String messageSubstring) {
        return new MustacheExceptionAssert(code, messageSubstring);
    }

}