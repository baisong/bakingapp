package com.example.android.bakingapp;

import android.os.RemoteException;
import android.os.SystemClock;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import com.example.android.bakingapp.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Instrumentation test, which will execute two UI tests on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityBasicTest {
    @Rule
    public ActivityTestRule<MainActivity> mMainRule = new ActivityTestRule<>(MainActivity.class);

    public UiDevice mDevice;
    public UiScrollable mScrollable;

    /**
     * Launch the app's main activity, and verify that clicking a step launches the detail activity.
     */
    @Test
    public void clickOnStep_launchesStepActivity() {
        setupUiObjects();
        rotateNatural();
        onData(anything()).inAdapterView(withId(R.id.gv_select_recipe)).atPosition(0).perform(click());
        try {
            mScrollable.scrollForward();
            onView(withId(R.id.rv_steps)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(withId(R.id.nsv_detail_fragment_wrapper)).check(exists());

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launch the detail activity, check we are on step 1, then navigate forward and backward.
     */
    @Test
    public void clickOnNextButton_launchesNextStep() {
        setupUiObjects();
        rotateNatural();
        onData(anything()).inAdapterView(withId(R.id.gv_select_recipe)).atPosition(0).perform(click());
        try {
            mScrollable.scrollForward();
            onView(withId(R.id.rv_steps)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(withId(R.id.tv_current_step)).check(matches(withText("1")));
            onView(withId(R.id.btn_next_step)).perform(click());
            SystemClock.sleep(500);
            onView(withId(R.id.tv_current_step)).check(matches(withText("2")));
            onView(withId(R.id.btn_prev_step)).perform(click());
            onView(withId(R.id.tv_current_step)).check(matches(withText("1")));

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rotateMainActivity_detailFragmentDisplaysFirstStep() {
        setupUiObjects();
        rotateNatural();
        onData(anything()).inAdapterView(withId(R.id.gv_select_recipe)).atPosition(0).perform(click());
        rotateLeft();
        onView(withId(R.id.tv_current_step)).check(matches(withText("1")));
    }

    /**
     * Fails if the viewMatcher doesn't match anything. (Syntactic sugar)
     *
     * @return
     */
    public static ViewAssertion exists() {
        return matches(anything());
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

    public void rotateNatural() {
        try {
            mDevice.setOrientationNatural();
            SystemClock.sleep(500);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void rotateLeft() {
        try {
            mDevice.setOrientationLeft();
            SystemClock.sleep(500);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
