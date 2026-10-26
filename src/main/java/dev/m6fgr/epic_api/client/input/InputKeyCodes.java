package dev.m6fgr.epic_api.client.input;

import dev.m6fgr.epic_api.cls.lists.ArraysList;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;

public final class InputKeyCodes {

    private static final ArraysList<Integer> KEYBOARD_KEYS = new ArraysList<>();
    private static final ArraysList<Integer> MOUSE_KEYS = new ArraysList<>();
    private static final ArraysList<Integer> INPUT_ACTIONS = new ArraysList<>();

    private InputKeyCodes() {}

    // --- Digits ---
    public static final int KEY_0 = GLFW.GLFW_KEY_0;
    public static final int KEY_1 = GLFW.GLFW_KEY_1;
    public static final int KEY_2 = GLFW.GLFW_KEY_2;
    public static final int KEY_3 = GLFW.GLFW_KEY_3;
    public static final int KEY_4 = GLFW.GLFW_KEY_4;
    public static final int KEY_5 = GLFW.GLFW_KEY_5;
    public static final int KEY_6 = GLFW.GLFW_KEY_6;
    public static final int KEY_7 = GLFW.GLFW_KEY_7;
    public static final int KEY_8 = GLFW.GLFW_KEY_8;
    public static final int KEY_9 = GLFW.GLFW_KEY_9;

    // --- Letters ---
    public static final int KEY_A = GLFW.GLFW_KEY_A;
    public static final int KEY_B = GLFW.GLFW_KEY_B;
    public static final int KEY_C = GLFW.GLFW_KEY_C;
    public static final int KEY_D = GLFW.GLFW_KEY_D;
    public static final int KEY_E = GLFW.GLFW_KEY_E;
    public static final int KEY_F = GLFW.GLFW_KEY_F;
    public static final int KEY_G = GLFW.GLFW_KEY_G;
    public static final int KEY_H = GLFW.GLFW_KEY_H;
    public static final int KEY_I = GLFW.GLFW_KEY_I;
    public static final int KEY_J = GLFW.GLFW_KEY_J;
    public static final int KEY_K = GLFW.GLFW_KEY_K;
    public static final int KEY_L = GLFW.GLFW_KEY_L;
    public static final int KEY_M = GLFW.GLFW_KEY_M;
    public static final int KEY_N = GLFW.GLFW_KEY_N;
    public static final int KEY_O = GLFW.GLFW_KEY_O;
    public static final int KEY_P = GLFW.GLFW_KEY_P;
    public static final int KEY_Q = GLFW.GLFW_KEY_Q;
    public static final int KEY_R = GLFW.GLFW_KEY_R;
    public static final int KEY_S = GLFW.GLFW_KEY_S;
    public static final int KEY_T = GLFW.GLFW_KEY_T;
    public static final int KEY_U = GLFW.GLFW_KEY_U;
    public static final int KEY_V = GLFW.GLFW_KEY_V;
    public static final int KEY_W = GLFW.GLFW_KEY_W;
    public static final int KEY_X = GLFW.GLFW_KEY_X;
    public static final int KEY_Y = GLFW.GLFW_KEY_Y;
    public static final int KEY_Z = GLFW.GLFW_KEY_Z;

    // --- Function Keys ---
    public static final int KEY_F1 = GLFW.GLFW_KEY_F1;
    public static final int KEY_F2 = GLFW.GLFW_KEY_F2;
    public static final int KEY_F3 = GLFW.GLFW_KEY_F3;
    public static final int KEY_F4 = GLFW.GLFW_KEY_F4;
    public static final int KEY_F5 = GLFW.GLFW_KEY_F5;
    public static final int KEY_F6 = GLFW.GLFW_KEY_F6;
    public static final int KEY_F7 = GLFW.GLFW_KEY_F7;
    public static final int KEY_F8 = GLFW.GLFW_KEY_F8;
    public static final int KEY_F9 = GLFW.GLFW_KEY_F9;
    public static final int KEY_F10 = GLFW.GLFW_KEY_F10;
    public static final int KEY_F11 = GLFW.GLFW_KEY_F11;
    public static final int KEY_F12 = GLFW.GLFW_KEY_F12;
    public static final int KEY_F13 = GLFW.GLFW_KEY_F13;
    public static final int KEY_F14 = GLFW.GLFW_KEY_F14;
    public static final int KEY_F15 = GLFW.GLFW_KEY_F15;
    public static final int KEY_F16 = GLFW.GLFW_KEY_F16;
    public static final int KEY_F17 = GLFW.GLFW_KEY_F17;
    public static final int KEY_F18 = GLFW.GLFW_KEY_F18;
    public static final int KEY_F19 = GLFW.GLFW_KEY_F19;
    public static final int KEY_F20 = GLFW.GLFW_KEY_F20;
    public static final int KEY_F21 = GLFW.GLFW_KEY_F21;
    public static final int KEY_F22 = GLFW.GLFW_KEY_F22;
    public static final int KEY_F23 = GLFW.GLFW_KEY_F23;
    public static final int KEY_F24 = GLFW.GLFW_KEY_F24;
    public static final int KEY_F25 = GLFW.GLFW_KEY_F25;

    // --- Numpad ---
    public static final int KEY_NUMLOCK = GLFW.GLFW_KEY_NUM_LOCK;
    public static final int KEY_NUMPAD0 = GLFW.GLFW_KEY_KP_0;
    public static final int KEY_NUMPAD1 = GLFW.GLFW_KEY_KP_1;
    public static final int KEY_NUMPAD2 = GLFW.GLFW_KEY_KP_2;
    public static final int KEY_NUMPAD3 = GLFW.GLFW_KEY_KP_3;
    public static final int KEY_NUMPAD4 = GLFW.GLFW_KEY_KP_4;
    public static final int KEY_NUMPAD5 = GLFW.GLFW_KEY_KP_5;
    public static final int KEY_NUMPAD6 = GLFW.GLFW_KEY_KP_6;
    public static final int KEY_NUMPAD7 = GLFW.GLFW_KEY_KP_7;
    public static final int KEY_NUMPAD8 = GLFW.GLFW_KEY_KP_8;
    public static final int KEY_NUMPAD9 = GLFW.GLFW_KEY_KP_9;
    public static final int KEY_NUMPADCOMMA = GLFW.GLFW_KEY_KP_DECIMAL;
    public static final int KEY_NUMPADENTER = GLFW.GLFW_KEY_KP_ENTER;
    public static final int KEY_NUMPADEQUALS = GLFW.GLFW_KEY_KP_EQUAL;

    // --- Navigation & Direction ---
    public static final int KEY_DOWN = GLFW.GLFW_KEY_DOWN;
    public static final int KEY_LEFT = GLFW.GLFW_KEY_LEFT;
    public static final int KEY_RIGHT = GLFW.GLFW_KEY_RIGHT;
    public static final int KEY_UP = GLFW.GLFW_KEY_UP;

    // --- Symbols & Punctuation ---
    public static final int KEY_ADD = GLFW.GLFW_KEY_KP_ADD;
    public static final int KEY_APOSTROPHE = GLFW.GLFW_KEY_APOSTROPHE;
    public static final int KEY_BACKSLASH = GLFW.GLFW_KEY_BACKSLASH;
    public static final int KEY_COMMA = GLFW.GLFW_KEY_COMMA;
    public static final int KEY_EQUALS = GLFW.GLFW_KEY_EQUAL;
    public static final int KEY_GRAVE = GLFW.GLFW_KEY_GRAVE_ACCENT;
    public static final int KEY_LBRACKET = GLFW.GLFW_KEY_LEFT_BRACKET;
    public static final int KEY_MINUS = GLFW.GLFW_KEY_MINUS;
    public static final int KEY_MULTIPLY = GLFW.GLFW_KEY_KP_MULTIPLY;
    public static final int KEY_PERIOD = GLFW.GLFW_KEY_PERIOD;
    public static final int KEY_RBRACKET = GLFW.GLFW_KEY_RIGHT_BRACKET;
    public static final int KEY_SEMICOLON = GLFW.GLFW_KEY_SEMICOLON;
    public static final int KEY_SLASH = GLFW.GLFW_KEY_SLASH;
    public static final int KEY_SPACE = GLFW.GLFW_KEY_SPACE;
    public static final int KEY_TAB = GLFW.GLFW_KEY_TAB;

    // --- Modifiers & Utility ---
    public static final int KEY_LALT = GLFW.GLFW_KEY_LEFT_ALT;
    public static final int KEY_LCONTROL = GLFW.GLFW_KEY_LEFT_CONTROL;
    public static final int KEY_LSHIFT = GLFW.GLFW_KEY_LEFT_SHIFT;
    public static final int KEY_LWIN = GLFW.GLFW_KEY_LEFT_SUPER;
    public static final int KEY_RALT = GLFW.GLFW_KEY_RIGHT_ALT;
    public static final int KEY_RCONTROL = GLFW.GLFW_KEY_RIGHT_CONTROL;
    public static final int KEY_RSHIFT = GLFW.GLFW_KEY_RIGHT_SHIFT;
    public static final int KEY_RWIN = GLFW.GLFW_KEY_RIGHT_SUPER;
    public static final int KEY_RETURN = GLFW.GLFW_KEY_ENTER;
    public static final int KEY_ESCAPE = GLFW.GLFW_KEY_ESCAPE;
    public static final int KEY_BACKSPACE = GLFW.GLFW_KEY_BACKSPACE;
    public static final int KEY_DELETE = GLFW.GLFW_KEY_DELETE;
    public static final int KEY_END = GLFW.GLFW_KEY_END;
    public static final int KEY_HOME = GLFW.GLFW_KEY_HOME;
    public static final int KEY_INSERT = GLFW.GLFW_KEY_INSERT;
    public static final int KEY_PAGEDOWN = GLFW.GLFW_KEY_PAGE_DOWN;
    public static final int KEY_PAGEUP = GLFW.GLFW_KEY_PAGE_UP;
    public static final int KEY_CAPSLOCK = GLFW.GLFW_KEY_CAPS_LOCK;
    public static final int KEY_PAUSE = GLFW.GLFW_KEY_PAUSE;
    public static final int KEY_SCROLLLOCK = GLFW.GLFW_KEY_SCROLL_LOCK;
    public static final int KEY_PRINTSCREEN = GLFW.GLFW_KEY_PRINT_SCREEN;

    // --- Input Actions ---
    public static final int PRESS = GLFW.GLFW_PRESS;
    public static final int RELEASE = GLFW.GLFW_RELEASE;
    public static final int REPEAT = GLFW.GLFW_REPEAT;

    // --- Mouse Buttons ---
    public static final int MOUSE_BUTTON_LEFT = GLFW.GLFW_MOUSE_BUTTON_LEFT;
    public static final int MOUSE_BUTTON_MIDDLE = GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
    public static final int MOUSE_BUTTON_RIGHT = GLFW.GLFW_MOUSE_BUTTON_RIGHT;

    // --- Modifiers & Cursor modes ---
    public static final int MOD_CONTROL = GLFW.GLFW_MOD_CONTROL;
    public static final int CURSOR = GLFW.GLFW_CURSOR;
    public static final int CURSOR_DISABLED = GLFW.GLFW_CURSOR_DISABLED;
    public static final int CURSOR_NORMAL = GLFW.GLFW_CURSOR_NORMAL;

    static {
        MOUSE_KEYS.addAsArray(
                MOUSE_BUTTON_LEFT,
                MOUSE_BUTTON_MIDDLE,
                MOUSE_BUTTON_RIGHT
        );

        KEYBOARD_KEYS.addAsArray(
                KEY_0, KEY_1, KEY_2, KEY_3, KEY_4, KEY_5, KEY_6, KEY_7, KEY_8, KEY_9,
                KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G, KEY_H, KEY_I, KEY_J,
                KEY_K, KEY_L, KEY_M, KEY_N, KEY_O, KEY_P, KEY_Q, KEY_R, KEY_S, KEY_T,
                KEY_U, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_Z,
                KEY_F1, KEY_F2, KEY_F3, KEY_F4, KEY_F5, KEY_F6, KEY_F7, KEY_F8, KEY_F9,
                KEY_F10, KEY_F11, KEY_F12, KEY_F13, KEY_F14, KEY_F15, KEY_F16, KEY_F17,
                KEY_F18, KEY_F19, KEY_F20, KEY_F21, KEY_F22, KEY_F23, KEY_F24, KEY_F25,
                KEY_NUMLOCK, KEY_NUMPAD0, KEY_NUMPAD1, KEY_NUMPAD2, KEY_NUMPAD3, KEY_NUMPAD4,
                KEY_NUMPAD5, KEY_NUMPAD6, KEY_NUMPAD7, KEY_NUMPAD8, KEY_NUMPAD9,
                KEY_NUMPADCOMMA, KEY_NUMPADENTER, KEY_NUMPADEQUALS,
                KEY_DOWN, KEY_LEFT, KEY_RIGHT, KEY_UP,
                KEY_ADD, KEY_APOSTROPHE, KEY_BACKSLASH, KEY_COMMA, KEY_EQUALS, KEY_GRAVE,
                KEY_LBRACKET, KEY_MINUS, KEY_MULTIPLY, KEY_PERIOD, KEY_RBRACKET,
                KEY_SEMICOLON, KEY_SLASH, KEY_SPACE, KEY_TAB,
                KEY_LALT, KEY_LCONTROL, KEY_LSHIFT, KEY_LWIN,
                KEY_RALT, KEY_RCONTROL, KEY_RSHIFT, KEY_RWIN,
                KEY_RETURN, KEY_ESCAPE, KEY_BACKSPACE, KEY_DELETE,
                KEY_END, KEY_HOME, KEY_INSERT, KEY_PAGEDOWN, KEY_PAGEUP,
                KEY_CAPSLOCK, KEY_PAUSE, KEY_SCROLLLOCK, KEY_PRINTSCREEN
        );

        INPUT_ACTIONS.addAsArray(
                PRESS,
                RELEASE,
                REPEAT
        );
    }

    public static List<Integer> getKeyboardKeys() {
        return Collections.unmodifiableList(KEYBOARD_KEYS);
    }

    public static List<Integer> getMouseKeys() {
        return Collections.unmodifiableList(MOUSE_KEYS);
    }

    public static List<Integer> getInputActions() {
        return Collections.unmodifiableList(INPUT_ACTIONS);
    }

    public static boolean isInputAction(int keyCode) {
        return INPUT_ACTIONS.contains(keyCode);
    }

    public static boolean isKeyboardKey(int keyCode) {
        return KEYBOARD_KEYS.contains(keyCode);
    }

    public static boolean isMouseButton(int buttonCode) {
        return MOUSE_KEYS.contains(buttonCode);
    }
}