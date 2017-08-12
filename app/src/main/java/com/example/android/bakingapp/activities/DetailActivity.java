package com.example.android.bakingapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.fragments.DetailFragment;
import com.example.android.bakingapp.tools.RecipeRecordCollection;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("BakingApp", "Launching DetailActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        if (savedInstanceState == null) {
            Log.d("BakingApp", "...Launching DetailActivity (null instance state)");
            DetailFragment fragment = new DetailFragment();
            int recipe = getIntent().getIntExtra(MainActivity.EXTRA_RECIPE_INDEX, 0);
            int step = getIntent().getIntExtra(MainActivity.EXTRA_STEP_INDEX, 0);
            RecipeRecordCollection recipeData = (RecipeRecordCollection) getIntent().getSerializableExtra(MainActivity.EXTRA_RECIPE_DATA);
            fragment.setRecipeData(recipeData);
            fragment.setCurrentStep(recipe, step);
            fragment.refreshSteps();

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.recipe_container, fragment)
                    .commit();
        }
    }
}
