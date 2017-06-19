package com.example.android.bakingapp.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by oren on 6/13/17.
 */

public interface RecipeColumns {
    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    static final String ID = "_id";

    @DataType(TEXT) public static final String NAME = "name";
    @DataType(INTEGER) String SERVINGS = "servings";
    @DataType(TEXT) String IMAGE = "image_url";
}
