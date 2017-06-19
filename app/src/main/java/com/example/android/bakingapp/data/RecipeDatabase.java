package com.example.android.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.ExecOnCreate;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by oren on 6/13/17.
 */

@Database(className = RecipeDatabase.CLASS, fileName = RecipeDatabase.NAME, version = RecipeDatabase.VERSION, packageName = RecipeDatabase.PACKAGE)
public class RecipeDatabase {
    public static final String NAME = "bakingapp.db";
    public static final String CLASS = "BakingAppDatabase";
    public static final int VERSION = 1;
    public static final String PACKAGE = "com.example.android.bakingapp.provider";
    public RecipeDatabase() {};

    public static class Tables {

        @Table(IngredientColumns.class) @IfNotExists
        public static final String INGREDIENTS = "ingredients";

        @Table(StepColumns.class) @IfNotExists
        public static final String STEPS = "steps";
    }

    @Table(RecipeColumns.class) public static final String RECIPES = "recipes";
    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
    }

    @OnUpgrade
    public static void onUpgrade(Context c, SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @OnConfigure
    public static void onConfigure(SQLiteDatabase db) {
    }

    @ExecOnCreate
    public static final String EXEC_ON_CREATE = "SELECT * FROM " + RECIPES;
}
