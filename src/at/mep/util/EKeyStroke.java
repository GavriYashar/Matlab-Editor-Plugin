package at.mep.util;

import java.awt.event.KeyEvent;

/**
 * Created by Andreas Justin on 2016-08-25.
 */
public enum EKeyStroke {
    VK_COMMA(KeyEvent.VK_COMMA, "COMMA"),
    VK_PERIOD(KeyEvent.VK_PERIOD, "PERIOD"),
    VK_SEMICOLON(KeyEvent.VK_SEMICOLON, "SEMICOLON"),
    VK_SPACE(KeyEvent.VK_SPACE, "SPACE"),

    VK_SLASH(KeyEvent.VK_SLASH, "SLASH"),
    VK_BACK_SLASH(KeyEvent.VK_BACK_SLASH, "BACK_SLASH"),

    VK_EQUALS(KeyEvent.VK_EQUALS, "EQUALS"),

    VK_OPEN_BRACKET(KeyEvent.VK_OPEN_BRACKET, "OPEN_BRACKET"),
    VK_CLOSE_BRACKET(KeyEvent.VK_CLOSE_BRACKET, "CLOSE_BRACKET"),
    VK_BRACELEFT(KeyEvent.VK_BRACELEFT, "BRACELEFT"),
    VK_BRACERIGHT(KeyEvent.VK_BRACERIGHT, "BRACERIGHT"),
    VK_LEFT_PARENTHESIS(KeyEvent.VK_LEFT_PARENTHESIS, "LEFT_PARENTHESIS"),
    VK_RIGHT_PARENTHESIS(KeyEvent.VK_RIGHT_PARENTHESIS, "RIGHT_PARENTHESIS"),

    VK_ENTER(KeyEvent.VK_ENTER, "ENTER"),
    VK_ESCAPE(KeyEvent.VK_ESCAPE, "ESCAPE"),
    VK_BACK_SPACE(KeyEvent.VK_BACK_SPACE, "BACK_SPACE"),
    VK_TAB(KeyEvent.VK_TAB, "TAB"),
    VK_CANCEL(KeyEvent.VK_CANCEL, "CANCEL"),
    VK_CLEAR(KeyEvent.VK_CLEAR, "CLEAR"),

    VK_SHIFT(KeyEvent.VK_SHIFT, "SHIFT"),
    VK_CONTROL(KeyEvent.VK_CONTROL, "CONTROL"),
    VK_ALT(KeyEvent.VK_ALT, "ALT"),
    VK_CAPS_LOCK(KeyEvent.VK_CAPS_LOCK, "CAPS_LOCK"),

    VK_PRINTSCREEN(KeyEvent.VK_PRINTSCREEN, "PRINTSCREEN"),
    VK_SCROLL_LOCK(KeyEvent.VK_SCROLL_LOCK, "SCROLL_LOCK"),
    VK_PAUSE(KeyEvent.VK_PAUSE, "PAUSE"),

    VK_INSERT(KeyEvent.VK_INSERT, "INSERT"),
    VK_DELETE(KeyEvent.VK_DELETE, "DELETE"),
    VK_HOME(KeyEvent.VK_HOME, "HOME"),
    VK_END(KeyEvent.VK_END, "END"),
    VK_PAGE_UP(KeyEvent.VK_PAGE_UP, "PAGE_UP"),
    VK_PAGE_DOWN(KeyEvent.VK_PAGE_DOWN, "PAGE_DOWN"),

    VK_LEFT(KeyEvent.VK_LEFT, "LEFT"),
    VK_UP(KeyEvent.VK_UP, "UP"),
    VK_DOWN(KeyEvent.VK_DOWN, "DOWN"),
    VK_RIGHT(KeyEvent.VK_RIGHT, "RIGHT"),

    VK_NUM_LOCK(KeyEvent.VK_NUM_LOCK, "NUM_LOCK"),
    VK_DIVIDE(KeyEvent.VK_DIVIDE, "DIVIDE"),
    VK_MULTIPLY(KeyEvent.VK_MULTIPLY, "MULTIPLY"),
    VK_SUBTRACT(KeyEvent.VK_SUBTRACT, "SUBTRACT"),
    VK_ADD(KeyEvent.VK_ADD, "ADD"),
    VK_SEPARATOR(KeyEvent.VK_SEPARATOR, "SEPARATOR"),
    VK_DECIMAL(KeyEvent.VK_DECIMAL, "DECIMAL"),

    VK_F1(KeyEvent.VK_F1, "F1"),
    VK_F2(KeyEvent.VK_F2, "F2"),
    VK_F3(KeyEvent.VK_F3, "F3"),
    VK_F4(KeyEvent.VK_F4, "F4"),
    VK_F5(KeyEvent.VK_F5, "F5"),
    VK_F6(KeyEvent.VK_F6, "F6"),
    VK_F7(KeyEvent.VK_F7, "F7"),
    VK_F8(KeyEvent.VK_F8, "F8"),
    VK_F9(KeyEvent.VK_F9, "F9"),
    VK_F10(KeyEvent.VK_F10, "F10"),
    VK_F11(KeyEvent.VK_F11, "F11"),
    VK_F12(KeyEvent.VK_F12, "F12"),
    VK_F13(KeyEvent.VK_F13, "F13"),
    VK_F14(KeyEvent.VK_F14, "F14"),
    VK_F15(KeyEvent.VK_F15, "F15"),
    VK_F16(KeyEvent.VK_F16, "F16"),
    VK_F17(KeyEvent.VK_F17, "F17"),
    VK_F18(KeyEvent.VK_F18, "F18"),
    VK_F19(KeyEvent.VK_F19, "F19"),
    VK_F20(KeyEvent.VK_F20, "F20"),
    VK_F21(KeyEvent.VK_F21, "F21"),
    VK_F22(KeyEvent.VK_F22, "F22"),
    VK_F23(KeyEvent.VK_F23, "F23"),
    VK_F24(KeyEvent.VK_F24, "F24"),

    VK_HELP(KeyEvent.VK_HELP, "HELP"),
    VK_META(KeyEvent.VK_META, "META"),

    VK_BACK_QUOTE(KeyEvent.VK_BACK_QUOTE, "BACK_QUOTE"),
    VK_QUOTE(KeyEvent.VK_QUOTE, "QUOTE"),

    VK_KP_UP(KeyEvent.VK_KP_UP, "KP_UP"),
    VK_KP_DOWN(KeyEvent.VK_KP_DOWN, "KP_DOWN"),
    VK_KP_LEFT(KeyEvent.VK_KP_LEFT, "KP_LEFT"),
    VK_KP_RIGHT(KeyEvent.VK_KP_RIGHT, "KP_RIGHT"),

    VK_DEAD_GRAVE(KeyEvent.VK_DEAD_GRAVE, "DEAD_GRAVE"),
    VK_DEAD_ACUTE(KeyEvent.VK_DEAD_ACUTE, "DEAD_ACUTE"),
    VK_DEAD_CIRCUMFLEX(KeyEvent.VK_DEAD_CIRCUMFLEX, "DEAD_CIRCUMFLEX"),
    VK_DEAD_TILDE(KeyEvent.VK_DEAD_TILDE, "DEAD_TILDE"),
    VK_DEAD_MACRON(KeyEvent.VK_DEAD_MACRON, "DEAD_MACRON"),
    VK_DEAD_BREVE(KeyEvent.VK_DEAD_BREVE, "DEAD_BREVE"),
    VK_DEAD_ABOVEDOT(KeyEvent.VK_DEAD_ABOVEDOT, "DEAD_ABOVEDOT"),
    VK_DEAD_DIAERESIS(KeyEvent.VK_DEAD_DIAERESIS, "DEAD_DIAERESIS"),
    VK_DEAD_ABOVERING(KeyEvent.VK_DEAD_ABOVERING, "DEAD_ABOVERING"),
    VK_DEAD_DOUBLEACUTE(KeyEvent.VK_DEAD_DOUBLEACUTE, "DEAD_DOUBLEACUTE"),
    VK_DEAD_CARON(KeyEvent.VK_DEAD_CARON, "DEAD_CARON"),
    VK_DEAD_CEDILLA(KeyEvent.VK_DEAD_CEDILLA, "DEAD_CEDILLA"),
    VK_DEAD_OGONEK(KeyEvent.VK_DEAD_OGONEK, "DEAD_OGONEK"),
    VK_DEAD_IOTA(KeyEvent.VK_DEAD_IOTA, "DEAD_IOTA"),
    VK_DEAD_VOICED_SOUND(KeyEvent.VK_DEAD_VOICED_SOUND, "DEAD_VOICED_SOUND"),
    VK_DEAD_SEMIVOICED_SOUND(KeyEvent.VK_DEAD_SEMIVOICED_SOUND, "DEAD_SEMIVOICED_SOUND"),

    VK_AMPERSAND(KeyEvent.VK_AMPERSAND, "AMPERSAND"),
    VK_ASTERISK(KeyEvent.VK_ASTERISK, "ASTERISK"),
    VK_QUOTEDBL(KeyEvent.VK_QUOTEDBL, "QUOTEDBL"),
    VK_LESS(KeyEvent.VK_LESS, "LESS"),
    VK_GREATER(KeyEvent.VK_GREATER, "GREATER"),
    VK_AT(KeyEvent.VK_AT, "AT"),
    VK_COLON(KeyEvent.VK_COLON, "COLON"),
    VK_CIRCUMFLEX(KeyEvent.VK_CIRCUMFLEX, "CIRCUMFLEX"),
    VK_DOLLAR(KeyEvent.VK_DOLLAR, "DOLLAR"),
    VK_EURO_SIGN(KeyEvent.VK_EURO_SIGN, "EURO_SIGN"),
    VK_EXCLAMATION_MARK(KeyEvent.VK_EXCLAMATION_MARK, "EXCLAMATION_MARK"),
    VK_INVERTED_EXCLAMATION_MARK(KeyEvent.VK_INVERTED_EXCLAMATION_MARK, "INVERTED_EXCLAMATION_MARK"),
    VK_NUMBER_SIGN(KeyEvent.VK_NUMBER_SIGN, "NUMBER_SIGN"),
    VK_MINUS(KeyEvent.VK_MINUS, "MINUS"),
    VK_PLUS(KeyEvent.VK_PLUS, "PLUS"),
    VK_UNDERSCORE(KeyEvent.VK_UNDERSCORE, "UNDERSCORE"),
    VK_FINAL(KeyEvent.VK_FINAL, "FINAL"),
    VK_CONVERT(KeyEvent.VK_CONVERT, "CONVERT"),
    VK_NONCONVERT(KeyEvent.VK_NONCONVERT, "NONCONVERT"),
    VK_ACCEPT(KeyEvent.VK_ACCEPT, "ACCEPT"),
    VK_MODECHANGE(KeyEvent.VK_MODECHANGE, "MODECHANGE"),
    VK_KANA(KeyEvent.VK_KANA, "KANA"),
    VK_KANJI(KeyEvent.VK_KANJI, "KANJI"),
    VK_ALPHANUMERIC(KeyEvent.VK_ALPHANUMERIC, "ALPHANUMERIC"),
    VK_KATAKANA(KeyEvent.VK_KATAKANA, "KATAKANA"),
    VK_HIRAGANA(KeyEvent.VK_HIRAGANA, "HIRAGANA"),
    VK_FULL_WIDTH(KeyEvent.VK_FULL_WIDTH, "FULL_WIDTH"),
    VK_HALF_WIDTH(KeyEvent.VK_HALF_WIDTH, "HALF_WIDTH"),
    VK_ROMAN_CHARACTERS(KeyEvent.VK_ROMAN_CHARACTERS, "ROMAN_CHARACTERS"),
    VK_ALL_CANDIDATES(KeyEvent.VK_ALL_CANDIDATES, "ALL_CANDIDATES"),
    VK_PREVIOUS_CANDIDATE(KeyEvent.VK_PREVIOUS_CANDIDATE, "PREVIOUS_CANDIDATE"),
    VK_CODE_INPUT(KeyEvent.VK_CODE_INPUT, "CODE_INPUT"),
    VK_JAPANESE_KATAKANA(KeyEvent.VK_JAPANESE_KATAKANA, "JAPANESE_KATAKANA"),
    VK_JAPANESE_HIRAGANA(KeyEvent.VK_JAPANESE_HIRAGANA, "JAPANESE_HIRAGANA"),
    VK_JAPANESE_ROMAN(KeyEvent.VK_JAPANESE_ROMAN, "JAPANESE_ROMAN"),
    VK_KANA_LOCK(KeyEvent.VK_KANA_LOCK, "KANA_LOCK"),
    VK_INPUT_METHOD_ON_OFF(KeyEvent.VK_INPUT_METHOD_ON_OFF, "INPUT_METHOD_ON_OFF"),
    VK_AGAIN(KeyEvent.VK_AGAIN, "AGAIN"),
    VK_UNDO(KeyEvent.VK_UNDO, "UNDO"),
    VK_COPY(KeyEvent.VK_COPY, "COPY"),
    VK_PASTE(KeyEvent.VK_PASTE, "PASTE"),
    VK_CUT(KeyEvent.VK_CUT, "CUT"),
    VK_FIND(KeyEvent.VK_FIND, "FIND"),
    VK_PROPS(KeyEvent.VK_PROPS, "PROPS"),
    VK_STOP(KeyEvent.VK_STOP, "STOP"),
    VK_COMPOSE(KeyEvent.VK_COMPOSE, "COMPOSE"),
    VK_ALT_GRAPH(KeyEvent.VK_ALT_GRAPH, "ALT_GRAPH"),

    VK_0(KeyEvent.VK_0, "0"),
    VK_1(KeyEvent.VK_1, "1"),
    VK_2(KeyEvent.VK_2, "2"),
    VK_3(KeyEvent.VK_3, "3"),
    VK_4(KeyEvent.VK_4, "4"),
    VK_5(KeyEvent.VK_5, "5"),
    VK_6(KeyEvent.VK_6, "6"),
    VK_7(KeyEvent.VK_7, "7"),
    VK_8(KeyEvent.VK_8, "8"),
    VK_9(KeyEvent.VK_9, "9"),

    VK_NUMPAD0(KeyEvent.VK_NUMPAD0, "NUMPAD00"),
    VK_NUMPAD1(KeyEvent.VK_NUMPAD1, "NUMPAD01"),
    VK_NUMPAD2(KeyEvent.VK_NUMPAD2, "NUMPAD02"),
    VK_NUMPAD3(KeyEvent.VK_NUMPAD3, "NUMPAD03"),
    VK_NUMPAD4(KeyEvent.VK_NUMPAD4, "NUMPAD04"),
    VK_NUMPAD5(KeyEvent.VK_NUMPAD5, "NUMPAD05"),
    VK_NUMPAD6(KeyEvent.VK_NUMPAD6, "NUMPAD06"),
    VK_NUMPAD7(KeyEvent.VK_NUMPAD7, "NUMPAD07"),
    VK_NUMPAD8(KeyEvent.VK_NUMPAD8, "NUMPAD08"),
    VK_NUMPAD9(KeyEvent.VK_NUMPAD9, "NUMPAD09"),

    VK_A(KeyEvent.VK_A, "A"),
    VK_B(KeyEvent.VK_B, "B"),
    VK_C(KeyEvent.VK_C, "C"),
    VK_D(KeyEvent.VK_D, "D"),
    VK_E(KeyEvent.VK_E, "E"),
    VK_F(KeyEvent.VK_F, "F"),
    VK_G(KeyEvent.VK_G, "G"),
    VK_H(KeyEvent.VK_H, "H"),
    VK_I(KeyEvent.VK_I, "I"),
    VK_J(KeyEvent.VK_J, "J"),
    VK_K(KeyEvent.VK_K, "K"),
    VK_L(KeyEvent.VK_L, "L"),
    VK_M(KeyEvent.VK_M, "M"),
    VK_N(KeyEvent.VK_N, "N"),
    VK_O(KeyEvent.VK_O, "O"),
    VK_P(KeyEvent.VK_P, "P"),
    VK_Q(KeyEvent.VK_Q, "Q"),
    VK_R(KeyEvent.VK_R, "R"),
    VK_S(KeyEvent.VK_S, "S"),
    VK_T(KeyEvent.VK_T, "T"),
    VK_U(KeyEvent.VK_U, "U"),
    VK_V(KeyEvent.VK_V, "V"),
    VK_W(KeyEvent.VK_W, "W"),
    VK_X(KeyEvent.VK_X, "X"),
    VK_Y(KeyEvent.VK_Y, "Y"),
    VK_Z(KeyEvent.VK_Z, "Z"),

    UNKNOWN(Integer.MIN_VALUE,"");

    private final int keyCode;
    private String command;

    EKeyStroke(int key, String command) {
        this.keyCode = key;
        this.command = command;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public String getCommand() {
        return this.command;
    }

    private void setCommand(String command) {
        this.command = command;
    }

    public static EKeyStroke getKeyStrokeE(int keyCode) {
        EKeyStroke[] list = EKeyStroke.values();
        for (EKeyStroke keystroke : list) {
            if (keystroke.getKeyCode() == keyCode) return keystroke;
        }

        EKeyStroke ret = UNKNOWN;
        ret.setCommand("unknown(0x" + Integer.toString(keyCode, 16) + ")");
        return ret;
    }

    public static EKeyStroke getKeyStrokeE(String command) {
        EKeyStroke[] list = EKeyStroke.values();
        for (EKeyStroke keystroke : list) {
            if (keystroke.getCommand().toLowerCase().equals(command.toLowerCase()))
                return keystroke;
        }
        EKeyStroke ret = UNKNOWN;
        ret.setCommand("unknown command: \"" + command + "\"");
        return null;
    }

}
