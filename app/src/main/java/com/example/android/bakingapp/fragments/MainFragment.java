package com.example.android.bakingapp.fragments;

import android.content.ContentValues;
import android.content.Context;
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
import com.example.android.bakingapp.data.Schema;
import com.example.android.bakingapp.tools.RecipeRecordCollection;
import com.example.android.bakingapp.tools.RecyclerItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    public static final String RECIPE_NAME_LIST = "recipeNames";
    public static final String CURRENT_RECIPE = "currentRecipe";
    private static final String LOG_TAG = "BakingApp [MAI]{Frag}";

    private List<String> mRecipeNames;
    private RecipeRecordCollection mRecipeData;
    private int mCurrentRecipe;
    private IngredientAdapter mIngredientAdapter;
    private StepRecyclerAdapter mStepRecyclerAdapter;

    OnStepClickListener mCallback;

    @BindView(R.id.tv_recipe_name) TextView mRecipeName;
    @BindView(R.id.tv_ingredients_label) TextView mIngredientsLabel;
    @BindView(R.id.rv_ingredients) RecyclerView mIngredientsList;
    @BindView(R.id.tv_steps_label) TextView mStepsLabel;
    @BindView(R.id.rv_steps) RecyclerView mStepsList;
    @BindView(R.id.iv_recipe_pic) ImageView mImage;

    public MainFragment() {
    }

    public interface OnStepClickListener {
        void onStepSelected(int recipe, int step);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mRecipeNames = savedInstanceState.getStringArrayList(RECIPE_NAME_LIST);
            mCurrentRecipe = savedInstanceState.getInt(CURRENT_RECIPE);
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
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
        /*
        mIngredientsList.setOnItemClickListener(new RecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Trigger the callback method and pass in the position that was clicked
                mCallback.onStepSelected(position);
            }
        });*/
        mIngredientsList.setAdapter(mIngredientAdapter);

        // Set up steps list.
        LinearLayoutManager n = new LinearLayoutManager(this.getActivity());
        m.setAutoMeasureEnabled(true);
        mStepsList.setLayoutManager(n);
        mStepRecyclerAdapter = new StepRecyclerAdapter(getContext());
        mStepsList.setNestedScrollingEnabled(false);
        mStepsList.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), mStepsList, new RecyclerItemClickListener.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mCallback.onStepSelected(mCurrentRecipe, position);
                    }
                }));
        mStepsList.setAdapter(mStepRecyclerAdapter);

        /*
        mStepAdapter = new WidgetRecipeAdapter(getContext(), new ContentValues[]{});
        mStepsList.setScrollContainer(false);
        mStepsList.setAdapter(mStepAdapter);
        */
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mRecipeData != null && mRecipeData.getCount() > 0) {
            log("onActivityCreated()");
            loadCurrentRecipe();
        }
    }

    public void loadCurrentRecipe() {
        ContentValues[] ingredients = mRecipeData.getIngredients(mCurrentRecipe);
        log("Loading recipe at "
                + mCurrentRecipe + "; Ingredients: "
                + ingredients.length + "; " + ingredients[0].getAsString(Schema.INGREDIENT_NAME));
        mIngredientAdapter.setIngredientsData(ingredients);
        mStepRecyclerAdapter.setStepsData(mRecipeData.getSteps(mCurrentRecipe));

        ContentValues recipe = mRecipeData.getRecipe(mCurrentRecipe);
        mRecipeName.setText(recipe.getAsString(Schema.RECIPE_NAME));
        String imageUrl = recipe.getAsString(Schema.RECIPE_IMAGE_URL);
        if (URLUtil.isValidUrl(imageUrl)) {
            Picasso.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                    .into(mImage);
        }
    }

    public void setRecipeData(RecipeRecordCollection data) {
        mRecipeData = data;
    }

    public void setCurrentRecipe(int index) {
        mCurrentRecipe = index;
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putStringArrayList(RECIPE_NAME_LIST, (ArrayList<String>) mRecipeNames);
        currentState.putInt(CURRENT_RECIPE, mCurrentRecipe);
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
