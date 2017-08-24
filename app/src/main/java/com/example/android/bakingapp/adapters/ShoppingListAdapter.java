package com.example.android.bakingapp.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Schema;
import com.example.android.bakingapp.tools.RecipeRecordCollection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingListAdapter extends BaseAdapter {

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


    public ShoppingListAdapter(Context context, RecipeRecordCollection data, int recipeId) {
        mContext = context;
        mRecipeData = data;
        mRecipeId = recipeId;
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
        LinearLayout rootView = (LinearLayout) mInflater.inflate(R.layout.item_shopping_list, null);
        ButterKnife.bind(this, rootView);
        //TextView recipeName = (TextView) rootView.findViewById(R.id.tv_recipe_name);
        //ImageView recipePic = (ImageView) rootView.findViewById(R.id.iv_recipe_pic);

        ContentValues ingredient = mRecipeData.getIngredient(mRecipeId, position);
        Log.d("BakingApp", "Loading ingredient...");
        Log.d("BakingApp", ingredient.toString());
        mName.setText(ingredient.getAsString(Schema.INGREDIENT_NAME));
        mQuantity.setText(ingredient.getAsString(Schema.INGREDIENT_QUANTITY));
        mMeasure.setText(ingredient.getAsString(Schema.INGREDIENT_MEASURE));

        return rootView;
    }
}
