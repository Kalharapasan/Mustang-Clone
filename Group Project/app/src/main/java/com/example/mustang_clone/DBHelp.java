package com.example.mustang_clone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelp extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mustangDB.db";

    private static final String TABLE_CATEGORY = "Category";
    private static final String TABLE_CAR = "Car";

    private static final String KEY_CATEGORY_ID = "categoryID";
    private static final String KEY_CATEGORY_NAME = "categoryName";
    private static final String KEY_CATEGORY_IMG = "categoryImg";
    private static final String KEY_CATEGORY_MODEL = "categoryModel";

    private static final String KEY_CAR_ID = "carID";
    private static final String KEY_CAR_NAME = "carName";
    private static final String KEY_CAR_MODEL = "carModel";
    private static final String KEY_YEAR = "year";
    private static final String KEY_GENERATION = "generation";
    private static final String KEY_ENGINE_TYPE = "engineType";
    private static final String KEY_HORSEPOWER = "horsepower";
    private static final String KEY_TRANSMISSION = "transmission";
    private static final String KEY_COLOR = "color";
    private static final String KEY_IMG = "img";
    private static final String KEY_CATEGORY_REF_ID = "categoryID";
    private static final String KEY_RATING = "rating";

    public DBHelp(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + " ("
                + KEY_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_CATEGORY_NAME + " TEXT, "
                + KEY_CATEGORY_IMG + " BLOB, "
                + KEY_CATEGORY_MODEL + " TEXT"
                + ")";

        String CREATE_CAR_TABLE = "CREATE TABLE " + TABLE_CAR + " ("
                + KEY_CAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_CAR_NAME + " TEXT, "
                + KEY_CAR_MODEL + " TEXT, "
                + KEY_YEAR + " TEXT, "
                + KEY_GENERATION + " TEXT, "
                + KEY_ENGINE_TYPE + " TEXT, "
                + KEY_HORSEPOWER + " TEXT, "
                + KEY_TRANSMISSION + " TEXT, "
                + KEY_COLOR + " TEXT, "
                + KEY_IMG + " BLOB, "
                + KEY_CATEGORY_REF_ID + " INTEGER, "
                + KEY_RATING + " REAL, "
                + "FOREIGN KEY(" + KEY_CATEGORY_REF_ID + ") REFERENCES "
                + TABLE_CATEGORY + "(" + KEY_CATEGORY_ID + ")"
                + ")";

        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_CAR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(db);
    }

    // CATEGORY CRUD
    public long addCategory(String name, byte[] img, String model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, name);
        values.put(KEY_CATEGORY_IMG, img);
        values.put(KEY_CATEGORY_MODEL, model);
        long id = db.insert(TABLE_CATEGORY, null, values);
        db.close();
        return id;
    }

    public Cursor getAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CATEGORY, null);
    }

    public int updateCategory(int id, String name, byte[] img, String model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, name);
        values.put(KEY_CATEGORY_IMG, img);
        values.put(KEY_CATEGORY_MODEL, model);
        int rows = db.update(TABLE_CATEGORY, values, KEY_CATEGORY_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public void deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORY, KEY_CATEGORY_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // CAR CRUD
    public long addCar(String name, String model, String year, String generation,
                       String engineType, String horsepower, String transmission,
                       String color, byte[] img, int categoryID, double rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CAR_NAME, name);
        values.put(KEY_CAR_MODEL, model);
        values.put(KEY_YEAR, year);
        values.put(KEY_GENERATION, generation);
        values.put(KEY_ENGINE_TYPE, engineType);
        values.put(KEY_HORSEPOWER, horsepower);
        values.put(KEY_TRANSMISSION, transmission);
        values.put(KEY_COLOR, color);
        values.put(KEY_IMG, img);
        values.put(KEY_CATEGORY_REF_ID, categoryID);
        values.put(KEY_RATING, rating);
        long id = db.insert(TABLE_CAR, null, values);
        db.close();
        return id;
    }

    public Cursor getCarsByCategory(int categoryID) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CAR + " WHERE "
                + KEY_CATEGORY_REF_ID + "=?", new String[]{String.valueOf(categoryID)});
    }


    public Cursor getAllCars() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CAR, null);
    }

    public int updateCar(int id, String name, String model, String year,
                         String generation, String engineType, String horsepower,
                         String transmission, String color, byte[] img,
                         int categoryID, double rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CAR_NAME, name);
        values.put(KEY_CAR_MODEL, model);
        values.put(KEY_YEAR, year);
        values.put(KEY_GENERATION, generation);
        values.put(KEY_ENGINE_TYPE, engineType);
        values.put(KEY_HORSEPOWER, horsepower);
        values.put(KEY_TRANSMISSION, transmission);
        values.put(KEY_COLOR, color);
        values.put(KEY_IMG, img);
        values.put(KEY_CATEGORY_REF_ID, categoryID);
        values.put(KEY_RATING, rating);
        int rows = db.update(TABLE_CAR, values, KEY_CAR_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public void deleteCar(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CAR, KEY_CAR_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
