package com.example.android.bakingapp.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.IngredientAdapter;
import com.example.android.bakingapp.adapters.StepRecyclerAdapter;
import com.example.android.bakingapp.data.BakingAppSchema;
import com.example.android.bakingapp.tools.RecipeRecordCollection;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    public static final String RECIPE_NAME_LIST = "recipeNames";
    public static final String CURRENT_RECIPE = "currentRecipe";
    private static final String TAG = "DetailFragment";

    private List<String> mRecipeNames;
    private RecipeRecordCollection mRecipeData;
    private int mListIndex;
    private IngredientAdapter mIngredientAdapter;
    //private StepAdapter mStepAdapter;
    private StepRecyclerAdapter mStepRecyclerAdapter;

    @BindView(R.id.tv_recipe_name) TextView mRecipeName;
    @BindView(R.id.tv_ingredients_label) TextView mIngredientsLabel;
    @BindView(R.id.rv_ingredients) RecyclerView mIngredientsList;
    @BindView(R.id.tv_steps_label) TextView mStepsLabel;
    @BindView(R.id.rv_steps) RecyclerView mStepsList;
    @BindView(R.id.iv_recipe_pic) ImageView mImage;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mRecipeNames = savedInstanceState.getStringArrayList(RECIPE_NAME_LIST);
            mListIndex = savedInstanceState.getInt(CURRENT_RECIPE);
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        // @TODO Get butterknife working so we don't have to do the following:
        //mRecipeName = (TextView) rootView.findViewById(R.id.tv_recipe_name);
        mIngredientsLabel.setText(R.string.ingredients_label);
        mStepsLabel.setText(R.string.steps_label);

        // Set up ingredients list.
        LinearLayoutManager m = new LinearLayoutManager(this.getActivity());
        m.setAutoMeasureEnabled(true);
        mIngredientsList.setLayoutManager(m);
        mIngredientAdapter = new IngredientAdapter();
        mIngredientsList.setLayoutFrozen(true);
        mIngredientsList.setNestedScrollingEnabled(false);
        mIngredientsList.setAdapter(mIngredientAdapter);

        // Set up steps list.
        LinearLayoutManager n = new LinearLayoutManager(this.getActivity());
        m.setAutoMeasureEnabled(true);
        mStepsList.setLayoutManager(n);
        mStepRecyclerAdapter = new StepRecyclerAdapter(getContext());
        mStepsList.setNestedScrollingEnabled(false);
        mIngredientsList.setLayoutFrozen(true);
        mStepsList.setAdapter(mStepRecyclerAdapter);

        /*
        mStepAdapter = new StepAdapter(getContext(), new ContentValues[]{});
        mStepsList.setScrollContainer(false);
        mStepsList.setAdapter(mStepAdapter);
        */
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //Log.d("BakingApp", mRecipeData.getInfoString());
        super.onActivityCreated(savedInstanceState);

        if (mRecipeData != null && mRecipeData.getCount() > 0) {
            Log.d("BakingApp", "MainFragment::onActivityCreated()");
            loadCurrentRecipe();
        }
            /** Cycles through recipes
            mRecipeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListIndex < mRecipeNames.size() - 1) {
                        mListIndex++;
                    } else {
                        mListIndex = 0;
                    }
                    mRecipeName.setText(mRecipeNames.get(mListIndex));
                }
            });
             **/
    }

    public void loadCurrentRecipe() {
        ContentValues[] ingredients = mRecipeData.getIngredients(mListIndex);
        Log.d("BakingApp","Load current recipe. Recipe Index: " + mListIndex + "; Ingredients: " + ingredients.length);
        Log.d("BakingApp", ingredients[0].getAsString(BakingAppSchema.INGREDIENT_NAME));
        mIngredientAdapter.setIngredientsData(ingredients);
        Log.d("BakingApp", String.valueOf(ingredients.length) + " ingredients.");
        mStepRecyclerAdapter.setStepsData(mRecipeData.getSteps(mListIndex));

        ContentValues recipe = mRecipeData.getRecipe(mListIndex);
        mRecipeName.setText(recipe.getAsString(BakingAppSchema.RECIPE_NAME));
        String imageUrl = recipe.getAsString(BakingAppSchema.RECIPE_IMAGE_URL);
        if (URLUtil.isValidUrl(imageUrl)) {
            Picasso.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                    .into(mImage);
        }
    }

    public void setRecipeNames(List<String> recipeNames) {
        mRecipeNames = recipeNames;
    }

    public void setRecipeData(RecipeRecordCollection data) {
        mRecipeData = data;
    }

    public void setListIndex(int index) {
        mListIndex = index;
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putStringArrayList(RECIPE_NAME_LIST, (ArrayList<String>) mRecipeNames);
        currentState.putInt(CURRENT_RECIPE, mListIndex);
    }
}