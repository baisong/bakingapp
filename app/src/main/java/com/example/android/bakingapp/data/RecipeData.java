package com.example.android.bakingapp.data;

import java.util.ArrayList;
import java.util.List;

public class RecipeData {

    private final static String RECIPES_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private static final List<String> heads = new ArrayList<String>() {{
        add("Recipe 1");
        add("Recipe 2");
        add("Recipe 3");
        add("Recipe 4");
        add("Recipe 5");
        add("Recipe 6");
        add("Recipe 7");
        add("Recipe 8");
        add("Recipe 9");
        add("Recipe 10");
        add("Recipe 11");
        add("Recipe 12");
    }};
    public static List<String> getTestData() {
        return heads;
    }
}
