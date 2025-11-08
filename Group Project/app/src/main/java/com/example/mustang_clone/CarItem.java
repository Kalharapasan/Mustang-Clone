package com.example.mustang_clone;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CarItem extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView addCarIcon;
    private DBHelp dbHelp;
    private CarAdapter adapter;
    private List<Car> carList;
    private int categoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_item);

        recyclerView = findViewById(R.id.main);
        addCarIcon = findViewById(R.id.addCategoryIconID);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelp = new DBHelp(this);

        // Get category ID from intent
        categoryID = getIntent().getIntExtra("CATEGORY_ID", -1);

        loadCars();

        // Add car button
        addCarIcon.setOnClickListener(v -> {
            Intent intent = new Intent(CarItem.this, Car_Add.class);
            intent.putExtra("CATEGORY_ID", categoryID);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCars();
    }

    private void loadCars() {
        carList = new ArrayList<>();
        Cursor cursor = null;

        try {
            if (categoryID != -1) {
                cursor = dbHelp.getCarsByCategory(categoryID);
            } else {
                cursor = dbHelp.getAllCars();
            }

            if (cursor != null && cursor.moveToFirst()) {
                // findColumnIndex safely once
                int idCol = safeColumnIndex(cursor, "carID");
                int nameCol = safeColumnIndex(cursor, "carName");
                int modelCol = safeColumnIndex(cursor, "carModel");
                int yearCol = safeColumnIndex(cursor, "year");
                int genCol = safeColumnIndex(cursor, "generation");
                int engineCol = safeColumnIndex(cursor, "engineType");
                int hpCol = safeColumnIndex(cursor, "horsepower");
                int transCol = safeColumnIndex(cursor, "transmission");
                int colorCol = safeColumnIndex(cursor, "color");
                int imgBlobCol = safeColumnIndex(cursor, "img"); // old BLOB column
                int imgPathCol = safeColumnIndex(cursor, "imgPath"); // new TEXT column
                int catCol = safeColumnIndex(cursor, "categoryID");
                int ratingCol = safeColumnIndex(cursor, "rating");

                do {
                    int id = idCol >= 0 ? cursor.getInt(idCol) : -1;
                    String name = nameCol >= 0 ? cursor.getString(nameCol) : "";
                    String model = modelCol >= 0 ? cursor.getString(modelCol) : "";
                    String year = yearCol >= 0 ? cursor.getString(yearCol) : "";
                    String generation = genCol >= 0 ? cursor.getString(genCol) : "";
                    String engineType = engineCol >= 0 ? cursor.getString(engineCol) : "";
                    String horsepower = hpCol >= 0 ? cursor.getString(hpCol) : "";
                    String transmission = transCol >= 0 ? cursor.getString(transCol) : "";
                    String color = colorCol >= 0 ? cursor.getString(colorCol) : "";
                    int catID = catCol >= 0 ? cursor.getInt(catCol) : -1;
                    double rating = ratingCol >= 0 ? cursor.getDouble(ratingCol) : 0.0;

                    String encodedImage = "";

                    // 1) Prefer new imgPath TEXT column (file path). If exists, read file bytes and encode to Base64.
                    if (imgPathCol >= 0) {
                        String imgPath = cursor.getString(imgPathCol);
                        if (imgPath != null && !imgPath.isEmpty()) {
                            try {
                                byte[] fileBytes = dbHelp.readImageFile(imgPath);
                                if (fileBytes != null && fileBytes.length > 0) {
                                    encodedImage = Base64.encodeToString(fileBytes, Base64.DEFAULT);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // 2) Fallback: if encodedImage still empty, try the old BLOB column "img"
                    if ((encodedImage == null || encodedImage.isEmpty()) && imgBlobCol >= 0) {
                        try {
                            byte[] imgBytes = cursor.getBlob(imgBlobCol);
                            if (imgBytes != null && imgBytes.length > 0) {
                                encodedImage = Base64.encodeToString(imgBytes, Base64.DEFAULT);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // Add to list (carImg contains either Base64 data or empty string)
                    carList.add(new Car(id, name, model, year, generation, engineType,
                            horsepower, transmission, color, encodedImage, catID, rating));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading cars", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        adapter = new CarAdapter(carList, this);
        recyclerView.setAdapter(adapter);
    }

    // Helper: get column index safely (returns -1 if missing)
    private int safeColumnIndex(Cursor cursor, String columnName) {
        try {
            return cursor.getColumnIndex(columnName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
