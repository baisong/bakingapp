package com.example.android.bakingapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.DummyData;
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
    private List<String> mRecipeNames;
    private static final String LOG_TAG = "BakingApp_MainActivity";
    private static final String LIST_FRAG_TAG = "BakingApp_ListFragment";

    @BindView(R.id.pb_loading_data)
    ProgressBar mLoadingIndicator;
    //@BindView(R.id.tv_test) TextView mTest;

    public final static String EXTRA_RECIPE_INDEX = "recipeName";
    public final static String EXTRA_STEP_INDEX = "stepIndex";
    public final static String EXTRA_RECIPE_DATA = "recipeData";
    //private static final Uri RECIPES_URI = new Uri.Builder().scheme().authority().appendEncodedPath().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRecipeNames = DummyData.getRecipeNames();
        mRecipeData = new RecipeRecordCollection();
        //notifyListFragment();

        if (findViewById(R.id.ll_recipe_wrapper) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) addDetailFragment();
        } else {
            mTwoPane = false;
        }

        new FetchRecipesTask().execute();
    }

    public void notifyListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment f = fragmentManager.findFragmentById(R.id.main_list_fragment);
        if (f == null) {
            throw new UnsupportedOperationException("Unable to load Main List fragment.");
        } else if (f instanceof MainFragment) {
            mMainFragment = (MainFragment) f;
            Log.d("BakingApp", "MainActivity::notifyListFragment()");
            if (mRecipeData.getCount() > 0) {
                log(mRecipeNames.get(0));
                mMainFragment.setRecipeNames(mRecipeNames);
                mMainFragment.setRecipeData(mRecipeData);
                mMainFragment.setListIndex(0);
                mMainFragment.loadCurrentRecipe();
            }
        } else {
            log("Invalid Main List Fragment.");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //No call for super(). Bug on API Level > 11.
        } else {
            super.onSaveInstanceState(outState, outPersistentState);
        }
    }

    private void log(String message) {
        Log.d(LOG_TAG, message);
    }

    public void addDetailFragment() {
        mDetailFragment = new DetailFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        //mDetailFragment.setRecipeNames(mRecipeNames);
        fragmentManager.beginTransaction()
                .add(R.id.recipe_container, mDetailFragment)
                .commit();
    }

    public void showErrorMessage() {
        Toast t = Toast.makeText(this, "Error", Toast.LENGTH_LONG);
        t.show();
    }


    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        //mForecastAdapter.setWeatherData(null);
    }

    /*
    public void onCardSelected(int recipe, int step) {
        onStepSelected(recipe, step);
    }
    */

    private void onRecipeSelected(int position) {
        /**
         * @TODO Update the fragment this way, not via replace transaction.
         */
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment f = fragmentManager.findFragmentById(R.id.main_list_fragment);
        if (f == null) {
            throw new UnsupportedOperationException("Unable to load Main List fragment.");
        } else if (f instanceof MainFragment) {
            mMainFragment = (MainFragment) f;
            Log.d("BakingApp", "MainActivity::onRecipeSelected()");
            if (mRecipeData.getCount() > 0) {
                log(mRecipeNames.get(0));
                mMainFragment.setRecipeNames(mRecipeNames);
                mMainFragment.setRecipeData(mRecipeData);
                mMainFragment.setListIndex(position);
                mMainFragment.loadCurrentRecipe();
            }
        } else {
            log("Invalid Main List Fragment.");
        }
        /*
        MainFragment newFragment = new MainFragment();
        newFragment.setRecipeNames(mRecipeNames);
        newFragment.setCurrentStep(position);
        newFragment.setRecipeData(mRecipeData);
        newFragment.loadCurrentRecipe();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_container, newFragment)
                .commit();
                */
    }

    public void onStepSelected(int recipe, int step) {
        if (mTwoPane) {
            DetailFragment newFragment = new DetailFragment();
            //newFragment.setRecipeNames(mRecipeNames);
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
        Log.d("RecipeListActivity", collection.getInfoString());
        mRecipeData = collection;
        mRecipeNames = mRecipeData.getRecipeNames();
    }

    public void showRecipes() {
        /*
        mMainFragment.setRecipeNames(mRecipeNames);
        mMainFragment.setRecipeData(mRecipeData);
        mMainFragment
        */
        invalidateOptionsMenu();
        notifyListFragment();
        //if (mTwoPane) mDetailFragment.setRecipeNames(mRecipeNames);
    }


    private void updateCurrentRecipe() {
        /*
        MainFragment newFragment = new MainFragment();
        newFragment.setRecipeNames(mRecipeNames);
        newFragment.setCurrentStep(0);
        newFragment.setRecipeData(mRecipeData);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_container, newFragment)
                .commit();
                */
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
