/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yhh.terminal;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yhh.terminal.emulatorview.ColorScheme;
import com.yhh.terminal.emulatorview.TermSession;
import com.yhh.terminal.util.TermSettings;
import com.yhh.utils.ConstUtils;

/**
 * A terminal session, consisting of a TerminalEmulator, a TranscriptScreen,
 * the PID of the process attached to the session, and the I/O streams used to
 * talk to the process.
 */
public class ShellTermSession extends TermSession {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "ShellTermSession";
    //** Set to true to force into 80 x 24 for testing with vttest. */
    private static final boolean VTTEST_MODE = false;
    private TermSettings mSettings;

    private int mProcId;
    private FileDescriptor mTermFd;
    private Thread mWatcherThread;

    // A cookie which uniquely identifies this session.
    private String mHandle;

    public static final String SHELL_PATH = "/system/bin/sh -";
    public static final String TERM_TYPE = "screen";
    
    public static final int PROCESS_EXIT_FINISHES_SESSION = 0;
    public static final int PROCESS_EXIT_DISPLAYS_MESSAGE = 1;

    private String mProcessExitMessage;

    private static final int PROCESS_EXITED = 1;

    private Handler mMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!isRunning()) {
                return;
            }
            if (msg.what == PROCESS_EXITED) {
                onProcessExit((Integer) msg.obj);
            }
        }
    };

    public ShellTermSession(TermSettings settings, String initialCommand) {
        super();

        updatePrefs(settings);

        initializeSession();

        mWatcherThread = new Thread() {
             @Override
             public void run() {
                Log.i(TAG, "waiting for: " + mProcId);
                int result = Exec.waitFor(mProcId);
                Log.i(TAG, "Subprocess exited: " + result);
                mMsgHandler.sendMessage(mMsgHandler.obtainMessage(PROCESS_EXITED, result));
             }
        };
        mWatcherThread.setName("Process watcher");
    }

    public void updatePrefs(TermSettings settings) {
        mSettings = settings;
        setColorScheme(new ColorScheme(settings.getColorScheme()));
    }

    private void initializeSession() {
        TermSettings settings = mSettings;

        int[] processId = new int[1];

        String path = System.getenv("PATH");
        String[] env = new String[3];
        env[0] = "TERM=" + TERM_TYPE;
        env[1] = "PATH=" + path;
        env[2] = "HOME=" + settings.getHomePath();
        
        createSubprocess(processId, SHELL_PATH , env);
        mProcId = processId[0];

        setTermOut(new FileOutputStream(mTermFd));
        setTermIn(new FileInputStream(mTermFd));
    }

    @Override
    public void initializeEmulator(int columns, int rows) {
        if (VTTEST_MODE) {
            columns = 80;
            rows = 24;
        }
        super.initializeEmulator(columns, rows);

        Exec.setPtyUTF8Mode(mTermFd, true);

        mWatcherThread.start();
    }

    private void createSubprocess(int[] processId, String shell, String[] env) {
        ArrayList<String> argList = parse(shell);
        String arg0;
        String[] args;
        
        try {
            arg0 = argList.get(0);
            File file = new File(arg0);
            if (!file.exists()) {
                Log.e(TAG, "Shell " + arg0 + " not found!");
                throw new FileNotFoundException(arg0);
            } 
            args = argList.toArray(new String[1]);
        } catch (Exception e) {
            argList = parse(SHELL_PATH);
            arg0 = argList.get(0);
            args = argList.toArray(new String[1]);
        }

        mTermFd = Exec.createSubprocess(arg0, args, env, processId);
    }

    private ArrayList<String> parse(String cmd) {
        final int PLAIN = 0;
        final int WHITESPACE = 1;
        final int INQUOTE = 2;
        int state = WHITESPACE;
        ArrayList<String> result =  new ArrayList<String>();
        int cmdLen = cmd.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cmdLen; i++) {
            char c = cmd.charAt(i);
            if (state == PLAIN) {
                if (Character.isWhitespace(c)) {
                    result.add(builder.toString());
                    builder.delete(0,builder.length());
                    state = WHITESPACE;
                } else if (c == '"') {
                    state = INQUOTE;
                } else {
                    builder.append(c);
                }
            } else if (state == WHITESPACE) {
                if (Character.isWhitespace(c)) {
                    // do nothing
                } else if (c == '"') {
                    state = INQUOTE;
                } else {
                    state = PLAIN;
                    builder.append(c);
                }
            } else if (state == INQUOTE) {
                if (c == '\\') {
                    if (i + 1 < cmdLen) {
                        i += 1;
                        builder.append(cmd.charAt(i));
                    }
                } else if (c == '"') {
                    state = PLAIN;
                } else {
                    builder.append(c);
                }
            }
        }
        if (builder.length() > 0) {
            result.add(builder.toString());
        }
        return result;
    }

    @Override
    public void updateSize(int columns, int rows) {
        if (VTTEST_MODE) {
            columns = 80;
            rows = 24;
        }
        // Inform the attached pty of our new size:
        Exec.setPtyWindowSize(mTermFd, rows, columns, 0, 0);
        super.updateSize(columns, rows);
    }

    /* XXX We should really get this ourselves from the resource bundle, but
       we cannot hold a context */
    public void setProcessExitMessage(String message) {
        mProcessExitMessage = message;
    }

    private void onProcessExit(int result) {
        if (mProcessExitMessage != null) {
            try {
                byte[] msg = ("\r\n[" + mProcessExitMessage + "]").getBytes("UTF-8");
                appendToEmulator(msg, 0, msg.length);
                notifyUpdate();
            } catch (UnsupportedEncodingException e) {
                // Never happens
            }
        }
    }

    @Override
    public void finish() {
        Exec.hangupProcessGroup(mProcId);
        Exec.close(mTermFd);
        super.finish();
    }

    /**
     * Gets the terminal session's title.  Unlike the superclass's getTitle(),
     * if the title is null or an empty string, the provided default title will
     * be returned instead.
     *
     * @param defaultTitle The default title to use if this session's title is
     *     unset or an empty string.
     */
    public String getTitle(String defaultTitle) {
        String title = super.getTitle();
        if (title != null && title.length() > 0) {
            return title;
        } else {
            return defaultTitle;
        }
    }

    public void setHandle(String handle) {
        if (mHandle != null) {
            throw new IllegalStateException("Cannot change handle once set");
        }
        mHandle = handle;
    }

    public String getHandle() {
        return mHandle;
    }
}
