package com.example.android.bakingapp.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.Schema;
import com.example.android.bakingapp.tools.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for Main Activity RecyclerView displaying list of clickable recipe steps.
 */
public class StepRecyclerAdapter extends RecyclerView.Adapter<StepRecyclerAdapter.StepHolder> {

    private ContentValues[] mItems;

    /**
     * Creates an Adapter.
     */
    public StepRecyclerAdapter(Context context) {
        super();
    }

    /**
     * Cache the children views for a item_ingredient view.
     */
    public class StepHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_step_title)
        TextView mName;
        @BindView(R.id.tv_step_list_num)
        TextView mListNum;
        @BindView(R.id.ll_step_holder)
        LinearLayout mHolder;
        @BindView(R.id.btn_video_icon)
        Button mVideoIcon;

        /**
         * Set up the item view.
         */
        public StepHolder(View view) {
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
    public StepHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_step, parent, false);
        return new StepHolder(view);
    }

    /**
     * Display the layout bound to each visible view.
     */
    @Override
    public void onBindViewHolder(StepHolder holder, final int position) {
        ContentValues step = mItems[position];
        String videoUrl = step.getAsString(Schema.STEP_VIDEO_URL);
        if (URLUtil.isValidUrl(videoUrl) && NetworkUtils.isVideoFile(videoUrl)) {
            holder.mVideoIcon.setVisibility(View.VISIBLE);
        } else {
            holder.mVideoIcon.setVisibility(View.GONE);
        }
        holder.mName.setText(step.getAsString(Schema.STEP_TITLE));
        holder.mListNum.setText(String.valueOf(position + 1));
    }

    /**
     * Refresh the data held in the adapter.
     */
    public void setStepsData(ContentValues[] items) {
        mItems = items;
        notifyDataSetChanged();
    }
}
