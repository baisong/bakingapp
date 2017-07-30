package com.example.android.bakingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class RecipeListAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mRecipeNames;
    private LayoutInflater mInflater;

    public RecipeListAdapter(Context context, List<String> recipeNames) {
        mContext = context;
        mRecipeNames = recipeNames;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mRecipeNames.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LinearLayout rootView = (LinearLayout) mInflater.inflate(R.layout.list_item_recipe, null);
        TextView recipeName = (TextView) rootView.findViewById(R.id.tv_recipe_name);
        recipeName.setText(mRecipeNames.get(position));
        return rootView;
    }
}
