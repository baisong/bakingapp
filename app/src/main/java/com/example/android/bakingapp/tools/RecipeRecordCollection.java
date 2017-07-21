package com.example.android.bakingapp.tools;

import android.content.ContentValues;

import com.example.android.bakingapp.data.RecipeColumns;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecordCollection {
    public static ContentValues[] recipes;
    public static ContentValues[] ingredients;
    public static ContentValues[] steps;
    public int length() {
        return recipes.length;
    }
    public static List<String> getRecipeNames() {
        List<String> recipeNames = new ArrayList<>();
        for (int i = 0; i < recipes.length; i++) {
            recipeNames.add(recipes[0].getAsString(RecipeColumns.NAME));
        }

        return recipeNames;
    }
}
