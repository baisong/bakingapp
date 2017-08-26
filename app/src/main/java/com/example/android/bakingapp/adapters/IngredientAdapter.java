package com.example.android.bakingapp.adapters;

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Schema;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for Main Activity RecyclerView displaying list of recipe ingredients.
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientHolder> {

    private ContentValues[] mItems;

    /**
     * Create an Adapter.
     */
    public IngredientAdapter() {
        super();
    }

    /**
     * Cache the children views for a item_ingredient view.
     */
    public class IngredientHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_ingredient_name)
        TextView mName;
        @BindView(R.id.tv_quantity)
        TextView mQuantity;
        @BindView(R.id.tv_measure)
        TextView mMeasure;

        /**
         * Sets up the item view.
         */
        public IngredientHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * Return the number of items.
     */
    @Override
    public int getItemCount() {
        if (mItems == null) return 0;
        return mItems.length;
    }

    /**
     * Initialize each visible view holder on the screen.
     */
    @Override
    public IngredientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_ingredient, parent, false);

        return new IngredientHolder(view);
    }

    /**
     * Display the layout bound to each visible view.
     */
    @Override
    public void onBindViewHolder(IngredientHolder holder, int position) {
        ContentValues itemValues = mItems[position];
        holder.mName.setText(itemValues.getAsString(Schema.INGREDIENT_NAME));
        holder.mQuantity.setText(itemValues.getAsString(Schema.INGREDIENT_QUANTITY));
        holder.mMeasure.setText(itemValues.getAsString(Schema.INGREDIENT_MEASURE));
    }

    /**
     * Refresh the data held in the adapter.
     */
    public void setIngredientsData(ContentValues[] items) {
        mItems = items;
        notifyDataSetChanged();
    }
}
