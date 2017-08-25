package com.example.android.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.bakingapp.activities.WidgetActivity;
import com.example.android.bakingapp.data.Schema;
import com.example.android.bakingapp.tools.NetworkUtils;
import com.example.android.bakingapp.tools.RecipeRecordCollection;
import com.example.android.bakingapp.tools.WidgetListService;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WidgetActivity WidgetActivity}
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    private int mRecipeId;
    private Context mContext;
    private RemoteViews mViews;
    private static String mPackageName;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        //mContext = context;
        logFunc("updateAppWidget", context, appWidgetManager, appWidgetId);

        // Construct the RemoteViews object
        //ContentValues[] ingredients;
        //mPackageName = context.getPackageName();
        // Instruct the widget manager to update the widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);
        views.setTextViewText(R.id.appwidget_recipe_name, "No recipe selected.");
        appWidgetManager.updateAppWidget(appWidgetId,  views);
        new FetchRecipesTask(context, appWidgetManager, appWidgetId).execute();
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

    public static class FetchRecipesTask extends AsyncTask<Void, Void, RecipeRecordCollection> {

        private Context mContext;
        private AppWidgetManager mManager;
        private int mAppWidgetId;
        // Derived/instantiated from initial variables.
        private int mRecipeId;
        private RemoteViews mViews;

        public FetchRecipesTask(Context context, AppWidgetManager manager, int appWidgetId) {
            logFunc("new FetchRecipesTask", context, manager, appWidgetId);
            mContext = context;
            mManager = manager;
            mAppWidgetId = appWidgetId;
            mViews = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_provider);
            mRecipeId = WidgetActivity.loadRecipePref(context, appWidgetId);
            String recipeName = "Recipe ID " + mRecipeId + " is not valid.";
            if (Schema.isValidRecipe(mRecipeId)) {
                recipeName = "Loading recipe " + String.valueOf(mRecipeId) + "...";
            }
            mViews.setTextViewText(R.id.appwidget_recipe_name, recipeName);
        }

        @Override
        protected RecipeRecordCollection doInBackground(Void... voids) {
            if (Schema.isValidRecipe(mRecipeId)) {
                log("Fetch widget LOAD: " + String.valueOf(mRecipeId));
                RecipeRecordCollection collection = NetworkUtils.fetch();
                //ContentValues[] ingredients = collection.getIngredients(recipeId);
                return collection;
            }
            else {
                log("Fetch widget SKIP: " + String.valueOf(mRecipeId));
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
        protected void onPostExecute(RecipeRecordCollection data) {
            logFunc("onPostExecute", data);
            if (data == null) {
                showErrorMessage();
                return;
            }
            String recipeName = data.getRecipe(mRecipeId).getAsString(Schema.RECIPE_NAME);
            String[] ingredientArray = buildIngredientArray(data);
            String ingredientCount = String.valueOf(ingredientArray.length + " ingredients");
            String ingredientListString = TextUtils.join(Schema.INGREDIENTS_EXTRA_SEPARATOR,ingredientArray);
            mViews.setTextViewText(R.id.appwidget_recipe_name, recipeName);
            mViews.setTextViewText(R.id.appwidget_ingredient_count, ingredientCount);

            // @TODO Simplify this code that calls the remote list factory thing.
            // https://developer.android.com/guide/topics/appwidgets/index.html#collections
            Intent intent = new Intent(mContext, WidgetListService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            intent.putExtra(Schema.RECIPE_ID, mRecipeId);
            intent.putExtra(Schema.INGREDIENTS_EXTRA_KEY, ingredientListString);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            mViews.setRemoteAdapter(R.id.lv_shopping_list, intent);
            mManager.updateAppWidget(mAppWidgetId, mViews);

        }

        private String[] buildIngredientArray(RecipeRecordCollection data) {
            /*
            if (!Schema.isValidRecipe(mRecipeId)) {
                return "No ingredients for recipe (" + String.valueOf(mRecipeId) + ")";
            }*/
            //String ingredientStrings = "";
            ContentValues[] ingredientRecords = data.getIngredients(mRecipeId);
            String[] ingredientStrings = new String[ingredientRecords.length];
            for (int i = 0; i < ingredientRecords.length; i++) {
                ingredientStrings[i] = buildIngredientString(ingredientRecords[i]);
            }
            return ingredientStrings;
        }

        private String buildIngredientString(ContentValues ingredient) {
            String[] parts = new String[]{
                    ingredient.getAsString(Schema.INGREDIENT_QUANTITY),
            ingredient.getAsString(Schema.INGREDIENT_MEASURE),
            ingredient.getAsString(Schema.INGREDIENT_NAME)
            };
            return TextUtils.join(Schema.INGREDIENT_PART_SEPARATOR, parts);
        }
    }

    private static void updateShoppingList(RecipeRecordCollection collection) {
        Log.d("BakingApp", "Update shopping list with data.");
        //RemoteViews views = new RemoteViews(mPackageName, R.layout.recipe_widget_provider);
        //int recipeId = WidgetActivity.loadRecipePref(context, appWidgetId);
        //views.setTextViewText(R.id.appwidget_recipe_name, collection.getRecipe());

        //mAdapter = new ShoppingListAdapter(mContext, collection, mRecipeId);
        //mViews.setRemoteAdapter(R.id.lv_shopping_list, new Intent());
    }

    private static void showErrorMessage() {
        Log.d("BakingApp", "Update shopping list with NO DATA.");
    }

    private final static String LOG_TAG = "BakingApp [WID]{prov}";
    private static void log(String message) {
        Log.d(LOG_TAG, message);
    }

    private static void logFunc(String functionName, Object... args) {
        String message = functionName + "(";
        for (int i = 0; i < args.length; i++) {
            boolean isLast = (i == (args.length - 1));
            boolean isFirst = (i == 0);
            if (!isFirst) message += ", ";
            message += String.valueOf(args[i]);
            if (isLast) message += ")";
        }
        log(message);
    }
}

