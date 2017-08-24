package com.example.android.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.bakingapp.activities.WidgetActivity;
import com.example.android.bakingapp.adapters.ShoppingListAdapter;
import com.example.android.bakingapp.data.Schema;
import com.example.android.bakingapp.tools.NetworkUtils;
import com.example.android.bakingapp.tools.RecipeRecordCollection;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WidgetActivity WidgetActivity}
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    private ShoppingListAdapter mAdapter;
    private int mRecipeId;
    private Context mContext;
    private RemoteViews mViews;
    private static String mPackageName;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        //mContext = context;
        int recipeId = WidgetActivity.loadRecipePref(context, appWidgetId);
        // Construct the RemoteViews object
        RecipeRecordCollection collection = NetworkUtils.fetch();
        String recipeName = collection.getRecipe(recipeId).getAsString(Schema.RECIPE_NAME);
        //ContentValues[] ingredients;
        mPackageName = context.getPackageName();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);

        views.setTextViewText(R.id.appwidget_recipe_name, recipeName);
        new FetchRecipesTask(views).execute(recipeId);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            WidgetActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static class FetchRecipesTask extends AsyncTask<Integer, Void, RecipeRecordCollection> {
        private RemoteViews mViews;
        public FetchRecipesTask(RemoteViews views) {
            mViews = views;
        }

        @Override
        protected RecipeRecordCollection doInBackground(Integer... ints) {
            int recipeId = ints[0];
            if (recipeId >= 0 && recipeId <= 3) {
                Log.d("BakingApp", "Fetch widget LOAD: " + String.valueOf(recipeId));
                RecipeRecordCollection collection = NetworkUtils.fetch();
                RemoteViews views = new RemoteViews(mPackageName, R.layout.recipe_widget_provider);
                views.setTextViewText(R.id.appwidget_recipe_name, collection.getRecipe(recipeId).getAsString(Schema.RECIPE_NAME));
                //ContentValues[] ingredients = collection.getIngredients(recipeId);
                return collection;
            }
            else {
                Log.d("BakingApp", "Fetch widget SKIP: " + String.valueOf(recipeId));
            }
            return null;
        }

        /**
         * Show the loader before the task starts.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Handles updating the UI depending on the result of the background task.
         */
        @Override
        protected void onPostExecute(RecipeRecordCollection collection) {
            // @TODO
            mViews.setTextViewText();
            if (collection != null) {
                updateShoppingList(collection);
            } else {
                showErrorMessage();
            }
        }
    }

    private static void updateShoppingList(RecipeRecordCollection collection) {
        Log.d("BakingApp", "Update shopping list with data.");
        RemoteViews views = new RemoteViews(mPackageName, R.layout.recipe_widget_provider);
        //int recipeId = WidgetActivity.loadRecipePref(context, appWidgetId);
        //views.setTextViewText(R.id.appwidget_recipe_name, collection.getRecipe());

        //mAdapter = new ShoppingListAdapter(mContext, collection, mRecipeId);
        //mViews.setRemoteAdapter(R.id.lv_shopping_list, new Intent());
    }

    private static void showErrorMessage() {
        Log.d("BakingApp", "Update shopping list with NO DATA.");
    }
}

