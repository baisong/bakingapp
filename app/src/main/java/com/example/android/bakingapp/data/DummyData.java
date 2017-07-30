package com.example.android.bakingapp.data;

import java.util.ArrayList;
import java.util.List;

public class DummyData {

    public static final String RECIPE_NAME = "name";

    private static final List<String> testData = new ArrayList<String>() {{
        add("Loading recipe 1...");
        add("Loading recipe 2...");
        add("Loading recipe 3...");
    }};

    public static List<String> getRecipeNames() {
        return testData;
    }
}
