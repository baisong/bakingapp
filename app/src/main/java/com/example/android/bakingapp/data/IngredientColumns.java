package com.example.android.bakingapp.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface IngredientColumns {

    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String ID = "_id";

    @DataType(TEXT) String INGREDIENT = "ingredient";
    @DataType(REAL) String QUANTITY = "quantity";
    @DataType(TEXT) String MEASURE = "measure";

    @DataType(INTEGER)
    @NotNull
    @References(table = RecipeDatabase.RECIPES, column = RecipeColumns.ID)
    public static final String RECIPE_ID = "recipe_id";
}
