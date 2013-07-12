package org.trimou.engine.text;

import org.trimou.annotations.Internal;

/**
 *
 * @author Martin Kouba
 */
@Internal
public class TextSupportFactory {

    /**
     *
     * @return the default text support
     */
    public TextSupport createTextSupport() {
        return new DefaultTextSupport();
    }

}
