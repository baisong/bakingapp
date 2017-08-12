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
import com.example.android.bakingapp.data.BakingAppSchema;
import com.example.android.bakingapp.tools.RecipeRecordCollection;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment {

    public static final String RECIPE_NAME_LIST = "recipeNames";
    public static final String CURRENT_RECIPE = "currentRecipe";
    private static final String TAG = "DetailFragment";

    private ContentValues[] mSteps;
    private ContentValues mStep;
    private Context mContext;
    private RecipeRecordCollection mRecipeData;
    private int mCurrentStep;
    private int mCurrentRecipe;

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

        if (savedInstanceState != null) {
            //mRecipeNames = savedInstanceState.getStringArrayList(RECIPE_NAME_LIST);
            mCurrentRecipe = savedInstanceState.getInt(CURRENT_RECIPE);
        }

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
        updateStepView();
        return rootView;
    }

    private void updateStepView() {
        mStep = mSteps[mCurrentStep];
        //Log.d("BakingApp", mStep.toString());
        mTitle.setText(mStep.getAsString(BakingAppSchema.STEP_TITLE));
        mBody.setText(mStep.getAsString(BakingAppSchema.STEP_BODY));
        String videoUrl = mStep.getAsString(BakingAppSchema.STEP_VIDEO_URL);
        if (URLUtil.isValidUrl(videoUrl)) {
            mVideoURL.setText(videoUrl);
            mVideoURL.setVisibility(View.VISIBLE);
        } else {
            mVideoURL.setVisibility(View.GONE);
        }
        String imageUrl = mStep.getAsString(BakingAppSchema.STEP_IMAGE_URL);
        if (URLUtil.isValidUrl(imageUrl)) {
            Log.d("BakingApp", "Picasso loading... " + imageUrl);
            mThumbnail.setVisibility(View.VISIBLE);
            Picasso.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                    .into(mThumbnail);
        } else {
            mThumbnail.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("BakingApp", mRecipeData.getInfoString());
        super.onActivityCreated(savedInstanceState);

        /*
        ContentValues[] ingredients = mRecipeData.getIngredients(mCurrentRecipe);
        mIngredientAdapter.setIngredientsData(ingredients);
        Log.d("BakingApp", String.valueOf(ingredients.length) + " ingredients.");
        mStepRecyclerAdapter.setStepsData(mRecipeData.getSteps(mCurrentRecipe));

        ContentValues recipe = mRecipeData.getRecipe(mCurrentRecipe);
        mRecipeName.setText(recipe.getAsString(BakingAppSchema.RECIPE_NAME));
        String imageUrl = recipe.getAsString(BakingAppSchema.RECIPE_IMAGE_URL);
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
        mSteps = mRecipeData.getSteps(mCurrentRecipe);
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        //currentState.putStringArrayList(RECIPE_NAME_LIST, (ArrayList<String>) mRecipeNames);
        currentState.putInt(CURRENT_RECIPE, mCurrentRecipe);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    public void navigateBack() {
        if (mCurrentStep == 0) {
            showToast("First step");
        } else {
            mCurrentStep = mCurrentStep - 1;
            updateStepView();
        }
    }

    public void navigateNext() {
        if ((mCurrentStep + 1) == mSteps.length) {
            showToast("Last step");
        } else {
            mCurrentStep = mCurrentStep + 1;
            updateStepView();
        }
    }
}
