package com.example.mustang_clone;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CarItem extends AppCompatActivity {

    private static final String TAG = "CarItem";
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

        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView is null!");
            Toast.makeText(this, "Error: RecyclerView not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        dbHelp = new DBHelp(this);

        categoryID = getIntent().getIntExtra("CATEGORY_ID", -1);
        Log.d(TAG, "Loading cars for category ID: " + categoryID);

        if (categoryID == -1) {
            Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carList = new ArrayList<>();
        adapter = new CarAdapter(carList, this);
        recyclerView.setAdapter(adapter);

        loadCars();

        addCarIcon.setOnClickListener(v -> {
            Intent intent = new Intent(CarItem.this, Car_Add.class);
            intent.putExtra("CATEGORY_ID", categoryID);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - reloading cars");
        loadCars();
    }

    private void loadCars() {
        Log.d(TAG, "loadCars started for category: " + categoryID);

        carList.clear();
        Cursor cursor = null;

        try {
            cursor = (categoryID != -1) ? dbHelp.getCarsByCategory(categoryID) : dbHelp.getAllCars();

            if (cursor == null) {
                Log.e(TAG, "Cursor is null!");
                Toast.makeText(this, "Error loading cars", Toast.LENGTH_SHORT).show();
                return;
            }

            int count = cursor.getCount();
            Log.d(TAG, "Cursor returned " + count + " cars");

            if (count > 0 && cursor.moveToFirst()) {
                int idCol = safeColumnIndex(cursor, "carID");
                int nameCol = safeColumnIndex(cursor, "carName");
                int modelCol = safeColumnIndex(cursor, "carModel");
                int yearCol = safeColumnIndex(cursor, "year");
                int genCol = safeColumnIndex(cursor, "generation");
                int engineCol = safeColumnIndex(cursor, "engineType");
                int hpCol = safeColumnIndex(cursor, "horsepower");
                int transCol = safeColumnIndex(cursor, "transmission");
                int colorCol = safeColumnIndex(cursor, "color");
                int imgPathCol = safeColumnIndex(cursor, "carImgPath");
                int catCol = safeColumnIndex(cursor, "categoryID");
                int ratingCol = safeColumnIndex(cursor, "rating");

                Log.d(TAG, "Column indices - ID:" + idCol + " Name:" + nameCol + " Model:" + modelCol);

                if (idCol == -1 || nameCol == -1) {
                    Log.e(TAG, "Required columns missing!");
                    Toast.makeText(this, "Database error: Missing columns", Toast.LENGTH_SHORT).show();
                    return;
                }

                do {
                    try {
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
                        String imagePath = imgPathCol >= 0 ? cursor.getString(imgPathCol) : null;

                        Car car = new Car(id, name, model, year, generation, engineType,
                                horsepower, transmission, color, imagePath, catID, rating);
                        carList.add(car);

                        Log.d(TAG, "Added car: " + name + " (ID: " + id + ")");
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading car row", e);
                    }

                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "No cars found for category " + categoryID);
                Toast.makeText(this, "No cars found. Add one using the + button.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading cars", e);
            Toast.makeText(this, "Error loading cars: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
                Log.d(TAG, "Cursor closed");
            }
        }

        Log.d(TAG, "Total cars loaded: " + carList.size());

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

    private int safeColumnIndex(Cursor cursor, String columnName) {
        try {
            return cursor.getColumnIndex(columnName);
        } catch (Exception e) {
            Log.e(TAG, "Error getting column index for: " + columnName, e);
            return -1;
        }
    }
}