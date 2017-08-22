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
import com.example.android.bakingapp.data.State;
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
    private String DETAIL_FRAGMENT_TAG = "DetailFrag";

    private static final String LOG_TAG = "BakingApp [MAI]{Acty}";

    @BindView(R.id.pb_loading_data)
    ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate BEG");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            getCurrentRecipeStep();
            if(savedInstanceState.containsKey(State.RECIPE_DATA)) {
                mRecipeData = (RecipeRecordCollection) savedInstanceState.getSerializable(State.RECIPE_DATA);
            }
        }
        else {
            log("INIT NO PLAYER");
            State.getInstance(getApplicationContext()).put(State.Key.IS_PLAYING, false);
        }
        if (findViewById(R.id.ll_recipe_wrapper) != null) {
            mTwoPane = true;
            releaseFragmentPlayer();
            debug("TwoPane!");
            //if (dataLoaded() || savedInstanceState == null) {
            //addDetailFragment();
            //}
        } else {
            mTwoPane = false;
        }


        if (dataLoaded()) {
            loadMainFragmentRecipe();
            if (mTwoPane) addDetailFragment();
        }
        else {
            new FetchRecipesTask().execute();
        }
        debug("onCreate END");
    }

    private void releaseFragmentPlayer() {
        log("Looking for fragment to release...");
        if (mDetailFragment != null && mDetailFragment instanceof DetailFragment) {
            log("EUREKA!!!");
            mDetailFragment.releasePlayer("From Main");
        }
        /*
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment f = fragmentManager.findFragmentByTag(DETAIL_FRAGMENT_TAG);
        if (f == null) {
            throw new UnsupportedOperationException("Unable to load Main List fragment.");
        } else if (f instanceof DetailFragment) {
            mDetailFragment = (DetailFragment) f;
            mDetailFragment.releasePlayer();
        } else {
            log("Invalid Main List Fragment.");
        }
        */
    }

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

    private boolean dataLoaded() {
        return mRecipeData != null && mRecipeData.getCount() > 0;
    }
    private String quickLogData() {
        if (mRecipeData == null) {
            return "0";
        }
        return String.valueOf(mRecipeData.getCount());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        debug("onRestore BEG");
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(State.RECIPE_DATA)) {
            mRecipeData = (RecipeRecordCollection) savedInstanceState.getSerializable(State.RECIPE_DATA);
            log("onRestore WITH DATA FROM INSTANCE");
        } else {
            log("onRestore NO DATA FROM INSTANCE");
        }
        getCurrentRecipeStep();
        if (dataLoaded() && mDetailFragment != null) {
            mDetailFragment.setRecipeData(mRecipeData);
            mDetailFragment.setCurrentStep(mCurrentRecipe, mCurrentStep);
        }
        if (findViewById(R.id.ll_recipe_wrapper) != null) {
            mTwoPane = true;
            if (!savedInstanceState.getBoolean(State.IS_TWO_PANE)) {
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
            if (savedInstanceState.getBoolean(State.IS_TWO_PANE)) {
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
        debug("onRestore END");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(State.IS_TWO_PANE, mTwoPane);
        outState.putInt(State.CURRENT_RECIPE_INDEX, mCurrentRecipe);
        outState.putInt(State.CURRENT_STEP_INDEX, mCurrentStep);
        outState.putSerializable(State.RECIPE_DATA, mRecipeData);
        debug("saveInst END");
        super.onSaveInstanceState(outState);
    }

    public void loadMainFragmentRecipe() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment f = fragmentManager.findFragmentById(R.id.main_list_fragment);
        if (f == null) {
            throw new UnsupportedOperationException("Unable to load Main List fragment.");
        } else if (f instanceof MainFragment) {
            mMainFragment = (MainFragment) f;
            debug("loadMainFrag");
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
        mDetailFragment.setStep(mRecipeData, mCurrentRecipe, mCurrentStep);
        debug("addDetailFrag");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipe_container, mDetailFragment, DETAIL_FRAGMENT_TAG)
                .commit();
    }

    public void showErrorMessage() {
        Toast t = Toast.makeText(this, "Error", Toast.LENGTH_LONG);
        t.show();
    }

    private void onRecipeSelected(int position) {
        log("=== onRecipeSelected ===");
        mCurrentRecipe = position;
        mCurrentStep = 0;
        State.getInstance(getApplicationContext()).put(State.Key.ACTIVE_RECIPE_INT, mCurrentRecipe);
        State.getInstance().put(State.Key.ACTIVE_STEP_INT, mCurrentStep);
        debug("selected");

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment f = fragmentManager.findFragmentById(R.id.main_list_fragment);
        if (f == null) {
            throw new UnsupportedOperationException("Unable to load Main List fragment.");
        } else if (f instanceof MainFragment) {
            mMainFragment = (MainFragment) f;
            if (dataLoaded()) {
                mMainFragment.setRecipeData(mRecipeData);
                mMainFragment.setCurrentRecipe(mCurrentRecipe);
                mMainFragment.loadCurrentRecipe();
                if (mTwoPane) {
                    addDetailFragment();
                }
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
            intent.putExtra(State.CURRENT_RECIPE_INDEX, recipe);
            intent.putExtra(State.CURRENT_STEP_INDEX, step);
            Bundle bundle = new Bundle();
            bundle.putSerializable(State.RECIPE_DATA, mRecipeData);
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
                invalidateOptionsMenu();
                loadMainFragmentRecipe();
                if (mTwoPane) addDetailFragment();
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

        if (id >= 0 && id < 4) {
            onRecipeSelected(id);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void debug(String note) {
        log(note + " twoPane: " + String.valueOf(mTwoPane)
                + "; Step: " + String.valueOf(mCurrentRecipe)
                + "," + String.valueOf(mCurrentStep)
                + "; Data: " + quickLogData());
    }

    public void playVideo(View view) {
        String videoUrl = (String) view.getTag();
        Toast.makeText(getApplicationContext(), videoUrl, Toast.LENGTH_LONG).show();
    }
}
