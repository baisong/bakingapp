package com.example.android.bakingapp;

import android.graphics.Rect;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.ScrollToAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.example.android.bakingapp.activities.MainActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.AnyOf.anyOf;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityBasicTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickOnStep_launchesStepActivity() {
        UiDevice.getInstance(getInstrumentation());
        UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));
        try {
            appViews.scrollForward();
            onView(withId(R.id.tv_steps_label)).perform(new CustomScrollAction());
            onView(withId(R.id.rv_steps)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(withId(R.id.nsv_detail_fragment_wrapper)).check(exists());

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        //assertCurrentActivityIsInstanceOf(DetailActivity.class);
    }

    @Test
    public void clickOnNextButton_launchesNextStep() {
        UiDevice.getInstance(getInstrumentation());
        UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));
        try {
            appViews.scrollForward();
            onView(withId(R.id.tv_steps_label)).perform(new CustomScrollAction());
            onView(withId(R.id.rv_steps)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(withId(R.id.tv_current_step)).check(matches(withText("1")));
            onView(withId(R.id.btn_next_step)).perform(click());
            onView(withId(R.id.tv_current_step)).check(matches(withText("2")));

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ViewAssertion exists() {
        return matches(anything());
    }

    public final class CustomScrollAction implements android.support.test.espresso.ViewAction {
        private final String TAG = ScrollToAction.class.getSimpleName();
        @SuppressWarnings("unchecked")
        @Override
        public Matcher<View> getConstraints() {
            return allOf(
                    withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                    isDescendantOfA(anyOf(
                        isAssignableFrom(ScrollView.class),
                        isAssignableFrom(HorizontalScrollView.class),
                        isAssignableFrom(NestedScrollView.class),
                        isAssignableFrom(ListView.class)
            )));
        }
        @Override
        public void perform(UiController uiController, View view) {
            if (isDisplayingAtLeast(90).matches(view)) {
                Log.i(TAG, "View is already displayed. Returning.");
                return;
            }
            Rect rect = new Rect();
            view.getDrawingRect(rect);
            if (!view.requestRectangleOnScreen(rect, true /* immediate */)) {
                Log.w(TAG, "Scrolling to view was requested, but none of the parents scrolled.");
            }
            uiController.loopMainThreadUntilIdle();
            if (!isDisplayingAtLeast(90).matches(view)) {
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new RuntimeException(
                                "Scrolling to view was attempted, but the view is not displayed"))
                        .build();
            }
        }
        @Override
        public String getDescription() {
            return "scroll to";
        }
    }
}
