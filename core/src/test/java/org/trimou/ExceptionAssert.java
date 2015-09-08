package org.trimou;

import static org.junit.Assert.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Martin Kouba
 */
public class ExceptionAssert<T extends Exception> {

    private static final Logger logger = LoggerFactory
            .getLogger(ExceptionAssert.class);

    private final Class<T> exceptionClazz;

    protected ExceptionAssert(Class<T> exceptionClazz) {
        this.exceptionClazz = exceptionClazz;
    }

    /**
     * Check an exception is thrown.
     *
     * @param action
     *            An action which should cause an exception
     * @return self
     */
    @SuppressWarnings("unchecked")
    public ExceptionAssert<T> check(Runnable action) {
        try {
            action.run();
            fail(String.format("%s not thrown",
                    exceptionClazz.getSimpleName()));
        } catch (Exception e) {
            if (exceptionClazz != null) {
                if (!e.getClass().equals(exceptionClazz)) {
                    fail("Unexpected exception thrown: " + e);
                }
                checkException((T) e);
                logger.info("{} check successful, message: {}", exceptionClazz,
                        e.getMessage());
            }
        }
        return this;
    }

    protected void checkException(T exception) {
        // No-op
    }

    public static <T extends Exception> ExceptionAssert<T> expect(
            Class<T> exceptionClazz) {
        return new ExceptionAssert<T>(exceptionClazz);
    }

}