package org.trimou.engine;

/**
 *
 * @author Martin Kouba
 */
public enum MustacheTagType {

    VARIABLE(null), UNESCAPE_VARIABLE('&'), SECTION('#'), INVERTED_SECTION('^'), SECTION_END(
            '/'), COMMENT('!'), PARTIAL('>'), DELIMITER('='), EXTEND('<'), EXTEND_SECTION(
            '$');

    MustacheTagType(Character command) {
        this.command = command;
    }

    private Character command;

    public Character getCommand() {
        return command;
    }

}
