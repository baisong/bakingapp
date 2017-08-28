package com.example.android.bakingapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Displays the "Main List" view and widescreen Detail Fragment for "Main-Detail" navigation.
 * <p>
 * Launched as app parent activity, or via explicit intent when detail view rotates widescreen.
 */
public class MainActivity extends AppCompatActivity implements MainFragment.OnStepClickListener {

    private boolean mTwoPane;
    private DetailFragment mDetailFragment;
    private RecipeData mRecipeData;
    private int mCurrentRecipe;
    private int mCurrentStep;
    private final static String DETAIL_FRAGMENT_TAG = "DetailFrag";

    @BindView(R.id.pb_loading_data)
    ProgressBar mLoadingIndicator;

    /**
     * Provide
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Bundle extras = getIntent().getExtras();

        // Set current recipe step to the first step of the first recipe on initial launch.
        boolean justLaunchedApp = ((savedInstanceState == null) && (extras == null));
        if (justLaunchedApp) {
            setCurrentRecipeStep(0, 0);
            State.getInstance(getApplicationContext()).put(State.Key.IS_PLAYING, false);
            State.getInstance().put(State.Key.ACTIVE_RECIPE_INT, 0);
            State.getInstance().put(State.Key.ACTIVE_STEP_INT, 0);
        }
        // Otherwise, restore from SharedPreferences if available.
        else {
            getCurrentRecipeStep();
            // Restore fetched recipe data if available.
            if (savedInstanceState != null && savedInstanceState.containsKey(State.RECIPE_DATA)) {
                mRecipeData = (RecipeData) savedInstanceState.getSerializable(State.RECIPE_DATA);
            }
            // Fetch current recipe and step if device just rotated from step (detail) screen.
            if (extras != null && extras.getBoolean(State.LAUNCHED_FROM_DETAIL)) {
                mCurrentRecipe = extras.getInt(State.CURRENT_RECIPE_INDEX);
                mCurrentStep = extras.getInt(State.CURRENT_STEP_INDEX);
            }
        }

        // Detect two pane and release ExoPlayer if detail exists to prevent duplicate session.
        mTwoPane = (findViewById(R.id.ll_recipe_wrapper) != null);
        if (mTwoPane && (mDetailFragment != null)) mDetailFragment.releasePlayer();

        // Update Main & Detail Fragments if data is already loaded, otherwise fetch in background.
        if (dataLoaded()) {
            updateMainAndDetailFragments();
        } else {
            new FetchRecipesTask().execute();
        }
    }

    /**
     * Restore fetched recipe data if available, update current recipe, step, and twoPane values.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(State.RECIPE_DATA)) {
            mRecipeData = (RecipeData) savedInstanceState.getSerializable(State.RECIPE_DATA);
        }
        getCurrentRecipeStep();
        mTwoPane = (findViewById(R.id.ll_recipe_wrapper) != null);
        if (!dataLoaded() || mDetailFragment == null) {
            return;
        }
        mDetailFragment.setRecipeData(mRecipeData);
        mDetailFragment.setNewStepState(mCurrentRecipe, mCurrentStep);
    }

    /**
     * Store current data, recipe, step and twoPane values.
     *
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
        // Load disabled label for overflow options menu.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_options_menu).setEnabled(false);
        // Load data into menu for navigation between recipes.
        if (dataLoaded()) {
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
            updateMainAndDetailFragments();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility function to treat null data as 0.
     *
     * @return
     */
    private boolean dataLoaded() {
        return mRecipeData != null && mRecipeData.getCount() > 0;
    }

    /**
     * Find the Main Fragment by ID, set current data and recipe values, and update view.
     */
    public void updateMainAndDetailFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment f = fragmentManager.findFragmentById(R.id.main_list_fragment);
        if (f == null) {
            throw new UnsupportedOperationException(getString(R.string.could_not_load_main_frag));
        }
        if (!(f instanceof MainFragment) || !dataLoaded()) {
            return;
        }
        getCurrentRecipeStep();
        MainFragment mainFragment = (MainFragment) f;
        mainFragment.setRecipeData(mRecipeData);
        mainFragment.setCurrentRecipe(mCurrentRecipe);
        mainFragment.loadCurrentRecipe();

        if (mTwoPane) {
            addDetailFragment();
        }
    }

    /**
     * Create a new Detail Fragment with new data and replace view using FragmentManager.
     */
    public void addDetailFragment() {
        getCurrentRecipeStep();
        mDetailFragment = DetailFragment.newInstance(mRecipeData, mCurrentRecipe, mCurrentStep);
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
     * Reset the current recipe and step to the first step of the specified recipe.
     *
     * @param recipe
     */
    private void setCurrentRecipeStep(int recipe) {
        setCurrentRecipeStep(recipe, 0);
    }

    /**
     * Set the current recipe and step to member variables and SharedPreferences.
     *
     * @param recipe
     * @param step
     */
    private void setCurrentRecipeStep(int recipe, int step) {
        mCurrentRecipe = recipe;
        mCurrentStep = step;
        State.getInstance(getApplicationContext()).put(State.Key.ACTIVE_RECIPE_INT, mCurrentRecipe);
        State.getInstance().put(State.Key.ACTIVE_STEP_INT, mCurrentStep);
    }

    /**
     * Launch activity or display fragment. Implementation of MainFragment.onStepClickListener().
     *
     * @param recipe
     * @param step
     */
    public void onStepSelected(int recipe, int step) {
        if (mTwoPane) {
            int container = R.id.detail_fragment_container;
            DetailFragment f = DetailFragment.newInstance(mRecipeData, recipe, step);
            getSupportFragmentManager().beginTransaction().replace(container, f).commit();
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

    /**
     * Set the member variable and DetailFragment member variable to the received data.
     *
     * @param data
     */
    private void updateRecipeData(RecipeData data) {
        mRecipeData = data;
        if (mDetailFragment != null) {
            mDetailFragment.setRecipeData(mRecipeData);
        }
    }

    /**
     * Fetch the recipe data from remote source via network process on background thread.
     */
    private class FetchRecipesTask extends AsyncTask<Void, Void, RecipeData> {

        /**
         * Run the HTTP request in background thread.
         *
         * @param voids
         * @return
         */
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
         * Update the UI depending on the result of the background task.
         */
        @Override
        protected void onPostExecute(RecipeData data) {
            mLoadingIndicator.setVisibility(View.GONE);
            if (data == null) {
                showErrorMessage();
                return;
            }
            updateRecipeData(data);
            setCurrentRecipeStep(mCurrentRecipe, mCurrentStep);
            invalidateOptionsMenu();
            updateMainAndDetailFragments();
        }
    }
}
