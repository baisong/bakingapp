package com.example.android.bakingapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.DummyData;
import com.example.android.bakingapp.fragments.DetailFragment;
import com.example.android.bakingapp.tools.RecipeRecordCollection;

public class RecipeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("BakingApp", "Launching RecipeActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        if (savedInstanceState == null) {
            Log.d("BakingApp", "...Launching RecipeActivity (null instance state)");
            DetailFragment fragment = new DetailFragment();
            fragment.setRecipeNames(DummyData.getRecipeNames());
            int index = getIntent().getIntExtra(MainActivity.EXTRA_RECIPE_INDEX, 0);
            RecipeRecordCollection recipeData = (RecipeRecordCollection) getIntent().getSerializableExtra(MainActivity.EXTRA_RECIPE_DATA);
            fragment.setListIndex(index);
            fragment.setRecipeData(recipeData);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.recipe_container, fragment)
                    .commit();
        }
    }
}
