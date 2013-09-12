package org.trimou.engine.locator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.trimou.AbstractTest;

/**
 *
 * @author Martin Kouba
 */
public abstract class PathTemplateLocatorTest extends AbstractTest {

    protected String read(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder text = new StringBuilder();
        int character = bufferedReader.read();
        while (character != -1) {
            text.append((char) character);
            character = bufferedReader.read();
        }
        return text.toString();
    }

}
