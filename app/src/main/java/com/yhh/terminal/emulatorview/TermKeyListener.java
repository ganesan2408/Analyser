package com.yhh.terminal.emulatorview;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.yhh.utils.ConstUtils;

import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

/**
 * An ASCII key listener. Supports control characters and escape. Keeps track of
 * the current state of the alt, shift, fn, and control keys.
 *
 */
public class TermKeyListener {
    private final static String TAG =  ConstUtils.DEBUG_TAG+ "TermKeyListener";
    private static final boolean LOG_MISC = false;
    private static final boolean LOG_KEYS = false;
    private static final boolean LOG_COMBINING_ACCENT = false;
    
    
    public final static String MY_KEYCODE_UP = "\033[A";
    public final static String MY_KEYCODE_DOWN = "\033[B";
    public final static String MY_KEYCODE_RIGHT = "\033[C";
    public final static String MY_KEYCODE_LEFT = "\033[D";
    public final static String MY_KEYCODE_TAB = "\011";
    

    /** Disabled for now because it interferes with ALT processing on phones with physical keyboards. */
    private final static boolean SUPPORT_8_BIT_META = false;

    private static final int KEYMOD_ALT   = 0x80000000;
    private static final int KEYMOD_CTRL  = 0x40000000;
    private static final int KEYMOD_SHIFT = 0x20000000;
    /** Means this maps raw scancode */
    private static final int KEYMOD_SCAN  = 0x10000000;

    private static Map<Integer, String> mKeyMap;

    private String[] mKeyCodes = new String[256];
    private String[] mAppKeyCodes = new String[256];

    private void initKeyCodes() {
        mKeyMap = new HashMap<Integer, String>();
        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_LEFT, "\033[1;2D");
        mKeyMap.put(KEYMOD_ALT | KeyEvent.KEYCODE_DPAD_LEFT, "\033[1;3D");
        mKeyMap.put(KEYMOD_ALT | KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_LEFT, "\033[1;4D");
        mKeyMap.put(KEYMOD_CTRL | KeyEvent.KEYCODE_DPAD_LEFT, "\033[1;5D");
        mKeyMap.put(KEYMOD_CTRL | KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_LEFT, "\033[1;6D");
        mKeyMap.put(KEYMOD_CTRL | KEYMOD_ALT | KeyEvent.KEYCODE_DPAD_LEFT, "\033[1;7D");
        mKeyMap.put(KEYMOD_CTRL | KEYMOD_ALT | KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_LEFT, "\033[1;8D");

        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_RIGHT, "\033[1;2C");
        mKeyMap.put(KEYMOD_ALT | KeyEvent.KEYCODE_DPAD_RIGHT, "\033[1;3C");
        mKeyMap.put(KEYMOD_ALT | KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_RIGHT, "\033[1;4C");
        mKeyMap.put(KEYMOD_CTRL | KeyEvent.KEYCODE_DPAD_RIGHT, "\033[1;5C");
        mKeyMap.put(KEYMOD_CTRL | KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_RIGHT, "\033[1;6C");
        mKeyMap.put(KEYMOD_CTRL | KEYMOD_ALT | KeyEvent.KEYCODE_DPAD_RIGHT, "\033[1;7C");
        mKeyMap.put(KEYMOD_CTRL | KEYMOD_ALT | KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_RIGHT, "\033[1;8C");

        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_UP, "\033[1;2A");
        mKeyMap.put(KEYMOD_ALT | KeyEvent.KEYCODE_DPAD_UP, "\033[1;3A");
        mKeyMap.put(KEYMOD_ALT | KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_UP, "\033[1;4A");
        mKeyMap.put(KEYMOD_CTRL | KeyEvent.KEYCODE_DPAD_UP, "\033[1;5A");
        mKeyMap.put(KEYMOD_CTRL | KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_UP, "\033[1;6A");
        mKeyMap.put(KEYMOD_CTRL | KEYMOD_ALT | KeyEvent.KEYCODE_DPAD_UP, "\033[1;7A");
        mKeyMap.put(KEYMOD_CTRL | KEYMOD_ALT | KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_UP, "\033[1;8A");

        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_DOWN, "\033[1;2B");
        mKeyMap.put(KEYMOD_ALT | KeyEvent.KEYCODE_DPAD_DOWN, "\033[1;3B");
        mKeyMap.put(KEYMOD_ALT | KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_DOWN, "\033[1;4B");
        mKeyMap.put(KEYMOD_CTRL | KeyEvent.KEYCODE_DPAD_DOWN, "\033[1;5B");
        mKeyMap.put(KEYMOD_CTRL | KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_DOWN, "\033[1;6B");
        mKeyMap.put(KEYMOD_CTRL | KEYMOD_ALT | KeyEvent.KEYCODE_DPAD_DOWN, "\033[1;7B");
        mKeyMap.put(KEYMOD_CTRL | KEYMOD_ALT | KEYMOD_SHIFT | KeyEvent.KEYCODE_DPAD_DOWN, "\033[1;8B");

        //^[[3~
        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_FORWARD_DEL, "\033[3;2~");
        mKeyMap.put(KEYMOD_ALT | KeyEvent.KEYCODE_FORWARD_DEL, "\033[3;3~");
        mKeyMap.put(KEYMOD_CTRL | KeyEvent.KEYCODE_FORWARD_DEL, "\033[3;5~");

        //^[[2~
        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_INSERT, "\033[2;2~");
        mKeyMap.put(KEYMOD_ALT | KeyEvent.KEYCODE_INSERT, "\033[2;3~");
        mKeyMap.put(KEYMOD_CTRL | KeyEvent.KEYCODE_INSERT, "\033[2;5~");

        mKeyMap.put(KEYMOD_CTRL | KeyEvent.KEYCODE_MOVE_HOME, "\033[1;5H");
        mKeyMap.put(KEYMOD_CTRL | KeyEvent.KEYCODE_MOVE_END, "\033[1;5F");

        mKeyMap.put(KEYMOD_ALT | KeyEvent.KEYCODE_ENTER, "\033\r");
        mKeyMap.put(KEYMOD_CTRL | KeyEvent.KEYCODE_ENTER, "\n");
        // Duh, so special...
        mKeyMap.put(KEYMOD_CTRL | KeyEvent.KEYCODE_SPACE, "\000");

        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_F1, "\033[1;2P");
        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_F2, "\033[1;2Q");
        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_F3, "\033[1;2R");
        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_F4, "\033[1;2S");
        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_F5, "\033[15;2~");
        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_F6, "\033[17;2~");
        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_F7, "\033[18;2~");
        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_F8, "\033[19;2~");
        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_F9, "\033[20;2~");
        mKeyMap.put(KEYMOD_SHIFT | KeyEvent.KEYCODE_F10, "\033[21;2~");

        // 上/下/左/右
        mKeyCodes[KeyEvent.KEYCODE_DPAD_UP] = "\033[A";
        mKeyCodes[KeyEvent.KEYCODE_DPAD_DOWN] = "\033[B";
        mKeyCodes[KeyEvent.KEYCODE_DPAD_RIGHT] = "\033[C";
        mKeyCodes[KeyEvent.KEYCODE_DPAD_LEFT] = "\033[D";
        mKeyCodes[KeyEvent.KEYCODE_TAB] = "\011";
        
        setFnKeys("vt100");
        mKeyCodes[KeyEvent.KEYCODE_SYSRQ] = "\033[32~"; // Sys Request / Print
        mKeyCodes[KeyEvent.KEYCODE_BREAK] = "\033[34~"; // Pause/Break
        
        mKeyCodes[KeyEvent.KEYCODE_DPAD_CENTER] = "\015";
        mKeyCodes[KeyEvent.KEYCODE_ENTER] = "\015";
        mKeyCodes[KeyEvent.KEYCODE_ESCAPE] = "\033";

        mKeyCodes[KeyEvent.KEYCODE_INSERT] = "\033[2~";
        mKeyCodes[KeyEvent.KEYCODE_FORWARD_DEL] = "\033[3~";
        // Home/End keys are set by setFnKeys()
        mKeyCodes[KeyEvent.KEYCODE_PAGE_UP] = "\033[5~";
        mKeyCodes[KeyEvent.KEYCODE_PAGE_DOWN] = "\033[6~";
        mKeyCodes[KeyEvent.KEYCODE_DEL]= "\177";
        mKeyCodes[KeyEvent.KEYCODE_NUM_LOCK] = "\033OP";
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_DIVIDE] = "/";
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_MULTIPLY] = "*";
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_SUBTRACT] = "-";
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_ADD] = "+";
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_ENTER] = "\015";
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_EQUALS] = "=";
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_COMMA] = ",";

        // Keypad is used for cursor/func keys
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_DOT] = mKeyCodes[KeyEvent.KEYCODE_FORWARD_DEL];
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_0] = mKeyCodes[KeyEvent.KEYCODE_INSERT];
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_1] = mKeyCodes[KeyEvent.KEYCODE_MOVE_END];
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_2] = mKeyCodes[KeyEvent.KEYCODE_DPAD_DOWN];
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_3] = mKeyCodes[KeyEvent.KEYCODE_PAGE_DOWN];
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_4] = mKeyCodes[KeyEvent.KEYCODE_DPAD_LEFT];
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_5] = "5";
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_6] = mKeyCodes[KeyEvent.KEYCODE_DPAD_RIGHT];
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_7] = mKeyCodes[KeyEvent.KEYCODE_MOVE_HOME];
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_8] = mKeyCodes[KeyEvent.KEYCODE_DPAD_UP];
        mKeyCodes[KeyEvent.KEYCODE_NUMPAD_9] = mKeyCodes[KeyEvent.KEYCODE_PAGE_UP];

        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_DIVIDE] = "\033Oo";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_MULTIPLY] = "\033Oj";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_SUBTRACT] = "\033Om";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_ADD] = "\033Ok";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_ENTER] = "\033OM";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_EQUALS] = "\033OX";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_DOT] = "\033On";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_COMMA] = "\033Ol";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_0] = "\033Op";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_1] = "\033Oq";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_2] = "\033Or";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_3] = "\033Os";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_4] = "\033Ot";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_5] = "\033Ou";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_6] = "\033Ov";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_7] = "\033Ow";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_8] = "\033Ox";
        mAppKeyCodes[KeyEvent.KEYCODE_NUMPAD_9] = "\033Oy";
    }

    public void setCursorKeysApplicationMode(boolean val) {
        if (LOG_MISC) {
            Log.d(EmulatorDebug.LOG_TAG, "CursorKeysApplicationMode=" + val);
        }
        if (val) {
            mKeyCodes[KeyEvent.KEYCODE_NUMPAD_8] = mKeyCodes[KeyEvent.KEYCODE_DPAD_UP] = "\033OA";
            mKeyCodes[KeyEvent.KEYCODE_NUMPAD_2] = mKeyCodes[KeyEvent.KEYCODE_DPAD_DOWN] = "\033OB";
            mKeyCodes[KeyEvent.KEYCODE_NUMPAD_6] = mKeyCodes[KeyEvent.KEYCODE_DPAD_RIGHT] = "\033OC";
            mKeyCodes[KeyEvent.KEYCODE_NUMPAD_4] = mKeyCodes[KeyEvent.KEYCODE_DPAD_LEFT] = "\033OD";
        } else {
            mKeyCodes[KeyEvent.KEYCODE_NUMPAD_8] = mKeyCodes[KeyEvent.KEYCODE_DPAD_UP] = "\033[A";
            mKeyCodes[KeyEvent.KEYCODE_NUMPAD_2] = mKeyCodes[KeyEvent.KEYCODE_DPAD_DOWN] = "\033[B";
            mKeyCodes[KeyEvent.KEYCODE_NUMPAD_6] = mKeyCodes[KeyEvent.KEYCODE_DPAD_RIGHT] = "\033[C";
            mKeyCodes[KeyEvent.KEYCODE_NUMPAD_4] = mKeyCodes[KeyEvent.KEYCODE_DPAD_LEFT] = "\033[D";
        }
    }

    /**
     * The state engine for a modifier key. Can be pressed, released, locked,
     * and so on.
     *
     */
    private class ModifierKey {

        private int mState;

        private static final int UNPRESSED = 0;

        private static final int PRESSED = 1;

        private static final int RELEASED = 2;

        private static final int USED = 3;

        private static final int LOCKED = 4;

        /**
         * Construct a modifier key. UNPRESSED by default.
         *
         */
        public ModifierKey() {
            mState = UNPRESSED;
        }

        public void onPress() {
            switch (mState) {
            case PRESSED:
                // This is a repeat before use
                break;
            case RELEASED:
                mState = LOCKED;
                break;
            case USED:
                // This is a repeat after use
                break;
            case LOCKED:
                mState = UNPRESSED;
                break;
            default:
                mState = PRESSED;
                break;
            }
        }

        public void onRelease() {
            switch (mState) {
            case USED:
                mState = UNPRESSED;
                break;
            case PRESSED:
                mState = RELEASED;
                break;
            default:
                // Leave state alone
                break;
            }
        }

        public void adjustAfterKeypress() {
            switch (mState) {
            case PRESSED:
                mState = USED;
                break;
            case RELEASED:
                mState = UNPRESSED;
                break;
            default:
                // Leave state alone
                break;
            }
        }

        public boolean isActive() {
            return mState != UNPRESSED;
        }

        public int getUIMode() {
            switch (mState) {
            default:
            case UNPRESSED:
                return TextRenderer.MODE_OFF;
            case PRESSED:
            case RELEASED:
            case USED:
                return TextRenderer.MODE_ON;
            case LOCKED:
                return TextRenderer.MODE_LOCKED;
            }
        }
    }

    private ModifierKey mAltKey = new ModifierKey();

    private ModifierKey mCapKey = new ModifierKey();

    private ModifierKey mControlKey = new ModifierKey();

    private ModifierKey mFnKey = new ModifierKey();

    private int mCursorMode;

    private boolean mHardwareControlKey;

    private TermSession mTermSession;

    private int mBackKeyCode;
    private boolean mAltSendsEsc;

    private int mCombiningAccent;

    // Map keycodes out of (above) the Unicode code point space.
    static public final int KEYCODE_OFFSET = 0xA00000;

    /**
     * Construct a term key listener.
     *
     */
    public TermKeyListener(TermSession termSession) {
        mTermSession = termSession;
        initKeyCodes();
        updateCursorMode();
    }

    public void setBackKeyCharacter(int code) {
        mBackKeyCode = code;
    }

    public void setAltSendsEsc(boolean flag) {
        mAltSendsEsc = flag;
    }

    public void handleHardwareControlKey(boolean down) {
        mHardwareControlKey = down;
    }

    public void onPause() {
        // Ensure we don't have any left-over modifier state when switching
        // views.
        mHardwareControlKey = false;
    }

    public void onResume() {
        // Nothing special.
    }

    public void handleControlKey(boolean down) {
        if (down) {
            mControlKey.onPress();
        } else {
            mControlKey.onRelease();
        }
        updateCursorMode();
    }

    public void handleFnKey(boolean down) {
        if (down) {
            mFnKey.onPress();
        } else {
            mFnKey.onRelease();
        }
        updateCursorMode();
    }

    public void setTermType(String termType) {
        setFnKeys(termType);
    }

    private void setFnKeys(String termType) {
        // These key assignments taken from the debian squeeze terminfo database.
        if (termType.equals("xterm")) {
            mKeyCodes[KeyEvent.KEYCODE_NUMPAD_7] = mKeyCodes[KeyEvent.KEYCODE_MOVE_HOME] = "\033OH";
            mKeyCodes[KeyEvent.KEYCODE_NUMPAD_1] = mKeyCodes[KeyEvent.KEYCODE_MOVE_END] = "\033OF";
        } else {
            mKeyCodes[KeyEvent.KEYCODE_NUMPAD_7] = mKeyCodes[KeyEvent.KEYCODE_MOVE_HOME] = "\033[1~";
            mKeyCodes[KeyEvent.KEYCODE_NUMPAD_1] = mKeyCodes[KeyEvent.KEYCODE_MOVE_END] = "\033[4~";
        }
        if (termType.equals("vt100")) {
            mKeyCodes[KeyEvent.KEYCODE_F1] = "\033OP"; // VT100 PF1
            mKeyCodes[KeyEvent.KEYCODE_F2] = "\033OQ"; // VT100 PF2
            mKeyCodes[KeyEvent.KEYCODE_F3] = "\033OR"; // VT100 PF3
            mKeyCodes[KeyEvent.KEYCODE_F4] = "\033OS"; // VT100 PF4
            // the following keys are in the database, but aren't on a real vt100.
            mKeyCodes[KeyEvent.KEYCODE_F5] = "\033Ot";
            mKeyCodes[KeyEvent.KEYCODE_F6] = "\033Ou";
            mKeyCodes[KeyEvent.KEYCODE_F7] = "\033Ov";
            mKeyCodes[KeyEvent.KEYCODE_F8] = "\033Ol";
            mKeyCodes[KeyEvent.KEYCODE_F9] = "\033Ow";
            mKeyCodes[KeyEvent.KEYCODE_F10] = "\033Ox";
            // The following keys are not in database.
            mKeyCodes[KeyEvent.KEYCODE_F11] = "\033[23~";
            mKeyCodes[KeyEvent.KEYCODE_F12] = "\033[24~";
        } else if (termType.startsWith("linux")) {
            mKeyCodes[KeyEvent.KEYCODE_F1] = "\033[[A";
            mKeyCodes[KeyEvent.KEYCODE_F2] = "\033[[B";
            mKeyCodes[KeyEvent.KEYCODE_F3] = "\033[[C";
            mKeyCodes[KeyEvent.KEYCODE_F4] = "\033[[D";
            mKeyCodes[KeyEvent.KEYCODE_F5] = "\033[[E";
            mKeyCodes[KeyEvent.KEYCODE_F6] = "\033[17~";
            mKeyCodes[KeyEvent.KEYCODE_F7] = "\033[18~";
            mKeyCodes[KeyEvent.KEYCODE_F8] = "\033[19~";
            mKeyCodes[KeyEvent.KEYCODE_F9] = "\033[20~";
            mKeyCodes[KeyEvent.KEYCODE_F10] = "\033[21~";
            mKeyCodes[KeyEvent.KEYCODE_F11] = "\033[23~";
            mKeyCodes[KeyEvent.KEYCODE_F12] = "\033[24~";
        } else {
            // default
            // screen, screen-256colors, xterm, anything new
            mKeyCodes[KeyEvent.KEYCODE_F1] = "\033OP"; // VT100 PF1
            mKeyCodes[KeyEvent.KEYCODE_F2] = "\033OQ"; // VT100 PF2
            mKeyCodes[KeyEvent.KEYCODE_F3] = "\033OR"; // VT100 PF3
            mKeyCodes[KeyEvent.KEYCODE_F4] = "\033OS"; // VT100 PF4
            mKeyCodes[KeyEvent.KEYCODE_F5] = "\033[15~";
            mKeyCodes[KeyEvent.KEYCODE_F6] = "\033[17~";
            mKeyCodes[KeyEvent.KEYCODE_F7] = "\033[18~";
            mKeyCodes[KeyEvent.KEYCODE_F8] = "\033[19~";
            mKeyCodes[KeyEvent.KEYCODE_F9] = "\033[20~";
            mKeyCodes[KeyEvent.KEYCODE_F10] = "\033[21~";
            mKeyCodes[KeyEvent.KEYCODE_F11] = "\033[23~";
            mKeyCodes[KeyEvent.KEYCODE_F12] = "\033[24~";
        }
    }

    public int mapControlChar(int ch) {
        return mapControlChar(mHardwareControlKey || mControlKey.isActive(), mFnKey.isActive(), ch);
    }

    public int mapControlChar(boolean control, boolean fn, int ch) {
        int result = ch;
        if (control) {
            // Search is the control key.
            if (result >= 'a' && result <= 'z') {
                result = (char) (result - 'a' + '\001');
            } else if (result >= 'A' && result <= 'Z') {
                result = (char) (result - 'A' + '\001');
            } else if (result == ' ' || result == '2') {
                result = 0;
            } else if (result == '[' || result == '3') {
                result = 27; // ^[ (Esc)
            } else if (result == '\\' || result == '4') {
                result = 28;
            } else if (result == ']' || result == '5') {
                result = 29;
            } else if (result == '^' || result == '6') {
                result = 30; // control-^
            } else if (result == '_' || result == '7') {
                result = 31;
            } else if (result == '8') {
                result = 127; // DEL
            } else if (result == '9') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_F11;
            } else if (result == '0') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_F12;
            }
        } else if (fn) {
            if (result == 'w' || result == 'W') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_DPAD_UP;
            } else if (result == 'a' || result == 'A') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_DPAD_LEFT;
            } else if (result == 's' || result == 'S') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_DPAD_DOWN;
            } else if (result == 'd' || result == 'D') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_DPAD_RIGHT;
            } else if (result == 'p' || result == 'P') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_PAGE_UP;
            } else if (result == 'n' || result == 'N') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_PAGE_DOWN;
            } else if (result == 't' || result == 'T') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_TAB;
            } else if (result == 'l' || result == 'L') {
                result = '|';
            } else if (result == 'u' || result == 'U') {
                result = '_';
            } else if (result == 'e' || result == 'E') {
                result = 27; // ^[ (Esc)
            } else if (result == '.') {
                result = 28; // ^\
            } else if (result > '0' && result <= '9') {
                // F1-F9
                result = (char)(result + KEYCODE_OFFSET + KeyEvent.KEYCODE_F1 - 1);
            } else if (result == '0') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_F10;
            } else if (result == 'i' || result == 'I') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_INSERT;
            } else if (result == 'x' || result == 'X') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_FORWARD_DEL;
            } else if (result == 'h' || result == 'H') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_MOVE_HOME;
            } else if (result == 'f' || result == 'F') {
                result = KEYCODE_OFFSET + KeyEvent.KEYCODE_MOVE_END;
            }
        }

        if (result > -1) {
            mAltKey.adjustAfterKeypress();
            mCapKey.adjustAfterKeypress();
            mControlKey.adjustAfterKeypress();
            mFnKey.adjustAfterKeypress();
            updateCursorMode();
        }

        return result;
    }

    /**
     * Handle a keyDown event.
     *
     * @param keyCode the keycode of the keyDown event
     *
     */
    public void keyDown(int keyCode, KeyEvent event, boolean appMode,
            boolean allowToggle) throws IOException {
        if (LOG_KEYS) {
            Log.i(TAG, "keyDown(" + keyCode + "," + event + "," + appMode + "," + allowToggle + ")");
        }
        if (handleKeyCode(keyCode, event, appMode)) {
            return;
        }
        int result = -1;
        boolean chordedCtrl = false;
        boolean setHighBit = false;
        switch (keyCode) {
        case KeyEvent.KEYCODE_ALT_RIGHT:
        case KeyEvent.KEYCODE_ALT_LEFT:
            if (allowToggle) {
                mAltKey.onPress();
                updateCursorMode();
            }
            break;

        case KeyEvent.KEYCODE_SHIFT_LEFT:
        case KeyEvent.KEYCODE_SHIFT_RIGHT:
            if (allowToggle) {
                mCapKey.onPress();
                updateCursorMode();
            }
            break;

        case KeyEvent.KEYCODE_CTRL_LEFT:
        case KeyEvent.KEYCODE_CTRL_RIGHT:
            // Ignore the control key.
            return;

        case KeyEvent.KEYCODE_CAPS_LOCK:
            // Ignore the capslock key.
            return;

        case KeyEvent.KEYCODE_FUNCTION:
            // Ignore the function key.
            return;

        case KeyEvent.KEYCODE_BACK:
            result = mBackKeyCode;
            break;

        default: {
            int metaState = event.getMetaState();
            chordedCtrl = ((KeyEvent.META_CTRL_ON & metaState) != 0);
            boolean effectiveCaps = allowToggle &&
                    (mCapKey.isActive());
            boolean effectiveAlt = allowToggle && mAltKey.isActive();
            int effectiveMetaState = metaState & (~KeyEvent.META_CTRL_MASK);
            if (effectiveCaps) {
                effectiveMetaState |= KeyEvent.META_SHIFT_ON;
            }
            if (!allowToggle && (effectiveMetaState & KeyEvent.META_ALT_ON) != 0) {
                effectiveAlt = true;
            }
            if (effectiveAlt) {
                if (mAltSendsEsc) {
                    mTermSession.write(new byte[]{0x1b},0,1);
                    effectiveMetaState &= ~KeyEvent.META_ALT_MASK;
                } else if (SUPPORT_8_BIT_META) {
                    setHighBit = true;
                    effectiveMetaState &= ~KeyEvent.META_ALT_MASK;
                } else {
                    // Legacy behavior: Pass Alt through to allow composing characters.
                    effectiveMetaState |= KeyEvent.META_ALT_ON;
                }
            }

            // Note: The Hacker keyboard IME key labeled Alt actually sends Meta.


            if ((metaState & KeyEvent.META_META_ON) != 0) {
                if (mAltSendsEsc) {
                    mTermSession.write(new byte[]{0x1b},0,1);
                    effectiveMetaState &= ~KeyEvent.META_META_MASK;
                } else {
                    if (SUPPORT_8_BIT_META) {
                        setHighBit = true;
                        effectiveMetaState &= ~KeyEvent.META_META_MASK;
                    }
                }
            }
            result = event.getUnicodeChar(effectiveMetaState);

            if ((result & KeyCharacterMap.COMBINING_ACCENT) != 0) {
                if (LOG_COMBINING_ACCENT) {
                    Log.i(TAG, "Got combining accent " + result);
                }
                mCombiningAccent = result & KeyCharacterMap.COMBINING_ACCENT_MASK;
                return;
            }
            if (mCombiningAccent != 0) {
                int unaccentedChar = result;
                result = KeyCharacterMap.getDeadChar(mCombiningAccent, unaccentedChar);
                if (LOG_COMBINING_ACCENT) {
                    Log.i(TAG, "getDeadChar(" + mCombiningAccent + ", " + unaccentedChar + ") -> " + result);
                }
                mCombiningAccent = 0;
            }

            break;
            }
        }

        boolean effectiveControl = chordedCtrl || mHardwareControlKey || (allowToggle && mControlKey.isActive());
        boolean effectiveFn = allowToggle && mFnKey.isActive();

        result = mapControlChar(effectiveControl, effectiveFn, result);

        if (result >= KEYCODE_OFFSET) {
            handleKeyCode(result - KEYCODE_OFFSET, null, appMode);
        } else if (result >= 0) {
            if (setHighBit) {
                result |= 0x80;
            }
            mTermSession.write(result);
        }
    }

    public int getCombiningAccent() {
        return mCombiningAccent;
    }

    public int getCursorMode() {
        return mCursorMode;
    }

    private void updateCursorMode() {
        mCursorMode = getCursorModeHelper(mCapKey, TextRenderer.MODE_SHIFT_SHIFT)
                | getCursorModeHelper(mAltKey, TextRenderer.MODE_ALT_SHIFT)
                | getCursorModeHelper(mControlKey, TextRenderer.MODE_CTRL_SHIFT)
                | getCursorModeHelper(mFnKey, TextRenderer.MODE_FN_SHIFT);
    }

    private static int getCursorModeHelper(ModifierKey key, int shift) {
        return key.getUIMode() << shift;
    }

    static boolean isEventFromToggleDevice(KeyEvent event) {
        return KeyCharacterMap.load(event.getDeviceId()).getModifierBehavior() ==
                KeyCharacterMap.MODIFIER_BEHAVIOR_CHORDED_OR_TOGGLED;
    }

    public boolean handleKeyCode(int keyCode, KeyEvent event, boolean appMode) throws IOException {
        String code = null;
        if (event != null) {
            int keyMod = 0;
            // META_CTRL_ON was added only in API 11, so don't use it,
            // use our own tracking of Ctrl key instead.
            // (event.getMetaState() & META_CTRL_ON) != 0
            if (mHardwareControlKey || mControlKey.isActive()) {
                keyMod |= KEYMOD_CTRL;
            }
            if ((event.getMetaState() & KeyEvent.META_ALT_ON) != 0) {
                keyMod |= KEYMOD_ALT;
            }
            if ((event.getMetaState() & KeyEvent.META_SHIFT_ON) != 0) {
                keyMod |= KEYMOD_SHIFT;
            }
            // First try to map scancode
            code = mKeyMap.get(event.getScanCode() | KEYMOD_SCAN | keyMod);
            if (code == null) {
                code = mKeyMap.get(keyCode | keyMod);
            }
        }

        if (code == null && keyCode >= 0 && keyCode < mKeyCodes.length) {
            if (appMode) {
                code = mAppKeyCodes[keyCode];
            }
            if (code == null) {
                code = mKeyCodes[keyCode];
            }
        }

        if (code != null) {
            if (EmulatorDebug.LOG_CHARACTERS_FLAG) {
                byte[] bytes = code.getBytes();
                Log.d(EmulatorDebug.LOG_TAG, "Out: '" + EmulatorDebug.bytesToString(bytes, 0, bytes.length) + "'");
            }
            mTermSession.write(code);
            return true;
        }
        return false;
    }

    /**
     * Handle a keyUp event.
     *
     * @param keyCode the keyCode of the keyUp event
     */
    public void keyUp(int keyCode, KeyEvent event) {
        boolean allowToggle = isEventFromToggleDevice(event);
        switch (keyCode) {
        case KeyEvent.KEYCODE_ALT_LEFT:
        case KeyEvent.KEYCODE_ALT_RIGHT:
            if (allowToggle) {
                mAltKey.onRelease();
                updateCursorMode();
            }
            break;
        case KeyEvent.KEYCODE_SHIFT_LEFT:
        case KeyEvent.KEYCODE_SHIFT_RIGHT:
            if (allowToggle) {
                mCapKey.onRelease();
                updateCursorMode();
            }
            break;

        case KeyEvent.KEYCODE_CTRL_LEFT:
        case KeyEvent.KEYCODE_CTRL_RIGHT:
            // ignore control keys.
            break;

        default:
            // Ignore other keyUps
            break;
        }
    }

    public boolean isCtrlActive() {
        return mControlKey.isActive();
    }
}
