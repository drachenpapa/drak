package de.drachenpapa.drak.settings;

/**
 * Validates and parses control key input entered in the settings UI.
 */
public class ControlKeyInputValidator {

    public boolean isValidKeyInput(String input) {
        if (input == null || input.length() != 1) {
            return false;
        }
        char c = input.charAt(0);
        return !Character.isWhitespace(c) && !Character.isISOControl(c);
    }

    public char toControlKey(String input) {
        if (!isValidKeyInput(input)) {
            throw new IllegalArgumentException("Control key must be exactly one non-whitespace character.");
        }
        return input.charAt(0);
    }
}
