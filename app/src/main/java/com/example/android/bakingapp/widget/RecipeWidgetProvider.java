package com.example.android.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.data.Schema;
import com.example.android.bakingapp.tools.NetworkUtils;

/**
 * Implementation of App Widget functionality.
 * <p>
 * App Widget Configuration implemented in {@link WidgetConfigureActivity WidgetConfigureActivity}
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    private final static String WIDGET_DEFAULT_RECIPE_NAME = "No recipe selected.";
    private final static String WIDGET_INVALID_RECIPE_PREFIX = "Invalid Recipe ID: ";
    private final static String WIDGET_LOADING_RECIPE_PREFIX = "Loading Recipe: ";
    private final static String WIDGET_LOG_NO_DATA = "Widget error: no data.";
    private final static String INGREDIENTS_SUFFIX = " ingredients";

    public static void updateAppWidget(Context context, AppWidgetManager manager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);
        views.setTextViewText(R.id.appwidget_recipe_name, WIDGET_DEFAULT_RECIPE_NAME);
        manager.updateAppWidget(appWidgetId, views);
        new FetchRecipesTask(context, manager, appWidgetId).execute();
    }

    /**
     * Update all active widgets.
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * Delete preferences for deleted widgets.
     *
     * @param context
     * @param appWidgetIds
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            WidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    public static class FetchRecipesTask extends AsyncTask<Void, Void, RecipeData> {

        private Context mContext;
        private AppWidgetManager mManager;
        private int mAppWidgetId;
        private int mRecipeId;
        private RemoteViews mViews;

        public FetchRecipesTask(Context context, AppWidgetManager manager, int appWidgetId) {
            mContext = context;
            mManager = manager;
            mAppWidgetId = appWidgetId;
            mViews = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_provider);
            mRecipeId = WidgetConfigureActivity.loadRecipePref(context, appWidgetId);
            String recipeName = WIDGET_INVALID_RECIPE_PREFIX + mRecipeId;
            if (Schema.isValidRecipe(mRecipeId)) {
                recipeName = WIDGET_LOADING_RECIPE_PREFIX + String.valueOf(mRecipeId);
            }
            mViews.setTextViewText(R.id.appwidget_recipe_name, recipeName);
        }

        @Override
        protected RecipeData doInBackground(Void... voids) {
            if (Schema.isValidRecipe(mRecipeId)) {
                return NetworkUtils.fetch();
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
        protected void onPostExecute(RecipeData data) {
            if (data == null) {
                Log.d("BakingApp", WIDGET_LOG_NO_DATA);
                return;
            }
            String recipeName = data.getRecipe(mRecipeId).getAsString(Schema.RECIPE_NAME);
            String[] ingredientArray = buildIngredientArray(data);
            String ingredientCount = String.valueOf(ingredientArray.length + INGREDIENTS_SUFFIX);
            String ingredientListString = TextUtils.join(Schema.INGREDIENTS_EXTRA_SEPARATOR, ingredientArray);
            mViews.setTextViewText(R.id.appwidget_recipe_name, recipeName);
            mViews.setTextViewText(R.id.appwidget_ingredient_count, ingredientCount);

            Intent intent = new Intent(mContext, RemoteViewsListViewService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            intent.putExtra(Schema.INGREDIENTS_EXTRA_KEY, ingredientListString);

            mViews.setRemoteAdapter(R.id.lv_shopping_list, intent);
            mManager.updateAppWidget(mAppWidgetId, mViews);

        }

        private String[] buildIngredientArray(RecipeData data) {
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
}

