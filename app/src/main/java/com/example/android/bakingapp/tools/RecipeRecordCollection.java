package com.example.android.bakingapp.tools;

import android.content.ContentValues;

import com.example.android.bakingapp.data.Schema;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.bakingapp.data.Schema.RECIPE_NAME;

public class RecipeRecordCollection implements Serializable {
    String mRawJson;
    private static ContentValues[] recipes;
    private static ContentValues[][] ingredients;
    private static ContentValues[][] steps;

    private static final String[] RECIPE_IMAGES = new String[]{
            "https://raw.githubusercontent.com/baisong/bakingapp-extras/master/nutellapie.jpg",
            "https://raw.githubusercontent.com/baisong/bakingapp-extras/master/brownies.jpg",
            "https://raw.githubusercontent.com/baisong/bakingapp-extras/master/yellowcake.jpg",
            "https://raw.githubusercontent.com/baisong/bakingapp-extras/master/cheesecake.jpg",
    };

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

    public RecipeRecordCollection(String json) {
        loadFromJson(json);
    }

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

    public void reload() {
        loadFromJson(mRawJson);
    }

    /**
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
     *
     * @param recipe
     * @return
     */
    public static ContentValues[] parseSteps(JSONObject recipe) {
        try {
            int recipeId = recipe.getInt(Schema.RECIPE_ID);
            JSONArray ingredients = recipe.getJSONArray(Schema.RECIPE_STEPS_ARRAY);
            return getItems(ingredients, Schema.STEP_CONTENT_FIELDS, Schema.RECIPE_REFERENCE_ID, recipeId);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param recipe
     * @return
     */
    public static ContentValues[] parseIngredients(JSONObject recipe) {
        try {
            int recipeId = recipe.getInt(Schema.RECIPE_ID);
            JSONArray ingredients = recipe.getJSONArray(Schema.RECIPE_INGREDIENTS_ARRAY);
            return getItems(ingredients, Schema.INGREDIENT_CONTENT_FIELDS, Schema.RECIPE_REFERENCE_ID, recipeId);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     *
     * @param jsonArray
     * @param fields
     * @return
     */
    public static ContentValues[] getItems(JSONArray jsonArray, String[] fields) {
        return getItems(jsonArray, fields, "", 0);
    }

    /**
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
                    }
                    else {
                        items[i] = prepareFromJson(jsonArray.getJSONObject(i), fields);
                    }
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return items;
    }

    /**
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
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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
        return mRawJson;
    }

    public List<String> getRecipeNames() {
        List<String> recipeNames = new ArrayList<>();
        for (ContentValues recipe : recipes) {
            recipeNames.add(recipe.getAsString(RECIPE_NAME));
        }

        return recipeNames;
    }

    public ContentValues[] getRecipes() {
        return recipes;
    }

    /*
    public CharSequence[] getNamesAsListPreferenceEntries() {
        CharSequence[] entries = new CharSequence[recipes.length];
        for (int i = 0; i < recipes.length; i++) {
            entries[i] = recipes[i].getAsString(RECIPE_NAME);
        }
        return entries;
    }

    public int[] getNamesAsListPreferenceValues() {
        int[] values = new int[recipes.length];
        for (int i = 0; i < recipes.length; i++) {
            values[i] = i;
        }
        return values;
    }
    */
}
