package com.example.android.bakingapp.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.data.Schema;
import com.example.android.bakingapp.data.State;
import com.example.android.bakingapp.tools.NetworkUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.bakingapp.data.State.CURRENT_RECIPE_INDEX;
import static com.example.android.bakingapp.data.State.CURRENT_STEP_INDEX;
import static com.example.android.bakingapp.data.State.RECIPE_DATA;

/**
 * View of a single recipe step, with forward-backward navigation and optional ExoPlayer media.
 */
public class DetailFragment extends Fragment {

    private ContentValues[] mSteps;
    private ContentValues mStep;
    private Context mContext;
    private RecipeData mRecipeData;
    private int mCurrentStep;
    private int mCurrentRecipe;
    private SimpleExoPlayer mExoPlayer;

    @BindView(R.id.tv_step_title)
    TextView mTitle;
    @BindView(R.id.tv_step_body)
    TextView mBody;
    @BindView(R.id.iv_thumbnail)
    ImageView mThumbnail;
    @BindView(R.id.btn_prev_step)
    Button mBackStep;
    @BindView(R.id.btn_next_step)
    Button mNextStep;
    @BindView(R.id.tv_current_step)
    TextView mCurrentStepNum;
    @BindView(R.id.tv_total_steps)
    TextView mTotalSteps;
    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;

    /**
     * @TODO See code formatting standards:
     * https://google.github.io/styleguide/javaguide.html
     * https://source.android.com/source/code-style
     */
    public DetailFragment() {
    }

    /**
     * Static factory method to set up a new instance.
     *
     * More: http://www.androiddesignpatterns.com/2012/05/using-newinstance-to-instantiate.html
     *
     * @param data
     * @param recipe
     * @param step
     * @return
     */
    public static DetailFragment newInstance(RecipeData data, int recipe, int step) {
        DetailFragment f = new DetailFragment();
        f.setStep(data, recipe, step);
        return f;
    }

    /**
     * Fetch the current recipe step from SharedPreferences, initialize click handlers, draw view.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getRecipeStepState();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        mContext = rootView.getContext();
        mBackStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateBack();
            }
        });
        mNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateNext();
            }
        });

        try {
            setStep(mRecipeData, mCurrentRecipe, mCurrentStep);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
        if (mStep == null) {
            return rootView;
        }
        updateStepView();
        return rootView;
    }

    /**
     * Fetch current recipe step and data from savedInstanceState bundle if exists.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            getRecipeStepState();
            return;
        }
        setNewStepState(savedInstanceState.getInt(CURRENT_RECIPE_INDEX), savedInstanceState.getInt(CURRENT_STEP_INDEX));
        mRecipeData = (RecipeData) savedInstanceState.getSerializable(RECIPE_DATA);
    }

    /**
     * Persist state variables and recipe data.
     *
     * @param currentState
     */
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putInt(CURRENT_RECIPE_INDEX, mCurrentRecipe);
        currentState.putInt(CURRENT_STEP_INDEX, mCurrentStep);
        currentState.putSerializable(RECIPE_DATA, mRecipeData);
        setRecipeStepState(mCurrentRecipe, mCurrentStep);
        super.onSaveInstanceState(currentState);
    }

    /**
     * Release Exoplayer when pausing.
     *
     * @TODO When the app resumes from the background while the video is being played. The video
     * screen freezes. You need to restore the video playback.
     * Have a look at demo, more precisely this file to handle Exoplayer properly.
     * https://github.com/google/ExoPlayer/tree/release-v2/demo
     * https://github.com/google/ExoPlayer/blob/release-v2/demo/src/main/java/com/google/android/exoplayer2/demo/PlayerActivity.java
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    /**
     * Helper function for MainActivity and DetailActivity.
     *
     * @param data
     */
    public void setRecipeData(RecipeData data) {
        mRecipeData = data;
    }

    /**
     * Update member variables and SharedPreferences values for current recipe step.
     *
     * @param recipe
     * @param step
     */
    public void setNewStepState(int recipe, int step) {
        boolean newRecipe = (recipe != mCurrentRecipe);
        mCurrentRecipe = recipe;
        mCurrentStep = step;
        setRecipeStepState(mCurrentRecipe, mCurrentStep);
        if (newRecipe && mRecipeData != null && mRecipeData.getCount() > 0) {
            mSteps = mRecipeData.getSteps(mCurrentRecipe);
        }
    }

    /**
     * Validate and update active step ContentValues object based on data and recipe step position.
     *
     * @param data
     * @param recipe
     * @param step
     */
    public void setStep(RecipeData data, int recipe, int step) {
        if (data == null) {
            throw new UnsupportedOperationException(getString(R.string.error_recipe_data_null));
        }
        if (data.getCount() < 1) {
            if (data.hasData()) data.reload();
            if (data.getCount() < 1) {
                throw new UnsupportedOperationException(getString(R.string.error_recipe_data_empty));
            }
        }
        mRecipeData = data;
        if (!Schema.isValidRecipe(recipe)) {
            throw new UnsupportedOperationException(getString(R.string.error_recipe_out_of_bounds));
        }
        mCurrentRecipe = recipe;
        if (!Schema.isValidStep(step)) {
            throw new UnsupportedOperationException(getString(R.string.error_step_out_of_bounds));
        }
        mCurrentStep = step;
        mSteps = data.getSteps(recipe);
        if (mSteps.length < 1) {
            throw new UnsupportedOperationException("No steps found for recipe " + String.valueOf(recipe));
        }
        if (mCurrentStep >= mSteps.length) {
            throw new UnsupportedOperationException("No step " + String.valueOf(step) + " for recipe " + String.valueOf(recipe));
        }
        mStep = mSteps[mCurrentStep];
    }

    /**
     * Button click callback to navigate backward to previous step.
     */
    public void navigateBack() {
        if (mCurrentStep == 0) {
            // @TODO Snackbar is better.
            // http://www.androidhive.info/2015/09/android-material-design-snackbar-example/
            Toast.makeText(getActivity(), getString(R.string.first_step_toast), Toast.LENGTH_LONG).show();
            return;
        }
        setNewStepState(mCurrentStep - 1);
        releasePlayer();
        updateStepView();
    }

    /**
     * Button click callback to navigate forward to next step.
     */
    public void navigateNext() {
        if ((mCurrentStep + 1) == mSteps.length) {
            Toast.makeText(getActivity(), getString(R.string.last_step_toast), Toast.LENGTH_LONG).show();
            return;
        }
        setNewStepState(mCurrentStep + 1);
        releasePlayer();
        updateStepView();
    }

    /**
     * Update view objects based on the current step, including ExoPlayer and Picasso.
     */
    private void updateStepView() {
        if (mStep == null) {
            return;
        }
        mStep = mSteps[mCurrentStep];
        mCurrentStepNum.setText(String.valueOf(mCurrentStep + 1));
        mTotalSteps.setText(String.valueOf(mSteps.length));
        mTitle.setText(mStep.getAsString(Schema.STEP_TITLE));
        mBody.setText(mStep.getAsString(Schema.STEP_BODY));

        // Handle video with ExoPlayer
        String videoUrl = mStep.getAsString(Schema.STEP_VIDEO_URL);
        if (URLUtil.isValidUrl(videoUrl) && NetworkUtils.isVideoFile(videoUrl)) {
            mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(videoUrl));
        } else {
            mPlayerView.setVisibility(View.GONE);
        }

        // Handle image with Picasso
        String imageUrl = mStep.getAsString(Schema.STEP_IMAGE_URL);
        if (!URLUtil.isValidUrl(imageUrl)) {
            mThumbnail.setVisibility(View.GONE);
            return;
        }

        // Display a placeholder for broken images or unloadable images.
        int placeholder = R.drawable.ic_photo_size_select_actual_black_24dp;
        mThumbnail.setVisibility(View.VISIBLE);
        if (!NetworkUtils.isImageFile(imageUrl)) {
            Log.w("BakingApp", "Found non-image file in thumbnail field: " + imageUrl);
            mThumbnail.setImageResource(placeholder);
            return;
        }

        // Load the thumbnail image from the JSON property with the placeholder as a loading image.
        Picasso.with(mContext).load(imageUrl).placeholder(placeholder).into(mThumbnail);
    }

    /**
     * Helper function to set up ExoPlayer with validated video URI.
     *
     * @param videoUri
     */
    public void initializePlayer(Uri videoUri) {
        if (getPlayingState()) {
            releasePlayer();
        }
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
        }
        String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_label));
        MediaSource mediaSource = new ExtractorMediaSource(videoUri,
                new DefaultDataSourceFactory(getContext(), userAgent),
                new DefaultExtractorsFactory(),
                null,
                null
        );
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
        setPlayingState(true);
    }

    /**
     * Release ExoPlayer.
     */
    public void releasePlayer() {
        setPlayingState(false);
        if (mExoPlayer == null) {
            return;
        }
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    /**
     * Helper function returns whether ExoPlayer is currently playing from SharedPreferences.
     *
     * @return
     */
    boolean getPlayingState() {
        return State.getInstance(getContext()).getBoolean(State.Key.IS_PLAYING);
    }

    /**
     * Helper function to update whether ExoPlayer is currently playing to SharedPreferences.
     *
     * @param newValue
     */
    private void setPlayingState(boolean newValue) {
        State.getInstance().put(State.Key.IS_PLAYING, newValue);
    }

    /**
     * Helper function for updating which step of the current recipe to SharedPreferences.
     *
     * @param newStep
     */
    public void setNewStepState(int newStep) {
        mCurrentStep = newStep;
        State.getInstance(getContext()).put(State.Key.ACTIVE_STEP_INT, mCurrentStep);
    }

    /**
     * Helper function to update both the recipe and step currently displayed to SharedPreferences.
     *
     * @param recipe
     * @param step
     */
    private void setRecipeStepState(int recipe, int step) {
        State.getInstance(getContext()).put(State.Key.ACTIVE_RECIPE_INT, recipe);
        State.getInstance().put(State.Key.ACTIVE_STEP_INT, step);
    }

    /**
     * Helper function to load current recipe step from SharedPreferences into member variables.
     */
    private void getRecipeStepState() {
        try {
            mCurrentRecipe = State.getInstance(getContext()).getInt(State.Key.ACTIVE_RECIPE_INT);
            mCurrentStep = State.getInstance().getInt(State.Key.ACTIVE_STEP_INT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
