package org.trimou.engine.segment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Mustache;

/**
 *
 * @author Martin Kouba
 */
public class LineSeparatorSegmentTest extends AbstractEngineTest {

    @Test
    public void testLineSeparators() {
        String templateContents = "\nHello\r\n\n!";
        Mustache mustache = engine
                .compileMustache("line_sep", templateContents);
        assertEquals("\nHello\r\n\n!", mustache.render(null));
    }

}
