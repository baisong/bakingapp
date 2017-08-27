package com.example.android.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Schema;

/**
 * Service to provide a ListView adapter to widgets using a RemoteViewsFactory.
 * <p>
 * About Widget ListViews: https://developer.android.com/reference/android/widget/ListView.html
 */
public class RemoteViewsListViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Bundle extras = intent.getExtras();
        String data = extras.getString(Schema.INGREDIENTS_EXTRA_KEY);
        if (data == null) {
            return null;
        }
        String[] ingredientStrings = TextUtils.split(data, Schema.INGREDIENTS_EXTRA_SEPARATOR);
        if (ingredientStrings.length < 1) {
            return null;
        }

        return new ListRemoteViewsFactory(getApplicationContext(), ingredientStrings);
    }

    class ListRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private String[] mIngredientStrings;
        TextView mName;
        TextView mQuantity;
        TextView mMeasure;

        private ListRemoteViewsFactory(Context context, String[] ingredientStrings) {
            super();
            mContext = context;
            mIngredientStrings = ingredientStrings;
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

        /**
         * Updates the RemoteViews object for an ingredient list item in the widget.
         *
         * @param position
         * @return
         */
        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_shopping_list);
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
}
