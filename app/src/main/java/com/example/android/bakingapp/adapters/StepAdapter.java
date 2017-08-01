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
import com.example.android.bakingapp.data.BakingAppSchema;
import com.squareup.picasso.Picasso;

public class StepAdapter extends BaseAdapter {

    private Context mContext;
    //private List<String> mRecipeNames;
    private ContentValues[] mItems;
    private ImageView mThumbnail;
    private TextView mTitle;
    private TextView mBody;
    private TextView mVideoURL;
    private LayoutInflater mInflater;

    public StepAdapter(Context context, ContentValues[] items) {
        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
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
        notifyDataSetChanged();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LinearLayout rootView = (LinearLayout) mInflater.inflate(R.layout.item_step, null);
        mTitle = (TextView) rootView.findViewById(R.id.tv_step_title);
        mThumbnail = (ImageView) rootView.findViewById(R.id.iv_thumbnail);
        mVideoURL = (TextView) rootView.findViewById(R.id.tv_video_url);
        mBody = (TextView) rootView.findViewById(R.id.tv_step_body);

        ContentValues step = mItems[position];
        Log.d("BakingApp", "Loading card...");
        Log.d("BakingApp", step.toString());
        mTitle.setText(step.getAsString(BakingAppSchema.STEP_TITLE));
        mBody.setText(step.getAsString(BakingAppSchema.STEP_BODY));
        String videoUrl = step.getAsString(BakingAppSchema.STEP_VIDEO_URL);
        if (URLUtil.isValidUrl(videoUrl)) {
            mVideoURL.setText(videoUrl);
        }
        else {
            mVideoURL.setVisibility(View.GONE);
        }
        String imageUrl = step.getAsString(BakingAppSchema.STEP_IMAGE_URL);
        if (URLUtil.isValidUrl(imageUrl)) {
            Picasso.with(rootView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                    .into(mThumbnail);
        }
        else {
            mThumbnail.setVisibility(View.GONE);
        }
        return rootView;
    }
}
