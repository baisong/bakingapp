package com.example.android.bakingapp.tools;

import android.content.ContentValues;

import com.example.android.bakingapp.data.Recipes;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecordCollection {
    public static ContentValues[] recipes;
    public static ContentValues[] ingredients;
    public static ContentValues[] steps;


    public ContentValues getItem(ContentValues[] list, int position) {
        if (list.length >= (position + 1)) {
            return list[position];
        }
        return null;
    }

    public ContentValues getRecipe(int position) {
        return getItem(recipes, position);
    }

    public ContentValues getIngredients(int position) {
        return getItem(ingredients, position);
    }

    public ContentValues getSteps(int position) {
        return getItem(steps, position);
    }

    public int getCount() {
        return recipes.length;
    }

    public String getInfoString() {
        return "Collection: "
                + String.valueOf(recipes.length) + " recipes, "
                + String.valueOf(ingredients.length) + " ingredients, "
                + String.valueOf(steps.length) + " steps.";
    }

    public static List<String> getRecipeNames() {
        List<String> recipeNames = new ArrayList<>();
        for (int i = 0; i < recipes.length; i++) {
            recipeNames.add(recipes[i].getAsString(Recipes.RECIPE_NAME));
        }

        return recipeNames;
    }
}
