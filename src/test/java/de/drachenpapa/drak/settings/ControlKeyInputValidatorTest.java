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
    @DisplayName("isInvalidKeyInput")
    class IsInvalidKeyInput {

        @Test
        @DisplayName("returns true for null")
        void returnsTrueForNull() {
            assertThat(validator.isInvalidKeyInput(null)).isTrue();
        }

        @Test
        @DisplayName("returns true for empty string")
        void returnsTrueForEmptyString() {
            assertThat(validator.isInvalidKeyInput("")).isTrue();
        }

        @Test
        @DisplayName("returns true for multiple characters")
        void returnsTrueForMultipleCharacters() {
            assertThat(validator.isInvalidKeyInput("ab")).isTrue();
        }

        @Test
        @DisplayName("returns true for whitespace")
        void returnsTrueForWhitespace() {
            assertThat(validator.isInvalidKeyInput(" ")).isTrue();
        }

        @Test
        @DisplayName("returns false for single non-whitespace character")
        void returnsFalseForSingleCharacter() {
            assertThat(validator.isInvalidKeyInput("x")).isFalse();
        }
    }
}
