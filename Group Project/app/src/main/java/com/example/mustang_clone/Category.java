package com.example.mustang_clone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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

    private RecyclerView recyclerView;
    private DBHelp dbHelp;
    private CategoryAdapter adapter;
    private List<CategoryItem> categoryList;
    private FloatingActionButton addCategoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView = findViewById(R.id.main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addCategoryBtn = findViewById(R.id.addCategoryBtn);

        dbHelp = new DBHelp(this);
        categoryList = new ArrayList<>();

        loadCategories();

        // Add sample data if empty
        if (categoryList.isEmpty()) {
            addSampleCategory();
            loadCategories();
        }

        addCategoryBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(Category.this, Category_Add.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    private void loadCategories() {
        categoryList = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = dbHelp.getAllCategories();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex("categoryID");
                    int nameIndex = cursor.getColumnIndex("categoryName");
                    int imgIndex = cursor.getColumnIndex("categoryImg");
                    int modelIndex = cursor.getColumnIndex("categoryModel");

                    if (idIndex == -1 || nameIndex == -1 || imgIndex == -1 || modelIndex == -1) {
                        Toast.makeText(this, "Error: Missing DB column", Toast.LENGTH_SHORT).show();
                        continue;
                    }

                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    String model = cursor.getString(modelIndex);

                    String encodedImage = "";
                    byte[] imgBytes = cursor.getBlob(imgIndex);
                    if (imgBytes != null && imgBytes.length > 0) {
                        encodedImage = Base64.encodeToString(imgBytes, Base64.DEFAULT);
                    }

                    categoryList.add(new CategoryItem(id, encodedImage, name, model));

                } while (cursor.moveToNext());
            } else {
                // it's okay if no categories yet
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading categories: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
        }

        adapter = new CategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);
    }

    private void addSampleCategory() {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cobramustang);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imgBytes = stream.toByteArray();

            long id = dbHelp.addCategory("Mustang", imgBytes, "Cobra Model");
            if (id > 0) Toast.makeText(this, "Sample category added", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
