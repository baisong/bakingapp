package com.example.android.bakingapp.adapters;

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.BakingAppSchema;

import butterknife.ButterKnife;

public class StepRecyclerAdapter extends RecyclerView.Adapter<StepRecyclerAdapter.StepHolder> {

    private ContentValues[] mItems;

    /**
     * Creates an Adapter.
     */
    public StepRecyclerAdapter() {
        super();
    }

    /**
     * Cache of the children views for a item_ingredient view.
     */
    public class StepHolder extends RecyclerView.ViewHolder {
        // @TODO Use butterknife
        //@BindView(R.id.tv_step_name)
        TextView mName;

        /**
         * Sets up the item view.
         */
        public StepHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            // @TODO Use butterknife
            // mName = (TextView) view.findViewById(R.id.tv_name);
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
        ContentValues itemValues = mItems[position];
        holder.mName.setText(itemValues.getAsString(BakingAppSchema.STEP_TITLE));
    }

    /**
     * Refreshes the data held in the adapter.
     */
    public void setStepsData(ContentValues[] items) {
        mItems = items;
        notifyDataSetChanged();
    }
}
