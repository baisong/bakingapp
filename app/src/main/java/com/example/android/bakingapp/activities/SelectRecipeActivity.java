package com.example.android.bakingapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.SelectRecipeAdapter;
import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.data.State;
import com.example.android.bakingapp.tools.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Displays the "Main List" view and widescreen Detail Fragment for "Main-Detail" navigation.
 * <p>
 * Launched as app parent activity, or via explicit intent when detail view rotates widescreen.
 */
public class SelectRecipeActivity extends AppCompatActivity {

    private RecipeData mRecipeData;

    @BindView(R.id.pb_loading_data)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.gv_select_recipe)
    GridView mRecipeList;
    @BindView(R.id.tv_error_loading_data)
    TextView mErrorLoading;
    private SelectRecipeAdapter mAdapter;

    /**
     * @TODO As per Nanodegree Guidelines link, it is required that you restore the position of the
     * recycler view post screen rotation. For Recyclerview position restoration, you can refer:
     * https://stackoverflow.com/questions/27816217/how-to-save-recyclerviews-scroll-position-using-recyclerview-state
     * http://panavtec.me/retain-restore-recycler-view-scroll-position
     * https://medium.com/@dimezis/android-scroll-position-restoring-done-right-cff1e2104ac7
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_recipe);
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
            // Restore fetched recipe data if available.
            if (savedInstanceState != null && savedInstanceState.containsKey(State.RECIPE_DATA)) {
                mRecipeData = (RecipeData) savedInstanceState.getSerializable(State.RECIPE_DATA);
            }
        }

        // Update Main & Detail Fragments if data is already loaded, otherwise fetch in background.
        mAdapter = new SelectRecipeAdapter(getApplicationContext(), new ContentValues[]{});
        mRecipeList.setAdapter(mAdapter);
        mRecipeList.setOnItemClickListener(mOnItemClickListener);
        if (!dataLoaded()) {
            new FetchRecipesTask().execute();
        }
    }

    /**
     * Handle the configuration of a widget, updating the stored value and finish the Activity.
     */
    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int currentStep = 0;
            setCurrentRecipeStep(position, currentStep);
            final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(State.CURRENT_RECIPE_INDEX, position);
            intent.putExtra(State.CURRENT_STEP_INDEX, currentStep);
            if (dataLoaded()) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(State.RECIPE_DATA, mRecipeData);
                intent.putExtras(bundle);
            }
            startActivity(intent);
        }
    };

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
     * Display a long toast error message.
     */
    public void showErrorMessage() {
        mErrorLoading.setVisibility(View.VISIBLE);
        mRecipeList.setVisibility(View.GONE);
        mErrorLoading.setText(getString(R.string.error_message));
    }

    public void showRecipes() {
        Log.d("BakingApp", "Show recipes");
        mErrorLoading.setVisibility(View.GONE);
        mAdapter.setItems(mRecipeData.getRecipes());
        mRecipeList.setOnItemClickListener(mOnItemClickListener);
        mRecipeList.setVisibility(View.VISIBLE);
    }

    /**
     * Set the current recipe and step to member variables and SharedPreferences.
     *
     * @param recipe
     * @param step
     */
    private void setCurrentRecipeStep(int recipe, int step) {
        State.getInstance(getApplicationContext()).put(State.Key.ACTIVE_RECIPE_INT, recipe);
        State.getInstance().put(State.Key.ACTIVE_STEP_INT, step);
    }

    /**
     * Set the member variable and DetailFragment member variable to the received data.
     *
     * @param data
     */
    private void setRecipeData(RecipeData data) {
        mRecipeData = data;
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
            setRecipeData(data);
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
            setRecipeData(data);
            showRecipes();
        }
    }

}
