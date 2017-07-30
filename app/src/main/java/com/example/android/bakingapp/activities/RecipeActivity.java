package com.example.android.bakingapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.fragments.DetailFragment;
import com.example.android.bakingapp.data.Recipes;

public class RecipeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        if(savedInstanceState == null) {
            DetailFragment fragment = new DetailFragment();
            fragment.setRecipeNames(Recipes.getDummyRecipeNames());
            int index = getIntent().getIntExtra(MainActivity.EXTRA_RECIPE_INDEX, 0);
            fragment.setListIndex(index);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.recipe_container, fragment)
                    .commit();
        }
    }
}
