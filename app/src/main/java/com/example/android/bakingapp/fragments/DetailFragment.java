package com.example.android.bakingapp.fragments;

import android.content.ContentValues;
import android.content.Context;
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
import com.example.android.bakingapp.data.Schema;
import com.example.android.bakingapp.data.State;
import com.example.android.bakingapp.tools.RecipeRecordCollection;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.bakingapp.data.State.CURRENT_RECIPE_INDEX;
import static com.example.android.bakingapp.data.State.CURRENT_STEP_INDEX;
import static com.example.android.bakingapp.data.State.IS_TWO_PANE;
import static com.example.android.bakingapp.data.State.RECIPE_DATA;

public class DetailFragment extends Fragment {

    private static final String LOG_TAG = "BakingApp [DET]{Frag}";

    private ContentValues[] mSteps;
    private ContentValues mStep;
    private Context mContext;
    private RecipeRecordCollection mRecipeData;
    private int mCurrentStep;
    private int mCurrentRecipe;
    private boolean mTwoPane;

    @BindView(R.id.tv_step_title)
    TextView mTitle;
    @BindView(R.id.tv_video_url)
    TextView mVideoURL;
    @BindView(R.id.tv_step_body)
    TextView mBody;
    @BindView(R.id.iv_thumbnail)
    ImageView mThumbnail;

    @BindView(R.id.btn_prev_step)
    Button mBackStep;
    @BindView(R.id.btn_next_step)
    Button mNextStep;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log("create 1.");
        if (savedInstanceState != null) {
            log("create 1. EXISTS");
            /*
            mCurrentRecipe = savedInstanceState.getInt(CURRENT_RECIPE_INDEX);
            mCurrentStep = savedInstanceState.getInt(CURRENT_STEP_INDEX);
            mRecipeData = (RecipeRecordCollection) savedInstanceState.getSerializable(RECIPE_DATA);
            mTwoPane = savedInstanceState.getBoolean(IS_TWO_PANE);
            */
        } else {
            log("create 1. NULL");
            // Data has been set in DetailActivity.createFragmentFromExplicitIntent
        }

        log("NEW >>>"
                + " twoPane: " + String.valueOf(mTwoPane)
                + "; Step: " + String.valueOf(mCurrentRecipe)
                + "," + String.valueOf(mCurrentStep)
                + "; Data: " + quickLogData());

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
        log("update 1.");
        if (mStep == null) {
            log("update 1. NULL");
        }
        log("update 2.");
        try {
            setStep(mRecipeData, mCurrentRecipe, mCurrentStep);
            if (mStep == null) {
                log("update 2. NULL");
            }
        }
        catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
        log("update 3.");
        mStep = getStep();
        if (mStep == null) {
            log("update 3. NULL");
            return rootView;
        }
        updateStepView();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        log("=== onViewCreated ===");

        log("NEW >>>"
                + " twoPane: " + String.valueOf(mTwoPane)
                + "; Step: " + String.valueOf(mCurrentRecipe)
                + "," + String.valueOf(mCurrentStep)
                + "; Data: " + quickLogData());
        if (savedInstanceState != null) {
            log("create 1. EXISTS");
            mCurrentRecipe = savedInstanceState.getInt(CURRENT_RECIPE_INDEX);
            mCurrentStep = savedInstanceState.getInt(CURRENT_STEP_INDEX);
            mRecipeData = (RecipeRecordCollection) savedInstanceState.getSerializable(RECIPE_DATA);
            mTwoPane = savedInstanceState.getBoolean(IS_TWO_PANE);
            log("NEW >>>"
                    + " twoPane: " + String.valueOf(mTwoPane)
                    + "; Step: " + String.valueOf(mCurrentRecipe)
                    + "," + String.valueOf(mCurrentStep)
                    + "; Data: " + quickLogData());
        } else {
            log("create 1. NULL");
            // Data has been set in DetailActivity.createFragmentFromExplicitIntent
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //Log.d("BakingApp", mRecipeData.getInfoString());
        super.onActivityCreated(savedInstanceState);

        /*
        ContentValues[] ingredients = mRecipeData.getIngredients(mCurrentRecipe);
        mIngredientAdapter.setIngredientsData(ingredients);
        Log.d("BakingApp", String.valueOf(ingredients.length) + " ingredients.");
        mStepRecyclerAdapter.setStepsData(mRecipeData.getSteps(mCurrentRecipe));

        ContentValues recipe = mRecipeData.getRecipe(mCurrentRecipe);
        mRecipeName.setText(recipe.getAsString(Schema.RECIPE_NAME));
        String imageUrl = recipe.getAsString(Schema.RECIPE_IMAGE_URL);
        if (URLUtil.isValidUrl(imageUrl)) {
            Picasso.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                    .into(mImage);
        }*/
        /** Cycles through steps
         mRecipeName.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        if (mCurrentRecipe < mRecipeNames.size() - 1) {
        mCurrentRecipe++;
        } else {
        mCurrentRecipe = 0;
        }
        mRecipeName.setText(mRecipeNames.get(mCurrentRecipe));
        }
        });
         **/
    }

    public void setRecipeData(RecipeRecordCollection data) {
        mRecipeData = data;
    }

    public void setCurrentStep(int recipe, int step) {
        boolean newRecipe = recipe != mCurrentRecipe;
        mCurrentRecipe = recipe;
        mCurrentStep = step;
        if (newRecipe && mRecipeData != null && mRecipeData.getCount() > 0) {
            refreshSteps();
        }
    }

    public void refreshSteps() {
        log("Refreshing Steps with "
                + String.valueOf(mCurrentRecipe)
                + ", " + String.valueOf(mCurrentStep));
        mSteps = mRecipeData.getSteps(mCurrentRecipe);
        log("Steps: " + String.valueOf(mSteps));
    }

    public ContentValues getStep() {
        return mStep;
    }

    public void setStep(RecipeRecordCollection data, int recipe, int step) {
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
        if (recipe < 0 || recipe > 3) {
            throw new UnsupportedOperationException("This version only supports 4 recipes.");
        }
        mCurrentRecipe = recipe;
        if (step < 0 || step > 10) {
            throw new UnsupportedOperationException("This version only supports 10 steps");
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

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putInt(CURRENT_RECIPE_INDEX, mCurrentRecipe);
        currentState.putInt(CURRENT_STEP_INDEX,mCurrentStep);
        currentState.putSerializable(RECIPE_DATA, mRecipeData);
        State.getInstance(getContext()).put(State.Key.ACTIVE_RECIPE_INT, mCurrentRecipe);
        State.getInstance().put(State.Key.ACTIVE_STEP_INT, mCurrentStep);
        super.onSaveInstanceState(currentState);
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
            updateStepView();
        }
    }

    public void navigateNext() {
        if ((mCurrentStep + 1) == mSteps.length) {
            showToast("Last step");
        } else {
            setCurrentStep(mCurrentStep + 1);
            updateStepView();
        }
    }

    private void updateStepView() {
        log("update 1.");
        if (mStep == null) {
            log("update 1. NULL");
            return;
        }
        log("update 2.");
        mStep = mSteps[mCurrentStep];
        //Log.d("BakingApp", mStep.toString());
        mTitle.setText(mStep.getAsString(Schema.STEP_TITLE));
        mBody.setText(mStep.getAsString(Schema.STEP_BODY));
        String videoUrl = mStep.getAsString(Schema.STEP_VIDEO_URL);
        if (URLUtil.isValidUrl(videoUrl)) {
            mVideoURL.setText(videoUrl);
            mVideoURL.setVisibility(View.VISIBLE);
        } else {
            mVideoURL.setVisibility(View.GONE);
        }
        String imageUrl = mStep.getAsString(Schema.STEP_IMAGE_URL);
        if (URLUtil.isValidUrl(imageUrl)) {
            log("Picasso loading... " + imageUrl);
            mThumbnail.setVisibility(View.VISIBLE);
            Picasso.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                    .into(mThumbnail);
        } else {
            mThumbnail.setVisibility(View.GONE);
        }
    }

    private void log(String message) {
        Log.d(LOG_TAG, message);
    }
    private String quickLogData() {
        if (mRecipeData == null) {
            return "0";
        }
        return String.valueOf(mRecipeData.getCount());
    }
}
