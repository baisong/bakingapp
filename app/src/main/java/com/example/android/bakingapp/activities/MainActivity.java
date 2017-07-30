package com.example.android.bakingapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Recipes;
import com.example.android.bakingapp.fragments.DetailFragment;
import com.example.android.bakingapp.fragments.MainListFragment;
import com.example.android.bakingapp.tools.DatabaseHandler;
import com.example.android.bakingapp.tools.NetworkUtils;
import com.example.android.bakingapp.tools.RecipeRecordCollection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainListFragment.OnCardClickListener
        //, LoaderCallbacks<RecipeRecordCollection>
{

    private boolean mTwoPane;
    private MainListFragment mListFragment;
    private DetailFragment mDetailFragment;
    private RecipeRecordCollection mRecipeData;
    private List<String> mRecipeNames;
    private static final String LOG_TAG = "BakingApp_MainActivity";
    private static final String LIST_FRAG_TAG = "BakingApp_ListFragment";

    @BindView(R.id.pb_loading_data)
    ProgressBar mLoadingIndicator;
    //@BindView(R.id.tv_test) TextView mTest;

    public final static String EXTRA_RECIPE_INDEX = "recipeName";
    //private static final Uri RECIPES_URI = new Uri.Builder().scheme().authority().appendEncodedPath().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRecipeNames = Recipes.getDummyRecipeNames();
        notifyListFragment();

        if (findViewById(R.id.android_me_linear_layout) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) addDetailFragment();
        } else {
            mTwoPane = false;
        }

        loadRecipeData();
    }

    /**
     * Updates the RecyclerView to show the specified list of movies (remote sorted or favorites).
     */
    private void loadRecipeData() {
        new FetchRecipesTask().execute();
    }

    public void notifyListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment f = fragmentManager.findFragmentById(R.id.main_list_fragment);
        if (f == null) {
            throw new UnsupportedOperationException("Unable to load Main List fragment.");
        }
        else if (f instanceof MainListFragment) {
            mListFragment = (MainListFragment) f;
            log("Setting new names...");
            log(mRecipeNames.get(0));
            mListFragment.setRecipeNames(mRecipeNames);
        }
        else {
            log("Invalid Main List Fragment.");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                //No call for super(). Bug on API Level > 11.
            }
            else {
                super.onSaveInstanceState(outState, outPersistentState);
            }
    }

    private void log(String message) {
        Log.d(LOG_TAG, message);
    }

    public void addDetailFragment() {
        mDetailFragment = new DetailFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mDetailFragment.setRecipeNames(mRecipeNames);
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

    private void initData() {
        final DatabaseHandler handler = new DatabaseHandler(getContentResolver());
        //handler.startBulkInsert(1, null, RECIPES_URI, mData.recipes);
    }

    public void onCardSelected(int position) {
        if (mTwoPane) {
            DetailFragment newFragment = new DetailFragment();
            newFragment.setRecipeNames(mRecipeNames);
            newFragment.setListIndex(position);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_container, newFragment)
                    .commit();
        } else {
            final Intent intent = new Intent(this, RecipeActivity.class);
            intent.putExtra(EXTRA_RECIPE_INDEX, position);
            startActivity(intent);
        }

    }

    private void updateRecipeData(RecipeRecordCollection collection) {
        Log.d("RecipeListActivity", collection.getInfoString());
        mRecipeData = collection;
        mRecipeNames = mRecipeData.getRecipeNames();
    }

    public void showRecipes() {
        mListFragment.setRecipeNames(mRecipeNames);
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
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (collection != null) {
                showRecipes();
            } else {
                showErrorMessage();
            }
        }
    }

}
