package com.example.android.bakingapp.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.WidgetRecipeAdapter;
import com.example.android.bakingapp.tools.NetworkUtils;
import com.example.android.bakingapp.data.RecipeData;

/**
 * The configuration screen for the {@link RecipeWidgetProvider RecipeWidgetProvider} AppWidget.
 */
public class WidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.example.android.bakingapp.widget.RecipeWidgetProvider";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final int DEFAULT_NO_SELECTION = -1;
    private RecipeData mRecipeData;

    ProgressBar mLoadingIndicator;
    ListView mRecipeList;
    TextView mErrorLoading;
    WidgetRecipeAdapter mAdapter;

    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Object listItem = mRecipeList.getItemAtPosition(position);
            //int recipeId = position;//mRecipeList.getText().toString();
            Log.d("BakingApp", "Clicked widget position " + String.valueOf(position));
            final Context context = WidgetConfigureActivity.this;
            saveRecipeIdPref(context, mAppWidgetId, position);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RecipeWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public WidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveRecipeIdPref(Context context, int appWidgetId, int recipeId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, recipeId);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static int loadRecipePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_KEY + appWidgetId, DEFAULT_NO_SELECTION);
    }

    public static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.recipe_widget_provider_configure);
        mRecipeList = (ListView) findViewById(R.id.appwidget_recipe);
        mAdapter = new WidgetRecipeAdapter(getApplicationContext(), new ContentValues[]{});
        mRecipeList.setAdapter(mAdapter);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_data);
        mErrorLoading = (TextView) findViewById(R.id.tv_error_loading_data);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        //Toast.makeText(getApplicationContext(), "Loading recipes...", Toast.LENGTH_LONG).show();
        new FetchRecipesTask().execute();
        //mRecipeList.setText(loadRecipePref(WidgetConfigureActivity.this, mAppWidgetId));
    }

    public class FetchRecipesTask extends AsyncTask<Void, Void, RecipeData> {

        @Override
        protected RecipeData doInBackground(Void... voids) {
            RecipeData collection = NetworkUtils.fetch();
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
        protected void onPostExecute(RecipeData collection) {
            mLoadingIndicator.setVisibility(View.GONE);
            mRecipeData = collection;
            if (collection != null) {
                updateRecipeData(collection);
            } else {
                showErrorMessage();
            }
        }
    }

    private void updateRecipeData(RecipeData collection) {
        //mRecipeList.setAdapter(new WidgetRecipeAdapter(this, collection.getRecipes()));
        mAdapter.setItems(collection.getRecipes());
        mRecipeList.setOnItemClickListener(mOnItemClickListener);
        mRecipeList.setVisibility(View.VISIBLE);
        Log.d("BakingApp", "notify data set change!");
    }

    public void showErrorMessage() {
        Log.d("BakingApp", "show error message!");
        mErrorLoading.setVisibility(View.VISIBLE);
    }
}

