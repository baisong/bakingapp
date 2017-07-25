package com.example.android.bakingapp.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = RecipeProvider.AUTHORITY,
        name = RecipeProvider.NAME,
        database = RecipeDatabase.class,
        packageName = RecipeProvider.PACKAGE
)
public final class RecipeProvider {

    public static final String NAME = "RecipeProvider";
    public static final String PACKAGE = "com.example.android.bakingapp.schematic";
    public static final String AUTHORITY = "com.example.android.bakingapp";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static final String TYPE_PREFIX = "vnd.android.cursor";
    private static final String DIR = TYPE_PREFIX + ".dir";
    private static final String ITEM = TYPE_PREFIX + ".item";

    interface Path {
        String RECIPES = "recipes";
        String INGREDIENTS = "ingredients";
        String STEPS = "steps";
        String RECIPE = "recipe";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = RecipeDatabase.RECIPES)
    public static class Recipes {

        @ContentUri(
                path = Path.RECIPES,
                type = DIR + "/" + Path.RECIPE,
                defaultSort = RecipeColumns.NAME + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.RECIPES);

        @InexactContentUri(
                path = Path.RECIPES + "/#",
                name = "LIST_ID",
                type = ITEM + "/" + Path.RECIPE,
                whereColumn = RecipeColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.RECIPES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = RecipeDatabase.Tables.INGREDIENTS)
    public static class Ingredients {

        @InexactContentUri(
                name = "INGREDIENTS_FOR_RECIPE",
                path = Path.RECIPES + "/#/" + Path.INGREDIENTS,
                type = DIR + "/" + Path.RECIPE + "/" + Path.INGREDIENTS,
                whereColumn = IngredientColumns.RECIPE_ID,
                pathSegment = 1
        )
        public static Uri fromRecipe(long recipeId) {
            return buildUri(Path.RECIPES, String.valueOf(recipeId), Path.STEPS);
        }

    }

    @TableEndpoint(table = RecipeDatabase.Tables.STEPS)
    public static class Steps {
        @InexactContentUri(
                name = "STEPS_FOR_RECIPE",
                path = Path.RECIPES + "/#/" + Path.STEPS,
                type = DIR + "/" + Path.RECIPE + "/" + Path.STEPS,
                whereColumn = StepColumns.RECIPE_ID,
                pathSegment = 1
        )
        public static Uri fromRecipe(long recipeId) {
            return buildUri(Path.RECIPES, String.valueOf(recipeId), Path.STEPS);
        }
    }
}