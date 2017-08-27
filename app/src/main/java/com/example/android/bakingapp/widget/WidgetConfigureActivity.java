package com.example.android.bakingapp.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.WidgetRecipeAdapter;
import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.tools.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The configuration screen for the {@link RecipeWidgetProvider RecipeWidgetProvider} AppWidget.
 */
public class WidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.example.android.bakingapp.widget.RecipeWidgetProvider";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final int DEFAULT_NO_SELECTION = -1;
    WidgetRecipeAdapter mAdapter;

    @BindView(R.id.pb_loading_data)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.appwidget_recipe)
    ListView mRecipeList;
    @BindView(R.id.tv_error_loading_data)
    TextView mErrorLoading;

    /**
     * Handle the configuration of a widget, updating the stored value and finish the Activity.
     */
    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Update the app widget with the selected recipe position.
            final Context context = WidgetConfigureActivity.this;
            saveRecipeIdPref(context, mAppWidgetId, position);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RecipeWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Pass back the original appWidgetId.
            Intent data = new Intent();
            data.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, data);
            finish();
        }
    };

    public WidgetConfigureActivity() {
        super();
    }

    /**
     * Store the new recipe position SharedPreference keyvalue for the specified AppWidget.
     *
     * @param context
     * @param appWidgetId
     * @param recipeId
     */
    static void saveRecipeIdPref(Context context, int appWidgetId, int recipeId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, recipeId);
        prefs.apply();
    }

    /**
     * Read the SharedPreferences object for this widget or default value.
     *
     * @param context
     * @param appWidgetId
     * @return
     */
    public static int loadRecipePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_KEY + appWidgetId, DEFAULT_NO_SELECTION);
    }

    /**
     * Remove the SharedPreference for the deleted AppWidget.
     *
     * @param context
     * @param appWidgetId
     */
    public static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    /**
     * Set up the widget configuration view.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        setContentView(R.layout.recipe_widget_provider_configure);
        ButterKnife.bind(this);
        mAdapter = new WidgetRecipeAdapter(getApplicationContext(), new ContentValues[]{});
        mRecipeList.setAdapter(mAdapter);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String key = AppWidgetManager.EXTRA_APPWIDGET_ID;
            int defaultId = AppWidgetManager.INVALID_APPWIDGET_ID;
            mAppWidgetId = extras.getInt(key, defaultId);
        }

        // Handle an intent without an app widget ID with an error message.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        new FetchRecipesTask().execute();
    }

    /**
     * Fetch recipe data in background thread.
     */
    private class FetchRecipesTask extends AsyncTask<Void, Void, RecipeData> {

        @Override
        protected RecipeData doInBackground(Void... voids) {
            return NetworkUtils.fetch();
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
        protected void onPostExecute(RecipeData collection) {
            mLoadingIndicator.setVisibility(View.GONE);
            if (collection != null) {
                updateRecipeData(collection);
            } else {
                showErrorMessage();
            }
        }
    }

    /**
     * Complete the background data fetch and update view.
     *
     * @param collection
     */
    private void updateRecipeData(RecipeData collection) {
        mAdapter.setItems(collection.getRecipes());
        mRecipeList.setOnItemClickListener(mOnItemClickListener);
        mRecipeList.setVisibility(View.VISIBLE);
    }

    /**
     * Display error in case no data was fetched.
     */
    public void showErrorMessage() {
        mErrorLoading.setVisibility(View.VISIBLE);
    }
}

