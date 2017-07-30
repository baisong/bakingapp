package com.example.android.bakingapp.tools;

import android.content.ContentValues;

import com.example.android.bakingapp.data.Recipes;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecordCollection {
    private static ContentValues[] recipes;
    private static ContentValues[][] ingredients;
    private static ContentValues[][] steps;

    private ContentValues getItem(ContentValues[] list, int position) {
        if (list.length >= (position + 1)) {
            return list[position];
        }
        return null;
    }
    private ContentValues[] getList(ContentValues[][] list, int position) {
        if (list.length >= (position + 1)) {
            return list[position];
        }
        return null;
    }

    public void setRecipes(ContentValues[] values) {
        recipes = values;
        ingredients = new ContentValues[recipes.length][];
        steps = new ContentValues[recipes.length][];
    }
    public void setRecipeIngredients(int recipeIndex, ContentValues[] values) {
        ingredients[recipeIndex] = values;
    }

    public void setRecipeSteps(int recipeIndex, ContentValues[] values) {
        steps[recipeIndex] = values;
    }

    public ContentValues getRecipe(int position) {
        return getItem(recipes, position);
    }

    public ContentValues getIngredient(int recipeIndex, int position) {
        return getItem(getIngredients(recipeIndex), position);
    }

    public ContentValues[] getIngredients(int position) {
        return getList(ingredients, position);
    }

    public ContentValues getStep(int recipeIndex, int position) {
        return getItem(getSteps(recipeIndex), position);
    }

    public ContentValues[] getSteps(int position) {
        return getList(steps, position);
    }

    public int getCount() {
        return recipes.length;
    }

    public String getInfoString() {
        return "Collection: " + String.valueOf(recipes.length) + " recipes";
    }

    public static List<String> getRecipeNames() {
        List<String> recipeNames = new ArrayList<>();
        for (int i = 0; i < recipes.length; i++) {
            recipeNames.add(recipes[i].getAsString(Recipes.RECIPE_NAME));
        }

        return recipeNames;
    }
}
