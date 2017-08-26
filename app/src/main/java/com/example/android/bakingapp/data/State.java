package com.example.android.bakingapp.data;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * A wrapper to managing application state as a SharedPreferences instance.
 *
 * Adapted from: https://gist.github.com/alphamu/8748537a3b73d9c58b4c
 */
public class State {
    private static final String SETTINGS_NAME = "bakingapp_state";
    private static State sSharedPrefs;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private boolean mBulkUpdate = false;

    public final static String CURRENT_RECIPE_INDEX = "recipe";
    public final static String CURRENT_STEP_INDEX = "step";
    public final static String RECIPE_DATA = "data";
    public final static String IS_TWO_PANE = "twoPane";
    public final static String LAUNCHED_FROM_DETAIL = "launched_from_detail";

    public enum Key {
        ACTIVE_RECIPE_INT,
        ACTIVE_STEP_INT,
        IS_PLAYING
    }

    private State(Context context) {
        mPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }

    public static State getInstance(Context context) {
        if (sSharedPrefs == null) {
            sSharedPrefs = new State(context.getApplicationContext());
        }
        return sSharedPrefs;
    }

    public static State getInstance() {
        if (sSharedPrefs != null) {
            return sSharedPrefs;
        }
        throw new IllegalArgumentException("Should use getInstance(Context) at least once before using this method.");
    }

    public void put(Key key, int val) {
        doEdit();
        mEditor.putInt(key.name(), val);
        doCommit();
    }

    public void put(Key key, boolean val) {
        doEdit();
        mEditor.putBoolean(key.name(), val);
        doCommit();
    }

    public int getInt(Key key) {
        return mPref.getInt(key.name(), 0);
    }

    public boolean getBoolean(Key key) {
        return mPref.getBoolean(key.name(), false);
    }

    private void doEdit() {
        if (!mBulkUpdate && mEditor == null) {
            mEditor = mPref.edit();
        }
    }

    private void doCommit() {
        if (!mBulkUpdate && mEditor != null) {
            mEditor.commit();
            mEditor = null;
        }
    }
}