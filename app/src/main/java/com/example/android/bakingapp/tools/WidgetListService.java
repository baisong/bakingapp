package com.example.android.bakingapp.tools;

import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Schema;

import butterknife.BindView;

/**
 * Created by oren on 8/24/17.
 */

public class WidgetListService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Bundle extras = intent.getExtras();
        int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        return new ListRemoteViewsFactory(getApplicationContext(), null, 0);
    }

    class ListRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private RecipeRecordCollection mRecipeData;
        private LayoutInflater mInflater;
        private int mRecipeId;

        @BindView(R.id.tv_ingredient_name)
        TextView mName;
        @BindView(R.id.tv_quantity)
        TextView mQuantity;
        @BindView(R.id.tv_measure)
        TextView mMeasure;


        public ListRemoteViewsFactory(Context context, RecipeRecordCollection data, int recipeId) {
            mContext = context;
            mRecipeData = data;
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
            if (mRecipeData == null) return 0;
            return mRecipeData.getCount();
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            // @TODO Write this list adapter for the widget
            //LinearLayout rootView = (LinearLayout) mInflater.inflate(R.layout.item_shopping_list, null);
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_provider);
            //ButterKnife.bind(this, views);
            //TextView recipeName = (TextView) rootView.findViewById(R.id.tv_recipe_name);
            //ImageView recipePic = (ImageView) rootView.findViewById(R.id.iv_recipe_pic);

            ContentValues ingredient = mRecipeData.getIngredient(mRecipeId, position);
            Log.d("BakingApp", "Loading ingredient...");
            Log.d("BakingApp", ingredient.toString());
            mName.setText(ingredient.getAsString(Schema.INGREDIENT_NAME));
            mQuantity.setText(ingredient.getAsString(Schema.INGREDIENT_QUANTITY));
            mMeasure.setText(ingredient.getAsString(Schema.INGREDIENT_MEASURE));

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
}
