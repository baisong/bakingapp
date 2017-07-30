package com.example.android.bakingapp.data;

import java.util.ArrayList;
import java.util.List;

public class Recipes {

    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_SERVINGS = "servings";
    public static final String RECIPE_IMAGE = "image_url";

    public static final String INGREDIENT_NAME = "ingredient";
    public static final String INGREDIENT_QUANTITY = "quantity";
    public static final String INGREDIENT_MEASURE = "measure";

    public static final String STEP_SHORT_DESCRIPTION = "short_description";
    public static final String STEP_DESCRIPTION = "description";
    public static final String STEP_VIDEO_URL = "video_url";

    private final static String RECIPES_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private static final List<String> testData = new ArrayList<String>() {{
        add("Loading recipe 1...");
        add("Loading recipe 2...");
        add("Loading recipe 3...");
    }};

    public static List<String> getDummyRecipeNames() {
        return testData;
    }
}
