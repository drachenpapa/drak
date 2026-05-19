package de.drachenpapa.drak.settings;

import java.awt.*;

/**
 * Central constants used by settings UI components.
 */
final class SettingsConstants {

    private SettingsConstants() {
    }

    static final class Players {
        static final int MAX_PLAYERS = 6;
        static final int DEFAULT_ACTIVE_PLAYERS = 2;
        static final String PLAYER_NAME_PREFIX = "Player ";
        private static final Color[] DEFAULT_COLORS = {
            new Color(255, 0, 0),
            new Color(0, 255, 0),
            new Color(0, 0, 255),
            new Color(255, 0, 255),
            new Color(0, 255, 255),
            new Color(255, 255, 0)
        };
        private static final char[] DEFAULT_LEFT_KEYS = {'1', 'y', 'b', ',', 'o', '2'};
        private static final char[] DEFAULT_RIGHT_KEYS = {'q', 'x', 'n', '.', '0', '3'};

        private Players() {
        }

        static Color[] defaultColors() {
            return DEFAULT_COLORS.clone();
        }

        static char[] defaultLeftKeys() {
            return DEFAULT_LEFT_KEYS.clone();
        }

        static char[] defaultRightKeys() {
            return DEFAULT_RIGHT_KEYS.clone();
        }
    }

    static final class Speed {
        static final int DEFAULT = 3;
        static final int MIN = 1;
        static final int MAX = 5;

        private Speed() {
        }
    }

    static final class Commands {
        static final String START_GAME = "Start Game";
        static final String LOAD_DEFAULTS = "Load Defaults";

        private Commands() {
        }
    }

    static final class Ui {
        static final int FRAME_WIDTH = 600;
        static final int FRAME_HEIGHT = 500;
        static final Color PANEL_BACKGROUND = new Color(205, 205, 205);
        static final Color LOAD_DEFAULTS_BUTTON_BACKGROUND = new Color(180, 180, 180);
        static final Color START_BUTTON_BACKGROUND = new Color(80, 150, 240);
        static final Color TRANSPARENT_BLACK = new Color(0, 0, 0, 0);
        static final int BUTTON_BORDER_VERTICAL = 5;
        static final int BUTTON_BORDER_HORIZONTAL = 10;

        static final int PLAYER_FLOW_GAP = 6;
        static final int PLAYER_ROW_SPACER_LARGE = 20;
        static final int PLAYER_ROW_SPACER_SMALL = 10;
        static final int ALL_SELECTOR_OFFSET = 6;
        static final int ALL_SELECTOR_SIZE = 25;

        static final int NAME_FIELD_WIDTH = 150;
        static final int NAME_FIELD_HEIGHT = 25;
        static final int KEY_BUTTON_WIDTH = 85;
        static final int KEY_BUTTON_HEIGHT = 25;

        static final int OPTIONS_SPEED_LABEL_WIDTH = 90;
        static final int OPTIONS_SPEED_LABEL_HEIGHT = 25;
        static final int OPTIONS_SPEED_SPINNER_WIDTH = 40;
        static final int OPTIONS_SPEED_SPINNER_HEIGHT = 25;
        static final int OPTIONS_TOP_ROW_MAX_HEIGHT = 40;
        static final int OPTIONS_START_BUTTON_WIDTH = 140;
        static final int OPTIONS_START_BUTTON_HEIGHT = 35;
        static final int OPTIONS_MAIN_SPACER = 8;

        static final int SETTINGS_PANEL_SPACER_SMALL = 6;
        static final int SETTINGS_PANEL_SPACER_LARGE = 10;

        private Ui() {
        }
    }

    static final class Text {
        static final String CHOOSE_PLAYER_COLOR = "Choose Player Color";
        static final String ENTER_LEFT_KEY = "Enter new left control key for ";
        static final String ENTER_RIGHT_KEY = "Enter new right control key for ";
        static final String INVALID_CONTROL_KEY_TITLE = "Invalid Control Key";
        static final String INVALID_CONTROL_KEY_MESSAGE = "Please enter exactly one non-whitespace character.";
        static final String SPEED_LEVEL_LABEL = "Speed Level:";
        static final String ALL_LABEL = "All";
        static final String COLOR_BUTTON = "Color";

        private Text() {
        }
    }
}
