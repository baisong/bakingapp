package com.example.android.bakingapp.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Schema;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for Select Recipe Activity GridView displaying list of recipe cards.
 */
public class SelectRecipeAdapter extends BaseAdapter {

    private ContentValues[] mItems;
    private LayoutInflater mInflater;

    @BindView(R.id.tv_recipe_name)
    TextView mName;
    @BindView(R.id.iv_recipe_pic)
    ImageView mPic;

    /**
     * Set up the adapter.
     *
     * @param context
     * @param items
     */
    public SelectRecipeAdapter(Context context, ContentValues[] items) {
        mItems = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Return the number of adapter items.
     *
     * @return
     */
    @Override
    public int getCount() {
        if (mItems == null) return 0;
        return mItems.length;
    }

    /**
     * Return the item at a given position.
     *
     * @param i
     * @return
     */
    @Override
    public Object getItem(int i) {
        return null;
    }

    /**
     * Return the item ID at a given position.
     *
     * @param i
     * @return
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Set the items.
     *
     * @param items
     */
    public void setItems(ContentValues[] items) {
        mItems = items;
        this.notifyDataSetChanged();
    }

    /**
     * Interface implemented by RecipeSelectActivity.
     */
    public interface OnRecipeClickListener {
        void onRecipeSelected(int recipe);
    }

    /**
     * Get the view to hold the item at the given position.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootView;
        if (convertView != null) {
            rootView = convertView;
        } else {
            rootView = mInflater.inflate(R.layout.item_recipe, parent, false);
        }
        ButterKnife.bind(this, rootView);

        ContentValues recipe = mItems[position];
        mName.setText(recipe.getAsString(Schema.RECIPE_NAME));
        String imageUrl = recipe.getAsString(Schema.RECIPE_IMAGE_URL);
        if (URLUtil.isValidUrl(imageUrl)) {
            Picasso.with(rootView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                    .into(mPic);
        }

        return rootView;
    }
}
