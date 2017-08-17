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
import com.example.android.bakingapp.fragments.DetailFragment;
import com.example.android.bakingapp.fragments.MainFragment;
import com.example.android.bakingapp.tools.NetworkUtils;
import com.example.android.bakingapp.tools.RecipeRecordCollection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainFragment.OnStepClickListener
        //, LoaderCallbacks<RecipeRecordCollection>
{

    private boolean mTwoPane;
    private MainFragment mMainFragment;
    private DetailFragment mDetailFragment;
    private RecipeRecordCollection mRecipeData;

    private int mCurrentRecipe;
    private int mCurrentStep;

    private static final String LOG_TAG = "BakingApp_Mai_Activity";

    @BindView(R.id.pb_loading_data)
    ProgressBar mLoadingIndicator;

    public final static String EXTRA_RECIPE_INDEX = "recipeName";
    public final static String EXTRA_STEP_INDEX = "stepIndex";
    public final static String EXTRA_RECIPE_DATA = "recipeData";
    public final static String IS_TWO_PANE = "isTwoPane";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_RECIPE_DATA)) {
            mCurrentRecipe = savedInstanceState.getInt(EXTRA_RECIPE_DATA);
            if (savedInstanceState.containsKey(EXTRA_STEP_INDEX)) {
                mCurrentStep = savedInstanceState.getInt(EXTRA_RECIPE_DATA);
            }
            else {
                mCurrentStep = 0;
            }
        }
        else {
            mCurrentRecipe = 0;
            mCurrentStep = 0;
        }
        if (findViewById(R.id.ll_recipe_wrapper) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) addDetailFragment();
        } else {
            mTwoPane = false;
        }

        log("NEW STATE ***"
                + " twoPane: " + String.valueOf(mTwoPane)
                + "; Step: " + String.valueOf(mCurrentRecipe)
                + "," + String.valueOf(mCurrentStep)
                + "; Data: " + quickLogData());

        new FetchRecipesTask().execute();
    }


    private String quickLogData() {
        if (mRecipeData == null) {
            return "0";
        }
        return String.valueOf(mRecipeData.getCount());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(EXTRA_RECIPE_DATA)) {
            mRecipeData = (RecipeRecordCollection) savedInstanceState.getSerializable(EXTRA_RECIPE_DATA);
            mCurrentRecipe = savedInstanceState.getInt(EXTRA_RECIPE_INDEX);
            mCurrentStep = savedInstanceState.getInt(EXTRA_STEP_INDEX);
            notifyFragments();
            //log("Restored instance with data: " + mRecipeData.getInfoString());
        } else {
            //log("Restored instance without recipe data. " + mRecipeData);
        }
        if (findViewById(R.id.ll_recipe_wrapper) != null) {
            mTwoPane = true;
            if (!savedInstanceState.getBoolean(IS_TWO_PANE)) {
                log("New orientation!!! Now horizontal");
                //log("IN STATE: " + String.valueOf(mRecipeData.getInfoString()));
                addDetailFragment();
                // ???
                // https://stackoverflow.com/questions/15313598/once-for-all-how-to-correctly-save-instance-state-of-fragments-in-back-stack
            }
            else {
                log("Restore but no rotate - still horizontal");
            }
        } else {
            mTwoPane = false;
            if (savedInstanceState.getBoolean(IS_TWO_PANE)) {
                log("New orientation!!! Now vertical");
                //log("IN STATE: " + String.valueOf(mRecipeData.getInfoString()));
                //addDetailFragment();
                // ???
                // https://stackoverflow.com/questions/15313598/once-for-all-how-to-correctly-save-instance-state-of-fragments-in-back-stack
            }
            else {
                log("Restore but no rotate - still vertical");
            }
        }

        log("IN STATE <<<"
                + " twoPane: " + String.valueOf(mTwoPane)
                + "; Step: " + String.valueOf(mCurrentRecipe)
                + "," + String.valueOf(mCurrentStep)
                + "; Data: " + quickLogData());
    }

    private void notifyFragments() {
        if (mDetailFragment != null) {
            mDetailFragment.setRecipeData(mRecipeData);
            mDetailFragment.setCurrentStep(mCurrentRecipe, mCurrentStep);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_TWO_PANE, mTwoPane);
        outState.putInt(EXTRA_RECIPE_INDEX, mCurrentRecipe);
        outState.putInt(EXTRA_STEP_INDEX, mCurrentStep);
        outState.putSerializable(EXTRA_RECIPE_DATA, mRecipeData);
        log("Mai OUT >>>"
                + " twoPane: " + String.valueOf(mTwoPane)
                + "; Step: " + String.valueOf(mCurrentRecipe)
                + "," + String.valueOf(mCurrentStep)
                + "; Data: " + quickLogData());
        super.onSaveInstanceState(outState);
    }

    public void notifyListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment f = fragmentManager.findFragmentById(R.id.main_list_fragment);
        if (f == null) {
            throw new UnsupportedOperationException("Unable to load Main List fragment.");
        } else if (f instanceof MainFragment) {
            mMainFragment = (MainFragment) f;
            //log("MainActivity::notifyListFragment()");
            if (mRecipeData.getCount() > 0) {
                mMainFragment.setRecipeData(mRecipeData);
                mMainFragment.setCurrentRecipe(mCurrentRecipe);
                mMainFragment.loadCurrentRecipe();
            }
        } else {
            log("Invalid Main List Fragment.");
        }
    }

    private void log(String message) {
        Log.d(LOG_TAG, message);
    }

    public void addDetailFragment() {
        mDetailFragment = new DetailFragment();
        mDetailFragment.setStep(mRecipeData, 1, 0);
        debugData();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_container, mDetailFragment)
                .commit();
    }

    private void debugData() {
        log("HOW MANY RECIPES? " + String.valueOf(mRecipeData.getCount()));
    }

    public void showErrorMessage() {
        Toast t = Toast.makeText(this, "Error", Toast.LENGTH_LONG);
        t.show();
    }

    private void onRecipeSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment f = fragmentManager.findFragmentById(R.id.main_list_fragment);
        if (f == null) {
            throw new UnsupportedOperationException("Unable to load Main List fragment.");
        } else if (f instanceof MainFragment) {
            mMainFragment = (MainFragment) f;
            log("MainActivity::onRecipeSelected()");
            if (mRecipeData.getCount() > 0) {
                mMainFragment.setRecipeData(mRecipeData);
                mMainFragment.setCurrentRecipe(position);
                mMainFragment.loadCurrentRecipe();
            }
        } else {
            log("Invalid Main List Fragment.");
        }
    }

    public void onStepSelected(int recipe, int step) {
        if (mTwoPane) {
            DetailFragment newFragment = new DetailFragment();
            newFragment.setRecipeData(mRecipeData);
            newFragment.setCurrentStep(recipe, step);
            newFragment.refreshSteps();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_container, newFragment)
                    .commit();
        } else {
            final Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(EXTRA_RECIPE_INDEX, recipe);
            intent.putExtra(EXTRA_STEP_INDEX, step);
            Bundle bundle = new Bundle();
            bundle.putSerializable(EXTRA_RECIPE_DATA, mRecipeData);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void updateRecipeData(RecipeRecordCollection collection) {
        //log("Update recipe data with: " + collection.getInfoString());
        mRecipeData = collection;
        if (mDetailFragment != null) {
            mDetailFragment.setRecipeData(mRecipeData);
        }
        //log("Updated to: " + mRecipeData.getInfoString());
    }

    public void showRecipes() {
        //mMainFragment.setRecipeData(mRecipeData);
        invalidateOptionsMenu();
        notifyListFragment();
        //if (mTwoPane) mDetailFragment.setRecipeNames(mRecipeNames);
    }

    public class FetchRecipesTask extends AsyncTask<Void, Void, RecipeRecordCollection> {

        @Override
        protected RecipeRecordCollection doInBackground(Void... voids) {
            RecipeRecordCollection collection = NetworkUtils.fetch();
            updateRecipeData(collection);
            return collection;
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
        protected void onPostExecute(RecipeRecordCollection collection) {
            mLoadingIndicator.setVisibility(View.GONE);
            if (collection != null) {
                updateRecipeData(collection);
                showRecipes();
            } else {
                showErrorMessage();
            }
        }
    }

    /**
     * Gets called every time the user presses the menu button.
     * Use if your menu is dynamic.
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
     * Handles menu item selection by updating the data inside the RecyclerView.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id > 0 && id < 5) {
            onRecipeSelected(id);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
