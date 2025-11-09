package com.example.mustang_clone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelp extends SQLiteOpenHelper {

    private static final String TAG = "DBHelp";
    private static final int DATABASE_VERSION = 5; // Increased version for schema change
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
    private static final String KEY_CAR_IMG = "carImg"; // Changed from carImgPath
    private static final String KEY_CATEGORY_REF_ID = "categoryID";
    private static final String KEY_RATING = "rating";

    public DBHelp(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DBHelp constructor called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate - Creating database tables");

        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + " (" +
                KEY_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_CATEGORY_NAME + " TEXT NOT NULL, " +
                KEY_CATEGORY_IMG + " BLOB, " +
                KEY_CATEGORY_MODEL + " TEXT" +
                ")";

        String CREATE_CAR_TABLE = "CREATE TABLE " + TABLE_CAR + " (" +
                KEY_CAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_CAR_NAME + " TEXT NOT NULL, " +
                KEY_CAR_MODEL + " TEXT, " +
                KEY_YEAR + " TEXT, " +
                KEY_GENERATION + " TEXT, " +
                KEY_ENGINE_TYPE + " TEXT, " +
                KEY_HORSEPOWER + " TEXT, " +
                KEY_TRANSMISSION + " TEXT, " +
                KEY_COLOR + " TEXT, " +
                KEY_CAR_IMG + " BLOB, " + // Changed to BLOB
                KEY_CATEGORY_REF_ID + " INTEGER, " +
                KEY_RATING + " REAL, " +
                "FOREIGN KEY(" + KEY_CATEGORY_REF_ID + ") REFERENCES " +
                TABLE_CATEGORY + "(" + KEY_CATEGORY_ID + ") ON DELETE CASCADE" +
                ")";

        try {
            db.execSQL(CREATE_CATEGORY_TABLE);
            Log.d(TAG, "Category table created successfully");

            db.execSQL(CREATE_CAR_TABLE);
            Log.d(TAG, "Car table created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade - Upgrading database from version " + oldVersion + " to " + newVersion);

        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
            onCreate(db);
            Log.d(TAG, "Database upgraded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database", e);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // ===================== CATEGORY CRUD =====================
    public long addCategory(String name, byte[] img, String model) {
        Log.d(TAG, "addCategory - Name: " + name + ", Model: " + model + ", Image size: " +
                (img != null ? img.length : 0) + " bytes");

        SQLiteDatabase db = null;
        long id = -1;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_CATEGORY_NAME, name);
            values.put(KEY_CATEGORY_IMG, img);
            values.put(KEY_CATEGORY_MODEL, model);
            id = db.insert(TABLE_CATEGORY, null, values);
            Log.d(TAG, "Category added with ID: " + id);
        } catch (Exception e) {
            Log.e(TAG, "Error adding category", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                Log.d(TAG, "Database closed after adding category");
            }
        }
        return id;
    }

    public Cursor getAllCategories() {
        Log.d(TAG, "getAllCategories called");
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_CATEGORY + " ORDER BY " + KEY_CATEGORY_ID + " DESC";
            cursor = db.rawQuery(query, null);
            int count = cursor != null ? cursor.getCount() : 0;
            Log.d(TAG, "getAllCategories returning cursor with " + count + " rows");
        } catch (Exception e) {
            Log.e(TAG, "Error getting all categories", e);
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
            return null;
        }
        return cursor;
    }

    public int updateCategory(int id, String name, byte[] img, String model) {
        Log.d(TAG, "updateCategory - ID: " + id + ", Name: " + name);

        SQLiteDatabase db = null;
        int rows = 0;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_CATEGORY_NAME, name);
            values.put(KEY_CATEGORY_IMG, img);
            values.put(KEY_CATEGORY_MODEL, model);
            rows = db.update(TABLE_CATEGORY, values, KEY_CATEGORY_ID + "=?",
                    new String[]{String.valueOf(id)});
            Log.d(TAG, "Updated " + rows + " category rows");
        } catch (Exception e) {
            Log.e(TAG, "Error updating category", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return rows;
    }

    public void deleteCategory(int id) {
        Log.d(TAG, "deleteCategory - ID: " + id);

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            // Delete cars first (cascade should handle this, but being explicit)
            int carsDeleted = db.delete(TABLE_CAR, KEY_CATEGORY_REF_ID + "=?",
                    new String[]{String.valueOf(id)});
            Log.d(TAG, "Deleted " + carsDeleted + " cars for category " + id);

            // Delete category
            int categoriesDeleted = db.delete(TABLE_CATEGORY, KEY_CATEGORY_ID + "=?",
                    new String[]{String.valueOf(id)});
            Log.d(TAG, "Deleted category with ID: " + id + " (rows affected: " + categoriesDeleted + ")");
        } catch (Exception e) {
            Log.e(TAG, "Error deleting category", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // ===================== CAR CRUD =====================
    public long addCar(String name, String model, String year, String generation,
                       String engineType, String horsepower, String transmission,
                       String color, byte[] img, int categoryID, double rating) {

        Log.d(TAG, "addCar - Name: " + name + ", Category ID: " + categoryID +
                ", Image size: " + (img != null ? img.length : 0) + " bytes");

        SQLiteDatabase db = null;
        long id = -1;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_CAR_NAME, name);
            values.put(KEY_CAR_MODEL, model);
            values.put(KEY_YEAR, year);
            values.put(KEY_GENERATION, generation);
            values.put(KEY_ENGINE_TYPE, engineType);
            values.put(KEY_HORSEPOWER, horsepower);
            values.put(KEY_TRANSMISSION, transmission);
            values.put(KEY_COLOR, color);
            values.put(KEY_CAR_IMG, img); // Changed to BLOB
            values.put(KEY_CATEGORY_REF_ID, categoryID);
            values.put(KEY_RATING, rating);

            id = db.insert(TABLE_CAR, null, values);
            Log.d(TAG, "Car added with ID: " + id);
        } catch (Exception e) {
            Log.e(TAG, "Error adding car", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return id;
    }

    public Cursor getCarsByCategory(int categoryID) {
        Log.d(TAG, "getCarsByCategory - Category ID: " + categoryID);

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_CAR + " WHERE " + KEY_CATEGORY_REF_ID +
                    "=? ORDER BY " + KEY_CAR_ID + " DESC";
            cursor = db.rawQuery(query, new String[]{String.valueOf(categoryID)});
            int count = cursor != null ? cursor.getCount() : 0;
            Log.d(TAG, "getCarsByCategory returning " + count + " cars");
        } catch (Exception e) {
            Log.e(TAG, "Error getting cars by category", e);
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
            return null;
        }
        return cursor;
    }

    public Cursor getAllCars() {
        Log.d(TAG, "getAllCars called");

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_CAR + " ORDER BY " + KEY_CAR_ID + " DESC";
            cursor = db.rawQuery(query, null);
            int count = cursor != null ? cursor.getCount() : 0;
            Log.d(TAG, "getAllCars returning " + count + " cars");
        } catch (Exception e) {
            Log.e(TAG, "Error getting all cars", e);
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
            return null;
        }
        return cursor;
    }

    public int updateCar(int id, String name, String model, String year,
                         String generation, String engineType, String horsepower,
                         String transmission, String color, byte[] img,
                         int categoryID, double rating) {

        Log.d(TAG, "updateCar - ID: " + id + ", Name: " + name +
                ", Image size: " + (img != null ? img.length : 0) + " bytes");

        SQLiteDatabase db = null;
        int rows = 0;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_CAR_NAME, name);
            values.put(KEY_CAR_MODEL, model);
            values.put(KEY_YEAR, year);
            values.put(KEY_GENERATION, generation);
            values.put(KEY_ENGINE_TYPE, engineType);
            values.put(KEY_HORSEPOWER, horsepower);
            values.put(KEY_TRANSMISSION, transmission);
            values.put(KEY_COLOR, color);
            values.put(KEY_CAR_IMG, img); // Changed to BLOB
            values.put(KEY_CATEGORY_REF_ID, categoryID);
            values.put(KEY_RATING, rating);

            rows = db.update(TABLE_CAR, values, KEY_CAR_ID + "=?",
                    new String[]{String.valueOf(id)});
            Log.d(TAG, "Updated " + rows + " car rows");
        } catch (Exception e) {
            Log.e(TAG, "Error updating car", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return rows;
    }

    public void deleteCar(int id) {
        Log.d(TAG, "deleteCar - ID: " + id);

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            int carsDeleted = db.delete(TABLE_CAR, KEY_CAR_ID + "=?",
                    new String[]{String.valueOf(id)});
            Log.d(TAG, "Deleted car with ID: " + id + " (rows affected: " + carsDeleted + ")");
        } catch (Exception e) {
            Log.e(TAG, "Error deleting car", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
}