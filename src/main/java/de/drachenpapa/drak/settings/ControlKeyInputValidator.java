package de.drachenpapa.drak.settings;

/**
 * Validates and parses control key input entered in the settings UI.
 */
class ControlKeyInputValidator {

    boolean isInvalidKeyInput(String input) {
        return input == null || input.length() != 1 || Character.isWhitespace(input.charAt(0));
    }

    char toControlKey(String input) {
        if (isInvalidKeyInput(input)) {
            throw new IllegalArgumentException("Control key must be exactly one non-whitespace character.");
        }
        return input.charAt(0);
    }
}
