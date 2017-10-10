package org.trimou.engine;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class BuildInfoTest {

    @Test
    public void testVersion() {
        BuildInfo info = BuildInfo.load();
        assertNotNull(info);
        assertNotNull(info.getVersion());
        assertNotNull(info.getTimestamp());
    }

}
