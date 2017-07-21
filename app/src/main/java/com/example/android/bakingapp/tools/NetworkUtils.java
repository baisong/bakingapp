package com.example.android.bakingapp.tools;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String DATA_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private static final String RECIPE_ID = "id";
    private static final String RECIPE_NAME = "name";
    private static final String RECIPE_INGREDIENTS_ARRAY = "ingredients";
    private static final String RECIPE_STEPS_ARRAY = "steps";
    private static final String RECIPE_SERVING_COUNT = "servings";
    private static final String RECIPE_IMAGE_URL = "image";

    private static final String[] RECIPE_CONTENT_FIELDS = new String[]{
            RECIPE_ID,
            RECIPE_NAME,
            RECIPE_SERVING_COUNT,
            RECIPE_IMAGE_URL,
    };

    // "Ingredient" and "Step" records reference their recipe id.
    private static final String RECIPE_REFERENCE_ID = "recipe_id";

    private static final String INGREDIENT_NAME = "ingredient";
    private static final String INGREDIENT_QUANTITY = "quantity";
    private static final String INGREDIENT_MEASURE = "measure";
    private static final String[] INGREDIENT_CONTENT_FIELDS = new String[]{
            INGREDIENT_NAME,
            INGREDIENT_QUANTITY,
            INGREDIENT_MEASURE,
    };

    private static final String STEP_LABEL = "shortDescription";
    private static final String STEP_BODY = "description";
    private static final String STEP_VIDEO_URL = "videoURL";
    private static final String STEP_IMAGE_URL = "thumbnailURL";
    private static final String[] STEP_CONTENT_FIELDS = new String[]{
            STEP_LABEL,
            STEP_BODY,
            STEP_VIDEO_URL,
            STEP_IMAGE_URL,
    };

    /**
     * @TODO Document me
     * @return
     */
    public static URL buildUrl() {
        Uri builtUri = Uri.parse(DATA_URL).buildUpon().build();
        return getUrl(builtUri);
    }

    /**
     * @TODO Document me
     * @param uri
     * @return
     */
    private static URL getUrl(Uri uri) {
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * @TODO Document me
     * @param url
     * @return
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * @TODO Document me
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

    /**
     * @TODO Document me
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
     * @TODO Document me
     * @param recipes
     * @return
     */
    public static ContentValues[] getRecipes(JSONArray recipes) {
        return getItems(recipes, RECIPE_CONTENT_FIELDS);
    }

    /**
     * @TODO Document me
     * @param recipe
     * @return
     */
    public static ContentValues[] getSteps(JSONObject recipe) {
        try {
            int recipeId = recipe.getInt(RECIPE_ID);
            JSONArray ingredients = recipe.getJSONArray(RECIPE_STEPS_ARRAY);
            return getItems(ingredients, STEP_CONTENT_FIELDS, RECIPE_REFERENCE_ID, recipeId);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @TODO Document me
     * @param recipe
     * @return
     */
    public static ContentValues[] getIngredients(JSONObject recipe) {
        try {
            int recipeId = recipe.getInt(RECIPE_ID);
            JSONArray ingredients = recipe.getJSONArray(RECIPE_INGREDIENTS_ARRAY);
            return getItems(ingredients, INGREDIENT_CONTENT_FIELDS, RECIPE_REFERENCE_ID, recipeId);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @TODO Document me
     * @param jsonArray
     * @param fields
     * @return
     */
    public static ContentValues[] getItems(JSONArray jsonArray, String[] fields) {
        return getItems(jsonArray, fields, "", 0);
    }

    /**
     * @TODO Document me
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

    public static ContentValues[] concat(ContentValues[] a, ContentValues[] b) {
        int aLen = a.length;
        int bLen = b.length;
        ContentValues[] c= new ContentValues[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    /**
     * @TODO Document me
     * @return
     */
    @Nullable
    public static RecipeRecordCollection fetch() {
        RecipeRecordCollection collection = new RecipeRecordCollection();
        URL movieQueryUrl = buildUrl();
        Log.d(LOG_TAG, movieQueryUrl.toString());
        try {
            String jsonString = getResponseFromHttpUrl(movieQueryUrl);
            Log.d(LOG_TAG, jsonString);
            JSONArray recipes = new JSONArray(jsonString);
            collection.recipes = getRecipes(recipes);
            collection.ingredients = new ContentValues[]{};
            collection.steps = new ContentValues[]{};
            for (int i = 0; i < recipes.length(); i++) {
                JSONObject recipe = recipes.getJSONObject(i);
                collection.ingredients = concat(collection.ingredients, getIngredients(recipe));
                collection.steps = concat(collection.steps, getSteps(recipe));
            }
            return collection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}