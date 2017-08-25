package com.example.android.bakingapp.tools;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Schema;

import butterknife.BindView;

public class WidgetListService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        logFunc("onGetViewFactory", intent);
        Bundle extras = intent.getExtras();
        int recipeId = extras.getInt(Schema.RECIPE_ID, -1);
        if (!Schema.isValidRecipe(recipeId)) {
            log("1. NULL");
            return null;
        }
        logFunc("onGetViewFactory: recipeId = " + String.valueOf(recipeId));


        // RecipeRecordCollection data = (RecipeRecordCollection) extras.getSerializable(State.RECIPE_DATA);
        String data = extras.getString(Schema.INGREDIENTS_EXTRA_KEY);
        if (data == null) {
            log("2. NULL");
            return null;
        }
        logFunc("onGetViewFactory: data = " + data);
        String[] ingredientStrings = TextUtils.split(data, Schema.INGREDIENTS_EXTRA_SEPARATOR);
        if (ingredientStrings.length < 1) {
            log("3. NULL");
            return null;
        }
        logFunc("onGetViewFactory: ingredients = " + TextUtils.join(", ", ingredientStrings));

        //logFunc("onGetViewFactory", recipeId, data.getCount());
        int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        return new ListRemoteViewsFactory(getApplicationContext(), ingredientStrings, recipeId);
    }

    class ListRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private String[] mIngredientStrings;
        private LayoutInflater mInflater;
        private int mRecipeId;

        @BindView(R.id.tv_ingredient_name)
        TextView mName;
        @BindView(R.id.tv_quantity)
        TextView mQuantity;
        @BindView(R.id.tv_measure)
        TextView mMeasure;


        public ListRemoteViewsFactory(Context context, String[] ingredientStrings, int recipeId) {
            super();
            logFunc("ListRemoteViewsFactory", context, ingredientStrings, recipeId);
            mContext = context;
            mIngredientStrings = ingredientStrings;
            mRecipeId = recipeId;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public int getCount() {
            if (mIngredientStrings == null) return 0;
            return mIngredientStrings.length;
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            logFunc("getViewAt",position, mContext, mRecipeId, mIngredientStrings[position]);
            // @TODO Write this list adapter for the widget
            //LinearLayout rootView = (LinearLayout) mInflater.inflate(R.layout.item_shopping_list, null);
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_shopping_list);
            //ButterKnife.bind(this, views);
            //TextView recipeName = (TextView) rootView.findViewById(R.id.tv_recipe_name);
            //ImageView recipePic = (ImageView) rootView.findViewById(R.id.iv_recipe_pic);

            String ingredient = mIngredientStrings[position];
            views.setTextViewText(R.id.tv_ingredient_name, ingredient);
            views.setTextViewText(R.id.tv_ingredient_num, String.valueOf(position + 1) + ".");

            return views;
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

    }

    private final static String LOG_TAG = "BakingApp [WID]{serv}";
    private void log(String message) {
        Log.d(LOG_TAG, message);
    }
    private void logFunc(String functionName, Object... args) {
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
