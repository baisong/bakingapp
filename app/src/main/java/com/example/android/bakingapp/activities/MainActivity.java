package com.example.android.bakingapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.data.State;
import com.example.android.bakingapp.fragments.DetailFragment;
import com.example.android.bakingapp.fragments.MainFragment;
import com.example.android.bakingapp.tools.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainFragment.OnStepClickListener {

    private boolean mTwoPane;
    private MainFragment mMainFragment;
    private DetailFragment mDetailFragment;
    private RecipeData mRecipeData;
    private int mCurrentRecipe;
    private int mCurrentStep;
    private final static String DETAIL_FRAGMENT_TAG = "DetailFrag";

    @BindView(R.id.pb_loading_data)
    ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Bundle extras = getIntent().getExtras();
        boolean justLaunchedApp = ((savedInstanceState == null) && (extras == null));
        if (justLaunchedApp) {
            setCurrentRecipeStep(0, 0);
        }
        else {
            // Restore fetched recipe data if available.
            getCurrentRecipeStep();
        }

        if (savedInstanceState != null) {
            if(savedInstanceState.containsKey(State.RECIPE_DATA)) {
                mRecipeData = (RecipeData) savedInstanceState.getSerializable(State.RECIPE_DATA);
            }
        }
        // Register first recipe & step and that ExoPlayer is not playing if app just launched.
        else {
            State.getInstance(getApplicationContext()).put(State.Key.IS_PLAYING, false);
            State.getInstance().put(State.Key.ACTIVE_RECIPE_INT, 0);
            State.getInstance().put(State.Key.ACTIVE_STEP_INT, 0);
        }

        // Fetch current recipe and step if device just rotated from step (detail) screen.
        if (extras != null) {
            if (extras.getBoolean(State.LAUNCHED_FROM_DETAIL)) {
                mCurrentRecipe = extras.getInt(State.CURRENT_RECIPE_INDEX);
                mCurrentStep = extras.getInt(State.CURRENT_STEP_INDEX);
            }
        }

        // Detect two pane and release ExoPlayer if detail exists to prevent duplicate session.
        mTwoPane = false;
        if (findViewById(R.id.ll_recipe_wrapper) != null) {
            mTwoPane = true;
            if (mDetailFragment != null) mDetailFragment.releasePlayer("From Main");
        }

        if (dataLoaded()) {
            updateMainFragment();
            if (mTwoPane) {
                //Log.d("BakingApp [MAI]{Acty}","[  UBUBU  ] Add detail fragment from onCreate");
                //addDetailFragment();
            }
        }
        else {
            new FetchRecipesTask().execute();
        }
    }

    /**
     * Restore fetched recipe data if available, update current recipe, step, and twoPane values.
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(State.RECIPE_DATA)) {
            mRecipeData = (RecipeData) savedInstanceState.getSerializable(State.RECIPE_DATA);
        }
        getCurrentRecipeStep();
        if (dataLoaded() && mDetailFragment != null) {
            mDetailFragment.setRecipeData(mRecipeData);
            mDetailFragment.setCurrentStep(mCurrentRecipe, mCurrentStep);
        }
        mTwoPane = false;
        if (findViewById(R.id.ll_recipe_wrapper) != null) {
            mTwoPane = true;

            if (!savedInstanceState.getBoolean(State.IS_TWO_PANE) && !State.getInstance(getApplicationContext()).getBoolean(State.Key.IS_PLAYING)) {
                Log.d("BakingApp [MAI]{Acty}","[  UBUBU  ] Add detail fragment from onRestoreInstanceState");
                //addDetailFragment();
            }
        }
    }

    /**
     * Store current data, recipe, step and twoPane values.
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        setCurrentRecipeStep(mCurrentRecipe, mCurrentStep);
        outState.putBoolean(State.IS_TWO_PANE, mTwoPane);
        outState.putInt(State.CURRENT_RECIPE_INDEX, mCurrentRecipe);
        outState.putInt(State.CURRENT_STEP_INDEX, mCurrentStep);
        outState.putSerializable(State.RECIPE_DATA, mRecipeData);
        super.onSaveInstanceState(outState);
    }

    /**
     * Add recipe names to ToolBar options menu.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mRecipeData != null && mRecipeData.getCount() > 0) {
            List<String> names = mRecipeData.getRecipeNames();
            for (int i = 0; i < names.size(); i++) {
                menu.add(0, i, Menu.NONE, names.get(i));
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Handle Toolbar options menu click actions; load the selected recipe into main fragment.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id >= 0 && id < 4) {
            setCurrentRecipeStep(id);
            //addMainFragment();
            updateMainFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Restore current recipe and step from SharedPreferences, default: first step of first recipe.
     */
    private void getCurrentRecipeStep() {
        mCurrentRecipe = 0;
        mCurrentStep = 0;
        try {
            mCurrentRecipe = State.getInstance(getApplicationContext()).getInt(State.Key.ACTIVE_RECIPE_INT);
            mCurrentStep = State.getInstance().getInt(State.Key.ACTIVE_STEP_INT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility function to treat null data as 0.
     * @return
     */
    private boolean dataLoaded() {
        return mRecipeData != null && mRecipeData.getCount() > 0;
    }

    /**
     * Find the Main Fragment by ID, set current data and recipe values, and update view.
     */
    public void updateMainFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment f = fragmentManager.findFragmentById(R.id.main_list_fragment);
        if (f == null) {
            throw new UnsupportedOperationException("Unable to load Main List fragment.");
        }
        if (!(f instanceof MainFragment) || !dataLoaded()) {
            return;
        }
        getCurrentRecipeStep();
        mMainFragment = (MainFragment) f;
        mMainFragment.setRecipeData(mRecipeData);
        mMainFragment.setCurrentRecipe(mCurrentRecipe);
        mMainFragment.loadCurrentRecipe();

        if (mTwoPane) {
            Log.d("BakingApp [MAI]{Acty}","[  UBUBU  ] Add detail fragment from updateMainFragment");
            debug("updateMainFragment addDetailFrag");
            addDetailFragment();
        }
    }

    private static final String LOG_TAG = "BakingApp [MAI]{Acty}";
    private void log(String message) {
        Log.d(LOG_TAG, message);
    }

    private void debug(String note) {
        log(note + " twoPane: " + String.valueOf(mTwoPane)
                + "; Step: " + String.valueOf(mCurrentRecipe)
                + "," + String.valueOf(mCurrentStep)
                + "; Data: " + (dataLoaded() ? mRecipeData.getCount() : "null"));
    }


    /**
     * Create a new Detail Fragment with new data and replace view using FragmentManager.
     */
    public void addDetailFragment() {
        getCurrentRecipeStep();
        mDetailFragment = new DetailFragment();
        mDetailFragment.setStep(mRecipeData, mCurrentRecipe, mCurrentStep);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_fragment_container, mDetailFragment, DETAIL_FRAGMENT_TAG)
                .commit();
    }

    /**
     * Display a long toast error message.
     */
    public void showErrorMessage() {
        Toast t = Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_LONG);
        t.show();
    }

    /**
     * Find Main Fragment by ID, set
     *
    private void addMainFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment f = fragmentManager.findFragmentById(R.id.main_list_fragment);
        if (f == null) {
            throw new UnsupportedOperationException("Unable to load Main List fragment.");
        }
        if (!(f instanceof MainFragment) || !dataLoaded()) {
            return;
        }
        mMainFragment = (MainFragment) f;
        mMainFragment.setRecipeData(mRecipeData);
        mMainFragment.setCurrentRecipe(mCurrentRecipe);
        mMainFragment.loadCurrentRecipe();
        if (mTwoPane) addDetailFragment();
    } */

    private void setCurrentRecipeStep(int recipe) {
        setCurrentRecipeStep(recipe, 0);
    }

    private void setCurrentRecipeStep(int recipe, int step) {
        mCurrentRecipe = recipe;
        mCurrentStep = step;
        State.getInstance(getApplicationContext()).put(State.Key.ACTIVE_RECIPE_INT, mCurrentRecipe);
        State.getInstance().put(State.Key.ACTIVE_STEP_INT, mCurrentStep);
    }

    public void onStepSelected(int recipe, int step) {
        if (mTwoPane) {
            DetailFragment newFragment = new DetailFragment();
            newFragment.setRecipeData(mRecipeData);
            newFragment.setCurrentStep(recipe, step);
            newFragment.refreshSteps();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newFragment)
                    .commit();
        } else {
            final Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(State.CURRENT_RECIPE_INDEX, recipe);
            intent.putExtra(State.CURRENT_STEP_INDEX, step);
            Bundle bundle = new Bundle();
            bundle.putSerializable(State.RECIPE_DATA, mRecipeData);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void updateRecipeData(RecipeData data) {
        mRecipeData = data;
        if (mDetailFragment != null) {
            mDetailFragment.setRecipeData(mRecipeData);
        }
    }

    private class FetchRecipesTask extends AsyncTask<Void, Void, RecipeData> {

        @Override
        protected RecipeData doInBackground(Void... voids) {
            RecipeData data = NetworkUtils.fetch();
            updateRecipeData(data);
            return data;
        }

        /**
         * Show the loader before the task starts.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        /**
         * Handles updating the UI depending on the result of the background task.
         */
        @Override
        protected void onPostExecute(RecipeData data) {
            mLoadingIndicator.setVisibility(View.GONE);
            if (data != null) {
                updateRecipeData(data);
                setCurrentRecipeStep(mCurrentRecipe, mCurrentStep);
                invalidateOptionsMenu();
                updateMainFragment();
                if (mTwoPane) {
                    Log.d("BakingApp [MAI]{Acty}","[  UBUBU  ] Add detail fragment from onPostExecute");
                    //addDetailFragment();
                }
            } else {
                showErrorMessage();
            }
        }
    }

}
