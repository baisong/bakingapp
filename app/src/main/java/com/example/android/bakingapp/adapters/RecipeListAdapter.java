package com.example.android.bakingapp.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Schema;
import com.example.android.bakingapp.tools.RecipeRecordCollection;
import com.squareup.picasso.Picasso;

public class RecipeListAdapter extends BaseAdapter {

    private Context mContext;
    //private List<String> mRecipeNames;
    private RecipeRecordCollection mRecipeData;
    private LayoutInflater mInflater;

    public RecipeListAdapter(Context context, RecipeRecordCollection data) {
        mContext = context;
        mRecipeData = data;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mRecipeData.getCount();
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
        LinearLayout rootView = (LinearLayout) mInflater.inflate(R.layout.item_recipe, null);
        TextView recipeName = (TextView) rootView.findViewById(R.id.tv_recipe_name);
        ImageView recipePic = (ImageView) rootView.findViewById(R.id.iv_recipe_pic);

        ContentValues recipe = mRecipeData.getRecipe(position);
        Log.d("BakingApp", "Loading card...");
        Log.d("BakingApp", recipe.toString());
        recipeName.setText(recipe.getAsString(Schema.RECIPE_NAME));
        String imageUrl = recipe.getAsString(Schema.RECIPE_IMAGE_URL);
        if (URLUtil.isValidUrl(imageUrl)) {
            Picasso.with(rootView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                    .into(recipePic);
        }
        return rootView;
    }
}
