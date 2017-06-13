package com.example.android.bakingapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.fragments.RecipeFragment;
import com.example.android.bakingapp.data.RecipeData;

public class RecipeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        if(savedInstanceState == null) {
            RecipeFragment fragment = new RecipeFragment();
            fragment.setRecipeNames(RecipeData.getTestData());
            int index = getIntent().getIntExtra(MainActivity.EXTRA_RECIPE_INDEX, 0);
            fragment.setListIndex(index);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.recipe_container, fragment)
                    .commit();
        }
    }
}
