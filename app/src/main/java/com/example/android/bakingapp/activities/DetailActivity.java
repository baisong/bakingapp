package com.example.android.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.data.State;
import com.example.android.bakingapp.fragments.DetailFragment;

import static com.example.android.bakingapp.data.State.CURRENT_RECIPE_INDEX;
import static com.example.android.bakingapp.data.State.CURRENT_STEP_INDEX;
import static com.example.android.bakingapp.data.State.IS_TWO_PANE;
import static com.example.android.bakingapp.data.State.RECIPE_DATA;

/**
 * Displays the current step in either full screen, or as right-hand panel on larger displays.
 * <p>
 * Launched only from MainActivity, either via explicit intent on smaller displays, or via fragment
 * manager transaction on larger displays.
 */
public class DetailActivity extends AppCompatActivity {

    private static final float WIDESCREEN_MIN_WIDTH_IN_DP = 600;
    private int mCurrentRecipe;
    private int mCurrentStep;
    private boolean mTwoPane;
    private RecipeData mRecipeData;

    /**
     * Load data into member variables and replace the detail fragment with data received.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchMainIfWidescreen();
        setContentView(R.layout.activity_recipe);
        if (savedInstanceState == null) {
            loadDataFromExplicitIntent();
        } else {
            loadDataFromInstanceState(savedInstanceState);
        }
        addDetailFragment();
    }

    /**
     * Persist data after screen rotate, etc.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(RECIPE_DATA)) {
            mRecipeData = (RecipeData) savedInstanceState.getSerializable(RECIPE_DATA);
            mCurrentRecipe = savedInstanceState.getInt(CURRENT_RECIPE_INDEX);
            mCurrentStep = savedInstanceState.getInt(CURRENT_STEP_INDEX);
            mTwoPane = savedInstanceState.getBoolean(IS_TWO_PANE);
        }
    }

    /**
     * Store data in advance of screen rotate, etc.
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_TWO_PANE, mTwoPane);
        outState.putInt(CURRENT_RECIPE_INDEX, mCurrentRecipe);
        outState.putInt(CURRENT_STEP_INDEX, mCurrentStep);
        outState.putSerializable(RECIPE_DATA, mRecipeData);
        super.onSaveInstanceState(outState);
    }

    /**
     * Launch MainActivity with side panel DetailActivity if device is widescreen.
     */
    private void launchMainIfWidescreen() {
        if (getWidthInDp() < WIDESCREEN_MIN_WIDTH_IN_DP) {
            return;
        }
        final Intent intent = new Intent(this, MainActivity.class);
        State.getInstance(getApplicationContext()).put(State.Key.ACTIVE_RECIPE_INT, mCurrentRecipe);
        State.getInstance().put(State.Key.ACTIVE_STEP_INT, mCurrentStep);
        Bundle bundle = new Bundle();
        bundle.putSerializable(State.RECIPE_DATA, mRecipeData);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Utility function to determine device display metrics.
     *
     * @return
     */
    private float getWidthInDp() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        return outMetrics.widthPixels / getResources().getDisplayMetrics().density;
    }

    /**
     * Utility function to assign private member variables from intent.
     */
    private void loadDataFromExplicitIntent() {
        mCurrentRecipe = getIntent().getIntExtra(CURRENT_RECIPE_INDEX, 0);
        mCurrentStep = getIntent().getIntExtra(CURRENT_STEP_INDEX, 0);
        mRecipeData = (RecipeData) getIntent().getSerializableExtra(RECIPE_DATA);
    }

    /**
     * Utility function to assign private member variables from instance state bundle.
     */
    private void loadDataFromInstanceState(Bundle instanceState) {
        mCurrentRecipe = instanceState.getInt(CURRENT_RECIPE_INDEX, 0);
        mCurrentStep = instanceState.getInt(CURRENT_STEP_INDEX, 0);
        mRecipeData = (RecipeData) instanceState.getSerializable(RECIPE_DATA);
        mTwoPane = instanceState.getBoolean(IS_TWO_PANE);
    }

    /**
     * Replace the detail fragment with up-to-date recipe data.
     */
    private void addDetailFragment() {
        int container = R.id.detail_fragment_container;
        DetailFragment f = DetailFragment.newInstance(mRecipeData, mCurrentRecipe, mCurrentStep);
        getSupportFragmentManager().beginTransaction().replace(container, f).commit();
    }

}
