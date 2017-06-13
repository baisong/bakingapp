package com.example.android.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.RecipeData;
import com.example.android.bakingapp.fragments.MainListFragment;
import com.example.android.bakingapp.fragments.RecipeFragment;

public class MainActivity extends AppCompatActivity implements MainListFragment.OnCardClickListener {

    private boolean mTwoPane;
    public final static String EXTRA_RECIPE_INDEX = "recipeName";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.android_me_linear_layout) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                RecipeFragment headFragment = new RecipeFragment();
                headFragment.setRecipeNames(RecipeData.getTestData());
                fragmentManager.beginTransaction()
                        .add(R.id.recipe_container, headFragment)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }

    public void onCardSelected(int position) {
        if (mTwoPane) {
            RecipeFragment newFragment = new RecipeFragment();
            newFragment.setRecipeNames(RecipeData.getTestData());
            newFragment.setListIndex(position);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_container, newFragment)
                    .commit();
        } else {
            final Intent intent = new Intent(this, RecipeActivity.class);
            intent.putExtra(EXTRA_RECIPE_INDEX, position);
            startActivity(intent);
        }

    }

}
