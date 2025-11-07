package com.example.mustang_clone;

import android.content.Intent;
import android.database.Cursor;
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

        loadCategories();

        // Example: add sample data if table is empty
        if (categoryList.isEmpty()) {
            addSampleCategory();
            loadCategories();
        }

        // Add Category Button Click
        addCategoryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Category.this, Category_Add.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload categories when returning from Add/Update activity
        loadCategories();
    }

    private void loadCategories() {
        categoryList = new ArrayList<>();
        Cursor cursor = dbHelp.getAllCategories();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                byte[] imgBytes = cursor.getBlob(2);
                String name = cursor.getString(1);
                String model = cursor.getString(3);

                String encodedImage = "";
                if (imgBytes != null) {
                    encodedImage = Base64.encodeToString(imgBytes, Base64.DEFAULT);
                }

                categoryList.add(new CategoryItem(id, encodedImage, name, model));

            } while (cursor.moveToNext());
        }

        cursor.close();

        adapter = new CategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);
    }

    private void addSampleCategory() {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cobramustang);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imgBytes = stream.toByteArray();

        long id = dbHelp.addCategory("Mustang", imgBytes, "Cobra Model");

        if (id > 0) {
            Toast.makeText(this, "Sample category added", Toast.LENGTH_SHORT).show();
        }
    }
}