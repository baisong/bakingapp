package com.example.android.bakingapp;

import android.os.RemoteException;
import android.os.SystemClock;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import com.example.android.bakingapp.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by oren on 8/26/17.
 */

public class MainDetailNavigationTest {
    @Rule
    public ActivityTestRule<MainActivity> mMainRule = new ActivityTestRule<>(MainActivity.class);

    public UiDevice mDevice;
    public UiScrollable mScrollable;

    public void rotateLeft() {
        try {
            mDevice.setOrientationLeft();
            SystemClock.sleep(500);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void rotateNatural() {
        try {
            mDevice.setOrientationNatural();
            SystemClock.sleep(500);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rotateDetailActivity_preservesCurrentRecipeStep() {
        setupUiObjects();
        rotateNatural();
        onData(anything()).inAdapterView(withId(R.id.gv_select_recipe)).atPosition(0).perform(click());
        try {
            mScrollable.scrollForward();
            onView(withId(R.id.rv_steps)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(withId(R.id.tv_current_step)).check(matches(withText("1")));
            onView(withId(R.id.btn_next_step)).perform(click());
            onView(withId(R.id.tv_current_step)).check(matches(withText("2")));
            onView(withId(R.id.btn_next_step)).perform(click());
            onView(withId(R.id.tv_current_step)).check(matches(withText("3")));
            rotateLeft();
            onView(withId(R.id.tv_current_step)).check(matches(withText("3")));
            rotateNatural();

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void navigateViaToolbarAndButtons_rotatePreservesCurrentRecipeStep() {
        setupUiObjects();
        rotateNatural();
        onData(anything()).inAdapterView(withId(R.id.gv_select_recipe)).atPosition(3).perform(click());
        try {
            mScrollable.scrollForward();
            mScrollable.scrollForward();
            onView(withId(R.id.rv_steps)).perform(RecyclerViewActions.actionOnItemAtPosition(12, click()));
            onView(withId(R.id.tv_current_step)).check(matches(withText("13")));
            onView(withId(R.id.btn_prev_step)).perform(click(), click(), click(), click());
            onView(withId(R.id.tv_current_step)).check(matches(withText("9")));
            rotateLeft();
            onView(withId(R.id.tv_current_step)).check(matches(withText("9")));

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility setup function to allow scrolling on NestedScrollView views.
     *
     * @return
     */
    public void setupUiObjects() {
        mDevice = UiDevice.getInstance(getInstrumentation());
        mScrollable = new UiScrollable(new UiSelector().scrollable(true));
    }
}

