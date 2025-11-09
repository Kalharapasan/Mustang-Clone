package com.example.mustang_clone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;

public class Category extends AppCompatActivity {

    private static final String TAG = "Category";
    private RecyclerView recyclerView;
    private DBHelp dbHelp;
    private CategoryAdapter adapter;
    private List<CategoryItem> categoryList;
    private FloatingActionButton addCategoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Log.d(TAG, "onCreate started");

        recyclerView = findViewById(R.id.main);
        addCategoryBtn = findViewById(R.id.addCategoryBtn);

        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView is null!");
            Toast.makeText(this, "Error: RecyclerView not found", Toast.LENGTH_SHORT).show();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        dbHelp = new DBHelp(this);
        categoryList = new ArrayList<>();

        // Initialize adapter first
        adapter = new CategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);

        loadCategories();

        // Add sample data if empty
        if (categoryList.isEmpty()) {
            Log.d(TAG, "No categories found, adding sample category");
            addSampleCategory();
            loadCategories();
        }

        addCategoryBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(Category.this, Category_Add.class));
        });

        Log.d(TAG, "onCreate completed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - reloading categories");
        loadCategories();
    }

    private void loadCategories() {
        Log.d(TAG, "loadCategories started");

        categoryList.clear();
        Cursor cursor = null;

        try {
            cursor = dbHelp.getAllCategories();

            if (cursor == null) {
                Log.e(TAG, "Cursor is null!");
                Toast.makeText(this, "Error: Could not load categories", Toast.LENGTH_SHORT).show();
                return;
            }

            int count = cursor.getCount();
            Log.d(TAG, "Cursor returned " + count + " categories");

            if (count > 0 && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("categoryID");
                int nameIndex = cursor.getColumnIndex("categoryName");
                int imgIndex = cursor.getColumnIndex("categoryImg");
                int modelIndex = cursor.getColumnIndex("categoryModel");

                Log.d(TAG, "Column indices - ID:" + idIndex + " Name:" + nameIndex +
                        " Img:" + imgIndex + " Model:" + modelIndex);

                if (idIndex == -1 || nameIndex == -1 || imgIndex == -1 || modelIndex == -1) {
                    Log.e(TAG, "Error: Missing DB column");
                    Toast.makeText(this, "Database error: Missing columns", Toast.LENGTH_SHORT).show();
                    return;
                }

                do {
                    try {
                        int id = cursor.getInt(idIndex);
                        String name = cursor.getString(nameIndex);
                        String model = cursor.getString(modelIndex);

                        Log.d(TAG, "Reading category - ID:" + id + " Name:" + name + " Model:" + model);

                        String encodedImage = "";
                        byte[] imgBytes = cursor.getBlob(imgIndex);
                        if (imgBytes != null && imgBytes.length > 0) {
                            encodedImage = Base64.encodeToString(imgBytes, Base64.DEFAULT);
                            Log.d(TAG, "Image encoded, size: " + imgBytes.length + " bytes");
                        } else {
                            Log.w(TAG, "No image data for category: " + name);
                        }

                        CategoryItem item = new CategoryItem(id, encodedImage, name, model);
                        categoryList.add(item);
                        Log.d(TAG, "Added category to list: " + name);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading category row", e);
                    }

                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "No categories found in database");
                Toast.makeText(this, "No categories found. Add one using the + button.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading categories", e);
            Toast.makeText(this, "Error loading categories: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
                Log.d(TAG, "Cursor closed");
            }
        }

        Log.d(TAG, "Total categories in list: " + categoryList.size());

        // Notify adapter on main thread
        runOnUiThread(() -> {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Adapter notified of data change");
            } else {
                Log.e(TAG, "Adapter is null!");
            }
        });
    }

    private void addSampleCategory() {
        Log.d(TAG, "addSampleCategory started");
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cobramustang);

            if (bmp == null) {
                Log.e(TAG, "Failed to decode sample image resource");
                Toast.makeText(this, "Sample image not found in resources", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Sample image decoded successfully - Original size: " + bmp.getWidth() + "x" + bmp.getHeight());

            // Resize image to reasonable size (max 800x600)
            Bitmap resizedBmp = resizeBitmap(bmp, 800, 600);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // Compress as JPEG with quality 60 to reduce size
            resizedBmp.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] imgBytes = stream.toByteArray();

            Log.d(TAG, "Image compressed, size: " + imgBytes.length + " bytes");

            // If still too large (> 500KB), compress more
            if (imgBytes.length > 500000) {
                Log.w(TAG, "Image still large, compressing more");
                stream.reset();
                resizedBmp.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                imgBytes = stream.toByteArray();
                Log.d(TAG, "Recompressed image size: " + imgBytes.length + " bytes");
            }

            long id = dbHelp.addCategory("Mustang", imgBytes, "Cobra Model");

            if (id > 0) {
                Log.d(TAG, "Sample category added with ID: " + id);
                Toast.makeText(this, "Sample category added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to add sample category - ID: " + id);
                Toast.makeText(this, "Failed to add sample category", Toast.LENGTH_SHORT).show();
            }

            // Clean up
            stream.close();
            if (!resizedBmp.isRecycled()) {
                resizedBmp.recycle();
            }
            if (!bmp.isRecycled() && bmp != resizedBmp) {
                bmp.recycle();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error adding sample category", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap resizeBitmap(Bitmap image, int maxWidth, int maxHeight) {
        if (image == null) return null;

        int width = image.getWidth();
        int height = image.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
    }
}