package com.example.android.bakingapp.data;

/**
 * Created by oren on 7/31/17.
 */

public class BakingAppSchema {
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_INGREDIENTS_ARRAY = "ingredients";
    public static final String RECIPE_STEPS_ARRAY = "steps";
    public static final String RECIPE_SERVING_COUNT = "servings";
    public static final String RECIPE_IMAGE_URL = "image";
    public static final String[] RECIPE_CONTENT_FIELDS = new String[]{
            RECIPE_ID,
            RECIPE_NAME,
            RECIPE_SERVING_COUNT,
            RECIPE_IMAGE_URL,
    };
    // "Ingredient" and "Step" records reference their recipe id.
    public static final String RECIPE_REFERENCE_ID = "recipe_id";
    public static final String INGREDIENT_NAME = "ingredient";
    public static final String INGREDIENT_QUANTITY = "quantity";
    public static final String INGREDIENT_MEASURE = "measure";
    public static final String[] INGREDIENT_CONTENT_FIELDS = new String[]{
            INGREDIENT_NAME,
            INGREDIENT_QUANTITY,
            INGREDIENT_MEASURE,
    };
    public static final String STEP_TITLE = "shortDescription";
    public static final String STEP_BODY = "description";
    public static final String STEP_VIDEO_URL = "videoURL";
    public static final String STEP_IMAGE_URL = "thumbnailURL";
    public static final String[] STEP_CONTENT_FIELDS = new String[]{
            STEP_TITLE,
            STEP_BODY,
            STEP_VIDEO_URL,
            STEP_IMAGE_URL,
    };
}
