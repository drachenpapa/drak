package de.drachenpapa.drak.settings;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ControlKeyInputValidator")
class ControlKeyInputValidatorTest {

    private final ControlKeyInputValidator validator = new ControlKeyInputValidator();

    @Test
    @DisplayName("toControlKey returns the validated character")
    void toControlKeyReturnsCharacter() {
        assertThat(validator.toControlKey("k")).isEqualTo('k');
    }

    @Test
    @DisplayName("toControlKey throws for invalid input")
    void toControlKeyThrowsForInvalidInput() {
        assertThatThrownBy(() -> validator.toControlKey("  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("exactly one non-whitespace");
    }

    @Nested
    @DisplayName("isValidKeyInput")
    class IsValidKeyInput {

        @Test
        @DisplayName("returns false for null")
        void returnsFalseForNull() {
            assertThat(validator.isValidKeyInput(null)).isFalse();
        }

        @Test
        @DisplayName("returns false for empty string")
        void returnsFalseForEmptyString() {
            assertThat(validator.isValidKeyInput("")).isFalse();
        }

        @Test
        @DisplayName("returns false for multiple characters")
        void returnsFalseForMultipleCharacters() {
            assertThat(validator.isValidKeyInput("ab")).isFalse();
        }

        @Test
        @DisplayName("returns false for whitespace")
        void returnsFalseForWhitespace() {
            assertThat(validator.isValidKeyInput(" ")).isFalse();
        }

        @Test
        @DisplayName("returns true for single non-whitespace character")
        void returnsTrueForSingleCharacter() {
            assertThat(validator.isValidKeyInput("x")).isTrue();
        }

        @Test
        @DisplayName("returns false for ISO control character (e.g. ESC)")
        void returnsFalseForControlCharacter() {
            assertThat(validator.isValidKeyInput("\u001B")).isFalse();
        }

        @Test
        @DisplayName("returns false for null byte")
        void returnsFalseForNullByte() {
            assertThat(validator.isValidKeyInput("\u0000")).isFalse();
        }
    }
}
