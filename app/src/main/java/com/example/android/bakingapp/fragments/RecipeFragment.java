package com.example.android.bakingapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;

import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment {

    public static final String RECIPE_NAME_LIST = "recipeNames";
    public static final String CURRENT_RECIPE = "currentRecipe";
    private static final String TAG = "RecipeFragment";

    private List<String> mRecipeNames;
    private int mListIndex;

    public RecipeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mRecipeNames = savedInstanceState.getStringArrayList(RECIPE_NAME_LIST);
            mListIndex = savedInstanceState.getInt(CURRENT_RECIPE);
        }

        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        final TextView textView = (TextView) rootView.findViewById(R.id.tv_recipe_name);
        if (mRecipeNames != null) {
            textView.setText(mRecipeNames.get(mListIndex));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListIndex < mRecipeNames.size() - 1) {
                        mListIndex++;
                    } else {
                        mListIndex = 0;
                    }
                    textView.setText(mRecipeNames.get(mListIndex));
                }
            });

        } else {
            Log.v(TAG, "This fragment has a null list of recipe names");
        }

        return rootView;
    }

    public void setRecipeNames(List<String> recipeNames) {
        mRecipeNames = recipeNames;
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
