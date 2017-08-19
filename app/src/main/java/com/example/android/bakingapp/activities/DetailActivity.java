package com.example.android.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.State;
import com.example.android.bakingapp.fragments.DetailFragment;
import com.example.android.bakingapp.tools.RecipeRecordCollection;

import static com.example.android.bakingapp.data.State.CURRENT_RECIPE_INDEX;
import static com.example.android.bakingapp.data.State.CURRENT_STEP_INDEX;
import static com.example.android.bakingapp.data.State.IS_TWO_PANE;
import static com.example.android.bakingapp.data.State.RECIPE_DATA;

public class DetailActivity extends AppCompatActivity {

    private int mCurrentRecipe;
    private int mCurrentStep;
    private boolean mTwoPane;
    private RecipeRecordCollection mRecipeData;

    private static final String LOG_TAG = "BakingApp [DET]{Acty}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate");
        super.onCreate(savedInstanceState);
        launchMainIfWidescreen();
        setContentView(R.layout.activity_recipe);
        if (savedInstanceState == null) {
            loadDataFromExplicitIntent();
        }
        else {
            loadDataFromInstanceState(savedInstanceState);
        }
        addDetailFragment();
    }

    private void launchMainIfWidescreen() {
        if (getWidthInDp() >= 600) {
            final Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(State.CURRENT_RECIPE_INDEX, mCurrentRecipe);
            intent.putExtra(State.CURRENT_STEP_INDEX, mCurrentStep);
            Bundle bundle = new Bundle();
            bundle.putSerializable(State.RECIPE_DATA, mRecipeData);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private float getWidthInDp() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        return outMetrics.widthPixels / getResources().getDisplayMetrics().density;
    }

    private void loadDataFromExplicitIntent() {
        log("data from explicit intent");
        mCurrentRecipe = getIntent().getIntExtra(CURRENT_RECIPE_INDEX, 0);
        mCurrentStep = getIntent().getIntExtra(CURRENT_STEP_INDEX, 0);
        mRecipeData = (RecipeRecordCollection) getIntent().getSerializableExtra(RECIPE_DATA);
        log("NEW >>>"
                + " twoPane: " + String.valueOf(mTwoPane)
                + "; Step: " + String.valueOf(mCurrentRecipe)
                + "," + String.valueOf(mCurrentStep)
                + "; Data: " + quickLogData());
    }

    private void loadDataFromInstanceState(Bundle instanceState) {
        log("data from instance state");
        mCurrentRecipe = instanceState.getInt(CURRENT_RECIPE_INDEX, 0);
        mCurrentStep = instanceState.getInt(CURRENT_STEP_INDEX, 0);
        mRecipeData = (RecipeRecordCollection) instanceState.getSerializable(RECIPE_DATA);
        mTwoPane = instanceState.getBoolean(IS_TWO_PANE);
        log("NEW >>>"
                + " twoPane: " + String.valueOf(mTwoPane)
                + "; Step: " + String.valueOf(mCurrentRecipe)
                + "," + String.valueOf(mCurrentStep)
                + "; Data: " + quickLogData());
    }

    private void addDetailFragment() {
        DetailFragment fragment = new DetailFragment();
        fragment.setRecipeData(mRecipeData);
        fragment.setCurrentStep(mCurrentRecipe, mCurrentStep);
        fragment.refreshSteps();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.recipe_container, fragment)
                .commit();
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(RECIPE_DATA)) {
            mRecipeData = (RecipeRecordCollection) savedInstanceState.getSerializable(RECIPE_DATA);
            mCurrentRecipe = savedInstanceState.getInt(CURRENT_RECIPE_INDEX);
            mCurrentStep = savedInstanceState.getInt(CURRENT_STEP_INDEX);
            mTwoPane = savedInstanceState.getBoolean(IS_TWO_PANE);
        }
        log("RESTORE >>>"
                + " twoPane: " + String.valueOf(mTwoPane)
                + "; Step: " + String.valueOf(mCurrentRecipe)
                + "," + String.valueOf(mCurrentStep)
                + "; Data: " + quickLogData());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_TWO_PANE, mTwoPane);
        outState.putInt(CURRENT_RECIPE_INDEX, mCurrentRecipe);
        outState.putInt(CURRENT_STEP_INDEX, mCurrentStep);
        outState.putSerializable(RECIPE_DATA, mRecipeData);
        log("OUT >>>"
                + " twoPane: " + String.valueOf(mTwoPane)
                + "; Step: " + String.valueOf(mCurrentRecipe)
                + "," + String.valueOf(mCurrentStep)
                + "; Data: " + quickLogData());
        super.onSaveInstanceState(outState);
    }

    private void log(String message) {
        Log.d(LOG_TAG, message);
    }

    private String quickLogData() {
        if (mRecipeData == null) {
            return "0";
        }
        return String.valueOf(mRecipeData.getCount());
    }
}
