package com.example.android.bakingapp.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.BakingAppSchema;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepRecyclerAdapter extends RecyclerView.Adapter<StepRecyclerAdapter.StepHolder> {

    private ContentValues[] mItems;
    private LinearLayout mHolder;
    /*
    @BindView(R.id.tv_step_title) TextView mName;
    @BindView(R.id.iv_thumbnail) ImageView mThumbnail;
    @BindView(R.id.tv_video_url) TextView mVideoURL;
    @BindView(R.id.tv_step_body) TextView mBody;
    */

    /**
     * Creates an Adapter.
     */
    public StepRecyclerAdapter(Context context) {
        super();
    }

    /**
     * Cache of the children views for a item_ingredient view.
     */
    public class StepHolder extends RecyclerView.ViewHolder {
        // @TODO Use butterknife

        @BindView(R.id.tv_step_title) TextView mName;
        @BindView(R.id.ll_step_holder) LinearLayout mHolder;

        /**
         * Sets up the item view.
         */
        public StepHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            // @TODO Use butterknife
            /*

    @BindView(R.id.tv_step_title) TextView mName;
    @BindView(R.id.iv_thumbnail) ImageView mThumbnail;
    @BindView(R.id.tv_video_url) TextView mVideoURL;
    @BindView(R.id.tv_step_body) TextView mBody;
             */

            mName = (TextView) view.findViewById(R.id.tv_step_title);
            mHolder = (LinearLayout) view.findViewById(R.id.ll_step_holder);
            //@BindView(R.id.iv_thumbnail) ImageView mThumbnail;
            //@BindView(R.id.tv_video_url) TextView mVideoURL;
            //@BindView(R.id.tv_step_body) TextView mBody;
        }
    }

    /**
     * Returns the number of items.
     */
    @Override
    public int getItemCount() {
        if (mItems == null) return 0;
        return mItems.length;
    }

    /**
     * Initializes each visible view holder on the screen.
     */
    @Override
    public StepHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_step, parent, false);

        return new StepHolder(view);
    }

    /**
     * Displays the layout bound to each visible view.
     */
    @Override
    public void onBindViewHolder(StepHolder holder, int position) {
        ContentValues step = mItems[position];
        holder.mName.setText(step.getAsString(BakingAppSchema.STEP_TITLE));
        holder.mHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        /*
        holder.mBody.setText(step.getAsString(BakingAppSchema.STEP_BODY));
        String videoUrl = step.getAsString(BakingAppSchema.STEP_VIDEO_URL);
        if (URLUtil.isValidUrl(videoUrl)) {
            holder.mVideoURL.setText(videoUrl);
        }
        else {
            holder.mVideoURL.setVisibility(View.GONE);
        }
        String imageUrl = step.getAsString(BakingAppSchema.STEP_IMAGE_URL);
        if (URLUtil.isValidUrl(imageUrl)) {
            Picasso.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                    .into(holder.mThumbnail);
        }
        else {
            holder.mThumbnail.setVisibility(View.GONE);
        }
        holder.mName.setText(step.getAsString(BakingAppSchema.STEP_TITLE));
        */
    }

    /**
     * Refreshes the data held in the adapter.
     */
    public void setStepsData(ContentValues[] items) {
        mItems = items;
        notifyDataSetChanged();
    }
}
