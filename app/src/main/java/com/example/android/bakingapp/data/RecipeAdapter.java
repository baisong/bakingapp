package com.example.android.bakingapp.data;

/**
 * Adapters provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
 */

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.tools.RecipeRecordCollection;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MovieAdapterViewHolder> {

    private RecipeRecordCollection mCollection;
    private final RecipeAdapterOnClickHandler mClickHandler;

    /**
     * Provides an interface for onClickHandlers to pass along a JSONobject.
     */
    public interface RecipeAdapterOnClickHandler {
        void onClick(ContentValues movieData);
    }

    /**
     * Requires that MovieAdapter instances provide a clickHandler.
     */
    public RecipeAdapter(RecipeAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     *  Describes a movie item view and it's metadata.
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //public final ImageView mPosterImageView;

        /**
         * Sets up the poster ImageView and onClickHandler for launching the DetailActivity intent.
         */
        public MovieAdapterViewHolder(View view) {
            super(view);
            //mPosterImageView = (ImageView) view.findViewById(R.id.iv_tile_poster);
            view.setOnClickListener(this);
        }

        /**
         * Passes the specified movie's JSONobject of metadata to the onClickHandler.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            ContentValues values = mCollection.getRecipe(adapterPosition);
            mClickHandler.onClick(values);
        }
    }

    /**
     * Inflates the movie_tile view for each item.
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup group, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(group.getContext());

        return new MovieAdapterViewHolder(inflater.inflate(R.layout.item_recipe, group, false));
    }

    /**
     * Uses Picasso to load the poster into the item view.
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        //String posterImageUrl = mCollection.moviePosters[position];
        //Picasso.with(movieAdapterViewHolder.mPosterImageView.getContext()).load(posterImageUrl).into(movieAdapterViewHolder.mPosterImageView);
    }

    /**
     * Returns the current item count.
     */
    @Override
    public int getItemCount() {
        if (mCollection == null) return 0;
        return mCollection.getCount();
    }

    /**
     * Allows for new data to be loaded into the RecyclerView.
     */
    public void setMovieData(RecipeRecordCollection collection) {
        mCollection = collection;
        notifyDataSetChanged();
    }
}