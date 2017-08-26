package com.example.android.bakingapp.data;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.bakingapp.data.Schema.RECIPE_NAME;

/**
 * Stores values converted from JSON into ContentValue[] arrays with get/set and helper functions.
 */
public class RecipeData implements Serializable {
    private String mRawJson;
    private static ContentValues[] recipes;
    private static ContentValues[][] ingredients;
    private static ContentValues[][] steps;

    // Custom (royalty-free) images to represent the assigned 4 reciped.
    private static final String[] RECIPE_IMAGES = new String[]{
            "https://raw.githubusercontent.com/baisong/bakingapp-extras/master/nutellapie.jpg",
            "https://raw.githubusercontent.com/baisong/bakingapp-extras/master/brownies.jpg",
            "https://raw.githubusercontent.com/baisong/bakingapp-extras/master/yellowcake.jpg",
            "https://raw.githubusercontent.com/baisong/bakingapp-extras/master/cheesecake.jpg",
    };


    /**
     * Initialize with JSON string.
     *
     * @param json
     */
    public RecipeData(String json) {
        loadFromJson(json);
    }

    /**
     * Helper function to access items in ContentValues[] arrays.
     *
     * @param list
     * @param position
     * @return
     */
    private ContentValues getItem(ContentValues[] list, int position) {
        if (list.length >= (position + 1)) {
            return list[position];
        }
        return null;
    }

    /**
     * Used by getIngredients() and getSteps() to access recipe steps and recipe ingredients.
     *
     * @param list
     * @param position
     * @return
     */
    private ContentValues[] getList(ContentValues[][] list, int position) {
        if (list.length >= (position + 1)) {
            return list[position];
        }
        return null;
    }

    /**
     * Get the array of ingredients for the recipe at the specified position.
     *
     * @param position
     * @return
     */
    public ContentValues[] getIngredients(int position) {
        return getList(ingredients, position);
    }

    /**
     * Get the array of steps for the recipe at the specified position.
     *
     * @param position
     * @return
     */
    public ContentValues[] getSteps(int position) {
        return getList(steps, position);
    }

    /**
     * Return true if there is well-formatted recipe data in the raw JSON string.
     *
     * @return
     */
    public boolean hasData() {
        if (mRawJson == null || mRawJson.length() == 0) return false;
        try {
            JSONArray recipes = new JSONArray(mRawJson);
            ContentValues[] r = parseRecipes(recipes);
            return r.length > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Parse and assign recipe data to member variables.
     *
     * @param json
     */
    public void loadFromJson(String json) {
        mRawJson = json;
        recipes = new ContentValues[]{};
        ingredients = new ContentValues[0][];
        steps = new ContentValues[0][];
        try {
            JSONArray recipes = new JSONArray(json);
            setRecipes(parseRecipes(recipes));
            for (int i = 0; i < recipes.length(); i++) {
                JSONObject recipe = recipes.getJSONObject(i);
                setRecipeIngredients(i, parseIngredients(recipe));
                setRecipeSteps(i, parseSteps(recipe));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function to troubleshoot bad data.
     */
    public void reload() {
        loadFromJson(mRawJson);
    }

    /**
     * Parse JSONArray recipes into a ContentValues[] array.
     *
     * @param recipes
     * @return
     */
    public static ContentValues[] parseRecipes(JSONArray recipes) {
        ContentValues[] items = getItems(recipes, Schema.RECIPE_CONTENT_FIELDS);
        for (int i = 0; i < RECIPE_IMAGES.length; i++) {
            items[i].put(Schema.RECIPE_IMAGE_URL, RECIPE_IMAGES[i]);
        }
        return items;
    }


    /**
     * Parse recipe steps from a recipe JSONObject into a ContentValues[] array.
     *
     * @param recipe
     * @return
     */
    public static ContentValues[] parseSteps(JSONObject recipe) {
        try {
            int recipeId = recipe.getInt(Schema.RECIPE_ID);
            JSONArray ingredients = recipe.getJSONArray(Schema.RECIPE_STEPS_ARRAY);
            return getItems(ingredients, Schema.STEP_CONTENT_FIELDS, Schema.RECIPE_REFERENCE_ID, recipeId);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parse recipe ingredients from a recipe JSONObject into a ContentValues[] array.
     *
     * @param recipe
     * @return
     */
    public static ContentValues[] parseIngredients(JSONObject recipe) {
        try {
            int recipeId = recipe.getInt(Schema.RECIPE_ID);
            JSONArray ingredients = recipe.getJSONArray(Schema.RECIPE_INGREDIENTS_ARRAY);
            return getItems(ingredients, Schema.INGREDIENT_CONTENT_FIELDS, Schema.RECIPE_REFERENCE_ID, recipeId);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Helper function to populate ContentValues[] array item fields.
     *
     * @param jsonArray
     * @param fields
     * @return
     */
    public static ContentValues[] getItems(JSONArray jsonArray, String[] fields) {
        return getItems(jsonArray, fields, "", 0);
    }

    /**
     * Helper function to populate ContentValues[] array item fields, allowing local extra fields.
     * <p>
     * The only extra field provided at this time is the custom recipe image URL field.
     *
     * @param jsonArray
     * @param fields
     * @param extraKey
     * @param extraValue
     * @return
     */
    public static ContentValues[] getItems(JSONArray jsonArray, String[] fields, String extraKey, int extraValue) {
        ContentValues[] items;
        try {
            int length = jsonArray.length();
            items = new ContentValues[length];
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    if (extraKey.length() > 0 && extraValue > 0) {
                        items[i] = prepareFromJson(jsonArray.getJSONObject(i), fields, extraKey, extraValue);
                    } else {
                        items[i] = prepareFromJson(jsonArray.getJSONObject(i), fields);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return items;
    }

    /**
     * Helper function to extract JSONObject into a ContentValues item, allowing local extra field.
     *
     * @param json
     * @param keys
     * @param extraKey
     * @param extraValue
     * @return
     */
    private static ContentValues prepareFromJson(JSONObject json, String[] keys, String extraKey, int extraValue) {
        ContentValues item;
        item = prepareFromJson(json, keys);
        item.put(extraKey, extraValue);
        return item;
    }

    /**
     * Helper function to extract JSONObject into a ContentValues item.
     *
     * @param json
     * @param keys
     * @return
     */
    private static ContentValues prepareFromJson(JSONObject json, String[] keys) {
        ContentValues item;
        try {
            item = new ContentValues();
            for (String key : keys) {
                item.put(key, json.getString(key));
            }
            return item;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Set the recipes ContentValues[] array.
     *
     * @param values
     */
    public void setRecipes(ContentValues[] values) {
        recipes = values;
        ingredients = new ContentValues[recipes.length][];
        steps = new ContentValues[recipes.length][];
    }

    /**
     * Set the given recipe's ContentValue[] array of ingredients.
     *
     * @param recipeIndex
     * @param values
     */
    public void setRecipeIngredients(int recipeIndex, ContentValues[] values) {
        ingredients[recipeIndex] = values;
    }

    /**
     * Set the given recipe's ContentValue[] array of steps.
     *
     * @param recipeIndex
     * @param values
     */
    public void setRecipeSteps(int recipeIndex, ContentValues[] values) {
        steps[recipeIndex] = values;
    }

    /**
     * Get the given recipe ContentValues object.
     *
     * @param position
     * @return
     */
    public ContentValues getRecipe(int position) {
        return getItem(recipes, position);
    }

    /**
     * Get the number of parsed recipes.
     *
     * @return
     */
    public int getCount() {
        return recipes.length;
    }

    /**
     * Return a list of the names of the parsed recipes.
     *
     * @return
     */
    public List<String> getRecipeNames() {
        List<String> recipeNames = new ArrayList<>();
        for (ContentValues recipe : recipes) {
            recipeNames.add(recipe.getAsString(RECIPE_NAME));
        }

        return recipeNames;
    }

    /**
     * Return the recipe ContentValues[] array.
     *
     * @return
     */
    public ContentValues[] getRecipes() {
        return recipes;
    }
}
