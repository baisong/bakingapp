package com.example.android.bakingapp.adapters;

import android.content.ContentValues;
import android.content.Context;
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
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WidgetRecipeAdapter extends BaseAdapter {

    private Context mContext;
    private ContentValues[] mItems;
    private LayoutInflater mInflater;

    @BindView(R.id.tv_recipe_name)
    TextView mName;
    @BindView(R.id.iv_recipe_pic)
    ImageView mPic;

    public WidgetRecipeAdapter(Context context, ContentValues[] items) {
        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mItems == null) return 0;
        return mItems.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setItems(ContentValues[] items) {
        mItems = items;
        this.notifyDataSetChanged();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootView = (LinearLayout) mInflater.inflate(R.layout.item_recipe, parent, false);
        ButterKnife.bind(this, rootView);
        mName = (TextView) rootView.findViewById(R.id.tv_recipe_name);
        mPic = (ImageView) rootView.findViewById(R.id.iv_recipe_pic);
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
