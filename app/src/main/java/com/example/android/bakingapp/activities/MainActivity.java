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

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     *
     @Override public Loader<RecipeRecordCollection> onCreateLoader(int id, final Bundle loaderArgs) {

     return new AsyncTaskLoader<RecipeRecordCollection>(this) {

     /* This String array will hold and help cache our weather data *
     RecipeRecordCollection mData = null;

     // COMPLETED (3) Cache the weather data in a member variable and deliver it in onStartLoading.
     /**
      * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
     *
     @Override protected void onStartLoading() {
     if (mData != null) {
     deliverResult(mData);
     } else {
     //mLoadingIndicator.setVisibility(View.VISIBLE);
     forceLoad();
     }
     }

     /**
      * This is the method of the AsyncTaskLoader that will load and parse the JSON data
      * from OpenWeatherMap in the background.
      *
      * @return Weather data from OpenWeatherMap as an array of Strings.
     *         null if an error occurs
     *
     @Override public RecipeRecordCollection loadInBackground() {
     try {
     RecipeRecordCollection collection = NetworkUtils.fetch();
     return collection;
     } catch (Exception e) {
     e.printStackTrace();
     return null;
     }
     }

     /**
      * Sends the result of the load to the registered listener.
      *
      * @param data The result of the load
     *
    public void deliverResult(RecipeRecordCollection data) {
    mData = data;
    super.deliverResult(data);
    }
    };
    }*/

    // COMPLETED (4) When the load is finished, show either the data or an error message if there is no data
    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     *
     @Override public void onLoadFinished(Loader<RecipeRecordCollection> loader, RecipeRecordCollection data) {
     if (data != null) {
     showRecipeData(data);
     }
     else {
     showErrorMessage();
     }
     //mLoadingIndicator.setVisibility(View.INVISIBLE);
     /*
     mForecastAdapter.setWeatherData(data);
     if (null == data) {
     showErrorMessage();
     } else {
     showWeatherDataView();
     }
      *
     }
     */


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @Override public void onLoaderReset(Loader<RecipeRecordCollection> loader) {
     * /*
     * We aren't using this method in our example application, but we are required to Override
     * it to implement the LoaderCallbacks<String> interface
     * <p>
     * }
     */

    public void showRecipeData(RecipeRecordCollection collection) {
        mDetailFragment.setRecipeNames(collection.getRecipeNames());
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
            newFragment.setRecipeNames(Recipes.getDummyRecipeNames());
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

    public void showRecipeList() {
        mListFragment.setRecipeNames(mRecipeNames);
        if (mTwoPane) mDetailFragment.setRecipeNames(mRecipeNames);
        notifyListFragment();
    }

    public class FetchRecipesTask extends AsyncTask<Void, Void, RecipeRecordCollection> {

        @Override
        protected RecipeRecordCollection doInBackground(Void... voids) {
            RecipeRecordCollection collection = NetworkUtils.fetch();
            Log.d("RecipeListActivity", collection.getInfoString());
            mRecipeNames = collection.getRecipeNames();
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
                showRecipeList();
            } else {
                showErrorMessage();
            }
        }
    }

}
