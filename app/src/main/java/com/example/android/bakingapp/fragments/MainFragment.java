package com.example.android.bakingapp.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.IngredientAdapter;
import com.example.android.bakingapp.adapters.StepRecyclerAdapter;
import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.data.Schema;
import com.example.android.bakingapp.data.State;
import com.example.android.bakingapp.tools.RecyclerItemClickListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View of a selected recipe, including ingredients and steps which launch a DetailFragment view.
 */
public class MainFragment extends Fragment {
    private RecipeData mRecipeData;
    private int mCurrentRecipe;
    private IngredientAdapter mIngredientAdapter;
    private StepRecyclerAdapter mStepRecyclerAdapter;

    OnStepClickListener mCallback;

    @BindView(R.id.tv_recipe_name)
    TextView mRecipeName;
    @BindView(R.id.tv_ingredients_label)
    TextView mIngredientsLabel;
    @BindView(R.id.rv_ingredients)
    RecyclerView mIngredientsList;
    @BindView(R.id.tv_steps_label)
    TextView mStepsLabel;
    @BindView(R.id.rv_steps)
    RecyclerView mStepsList;
    @BindView(R.id.iv_recipe_pic)
    ImageView mImage;

    public MainFragment() {
    }

    /**
     * Ensure host activity has implemented the callback interface.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            String message = context.toString() + " must implement OnImageClickListener";
            throw new ClassCastException(message);
        }
    }

    /**
     * Set up view objects including linear RecyclerView instances for ingredients and steps.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrentRecipe = savedInstanceState.getInt(State.CURRENT_RECIPE_INDEX);
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
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
        mStepsList.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), mStepsList, new RecyclerItemClickListener.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mCallback.onStepSelected(mCurrentRecipe, position);
                    }
                }));
        mStepsList.setAdapter(mStepRecyclerAdapter);

        return rootView;
    }

    /**
     * Redraw view if recipe data is already loaded.
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mRecipeData != null && mRecipeData.getCount() > 0) {
            loadCurrentRecipe();
        }
    }

    /**
     * Store current recipe ID on outState bundle.
     *
     * @param currentState
     */
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putInt(State.CURRENT_RECIPE_INDEX, mCurrentRecipe);
    }

    /**
     * Interface implemented by MainActivity.
     */
    public interface OnStepClickListener {
        void onStepSelected(int recipe, int step);
    }

    /**
     * Load values from member variables into RecyclerAdapter instances, update main fragment view.
     */
    public void loadCurrentRecipe() {
        ContentValues[] ingredients = mRecipeData.getIngredients(mCurrentRecipe);
        mIngredientAdapter.setIngredientsData(ingredients);
        mStepRecyclerAdapter.setStepsData(mRecipeData.getSteps(mCurrentRecipe));

        ContentValues recipe = mRecipeData.getRecipe(mCurrentRecipe);
        mRecipeName.setText(recipe.getAsString(Schema.RECIPE_NAME));
        String imageUrl = recipe.getAsString(Schema.RECIPE_IMAGE_URL);
        int placeholder = R.drawable.ic_photo_size_select_actual_black_24dp;
        if (URLUtil.isValidUrl(imageUrl)) {
            Picasso.with(getContext()).load(imageUrl).placeholder(placeholder).into(mImage);
        }
    }

    /**
     * Helper function for MainActivity to set RecipeData object.
     *
     * @param data
     */
    public void setRecipeData(RecipeData data) {
        mRecipeData = data;
    }

    /**
     * Helper function for MainActivity to set current recipe.
     *
     * @param index
     */
    public void setCurrentRecipe(int index) {
        mCurrentRecipe = index;
    }

}
