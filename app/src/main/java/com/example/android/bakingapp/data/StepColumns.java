package com.example.android.bakingapp.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface StepColumns {

    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String ID = "_id";

    @DataType(TEXT) String SHORT_DESCRIPTION = "short_description";
    @DataType(TEXT) String DESCRIPTION = "description";
    @DataType(TEXT) String VIDEO_URL = "video_url";
    @DataType(TEXT) String THUMBNAIL_URL = "image_url";

    @DataType(INTEGER)
    @NotNull
    @References(table = RecipeDatabase.RECIPES, column = RecipeColumns.ID)
    public static final String RECIPE_ID = "recipe_id";
}
