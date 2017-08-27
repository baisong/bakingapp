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

public class DetailFragment extends Fragment {

    private ContentValues[] mSteps;
    private ContentValues mStep;
    private Context mContext;
    private RecipeData mRecipeData;
    private int mCurrentStep;
    private int mCurrentRecipe;
    private SimpleExoPlayer mExoPlayer;

    @BindView(R.id.tv_step_title) TextView mTitle;
    @BindView(R.id.tv_step_body) TextView mBody;
    @BindView(R.id.iv_thumbnail) ImageView mThumbnail;
    @BindView(R.id.btn_prev_step) Button mBackStep;
    @BindView(R.id.btn_next_step) Button mNextStep;
    @BindView(R.id.tv_current_step) TextView mCurrentStepNum;
    @BindView(R.id.tv_total_steps) TextView mTotalSteps;
    @BindView(R.id.playerView) SimpleExoPlayerView mPlayerView;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getCurrentRecipeStep();
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            getCurrentRecipeStep();
            return;
        }
        setCurrentStep(savedInstanceState.getInt(CURRENT_RECIPE_INDEX), savedInstanceState.getInt(CURRENT_STEP_INDEX));
        mRecipeData = (RecipeData) savedInstanceState.getSerializable(RECIPE_DATA);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putInt(CURRENT_RECIPE_INDEX, mCurrentRecipe);
        currentState.putInt(CURRENT_STEP_INDEX, mCurrentStep);
        currentState.putSerializable(RECIPE_DATA, mRecipeData);
        State.getInstance(getContext()).put(State.Key.ACTIVE_RECIPE_INT, mCurrentRecipe);
        State.getInstance().put(State.Key.ACTIVE_STEP_INT, mCurrentStep);
        super.onSaveInstanceState(currentState);
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    public void setRecipeData(RecipeData data) {
        mRecipeData = data;
    }

    public void setCurrentStep(int recipe, int step) {
        boolean newRecipe = (recipe != mCurrentRecipe);
        mCurrentRecipe = recipe;
        mCurrentStep = step;
        State.getInstance(getContext()).put(State.Key.ACTIVE_RECIPE_INT, mCurrentRecipe);
        State.getInstance().put(State.Key.ACTIVE_STEP_INT, mCurrentStep);
        if (newRecipe && mRecipeData != null && mRecipeData.getCount() > 0) {
            refreshSteps();
        }
    }

    public void refreshSteps() {
        mSteps = mRecipeData.getSteps(mCurrentRecipe);
    }

    public void setStep(RecipeData data, int recipe, int step) {
        if (data == null) {
            throw new UnsupportedOperationException("Recipe data is null.");
        }
        if (data.getCount() < 1) {
            if (data.hasData()) data.reload();
            if (data.getCount() < 1) {
                throw new UnsupportedOperationException("Cannot set empty recipe data.");
            }
        }
        mRecipeData = data;
        if (!Schema.isValidRecipe(recipe)) {
            throw new UnsupportedOperationException("This version only supports 4 recipes.");
        }
        mCurrentRecipe = recipe;
        if (!Schema.isValidStep(step)) {
            throw new UnsupportedOperationException("This version only supports 100 steps");
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

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    public void setCurrentStep(int newValue) {
        mCurrentStep = newValue;
        State.getInstance(getContext()).put(State.Key.ACTIVE_STEP_INT, mCurrentStep);
    }

    public void navigateBack() {
        if (mCurrentStep == 0) {
            showToast("First step");
        } else {
            setCurrentStep(mCurrentStep - 1);
            releasePlayer();
            updateStepView();
        }
    }

    public void navigateNext() {
        if ((mCurrentStep + 1) == mSteps.length) {
            showToast("Last step");
        } else {
            setCurrentStep(mCurrentStep + 1);
            releasePlayer();
            updateStepView();
        }
    }

    private void updateStepView() {
        if (mStep == null) {
            return;
        }
        mStep = mSteps[mCurrentStep];
        mCurrentStepNum.setText(String.valueOf(mCurrentStep + 1));
        mTotalSteps.setText(String.valueOf(mSteps.length));
        mTitle.setText(mStep.getAsString(Schema.STEP_TITLE));
        mBody.setText(mStep.getAsString(Schema.STEP_BODY));
        String videoUrl = mStep.getAsString(Schema.STEP_VIDEO_URL);
        if (URLUtil.isValidUrl(videoUrl) && NetworkUtils.isVideoFile(videoUrl)) {
            //mPlayVideoBtn.setTag(videoUrl);
            //mPlayVideoBtn.setVisibility(View.VISIBLE);
            mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(videoUrl));
        } else {
            mPlayerView.setVisibility(View.GONE);
        }
        String imageUrl = mStep.getAsString(Schema.STEP_IMAGE_URL);
        if (URLUtil.isValidUrl(imageUrl)) {
            if (NetworkUtils.isImageFile(imageUrl)) {
                mThumbnail.setVisibility(View.VISIBLE);
                Picasso.with(mContext)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                        .into(mThumbnail);
            } else {
                Log.w("BakingApp", "Found non-image file in thumbnail field: " + imageUrl);
            }
        } else {
            mThumbnail.setVisibility(View.GONE);
        }
    }

    public void initializePlayer(Uri videoUri) {
        if (isPlaying()) {
            releasePlayer();
        }
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
        }
        String userAgent = Util.getUserAgent(getContext(), "BakingApp");
        MediaSource mediaSource = new ExtractorMediaSource(videoUri,
                new DefaultDataSourceFactory(getContext(), userAgent),
                new DefaultExtractorsFactory(),
                null,
                null
        );
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
        setPlayerState(true);
    }

    boolean isPlaying() {
        return State.getInstance(getContext()).getBoolean(State.Key.IS_PLAYING);
    }

    /**
     * Release ExoPlayer.
     */
    public void releasePlayer() {
        setPlayerState(false);
        if (mExoPlayer == null) {
            return;
        }
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    private void setPlayerState(boolean newValue) {
        State.getInstance().put(State.Key.IS_PLAYING, newValue);
    }

    private void getCurrentRecipeStep() {
        try {
            mCurrentRecipe = State.getInstance(getContext()).getInt(State.Key.ACTIVE_RECIPE_INT);
            mCurrentStep = State.getInstance().getInt(State.Key.ACTIVE_STEP_INT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
