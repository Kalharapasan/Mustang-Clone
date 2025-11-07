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
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("carID"));
                    String name = cursor.getString(cursor.getColumnIndex("carName"));
                    String model = cursor.getString(cursor.getColumnIndex("carModel"));
                    String year = cursor.getString(cursor.getColumnIndex("year"));
                    String generation = cursor.getString(cursor.getColumnIndex("generation"));
                    String engineType = cursor.getString(cursor.getColumnIndex("engineType"));
                    String horsepower = cursor.getString(cursor.getColumnIndex("horsepower"));
                    String transmission = cursor.getString(cursor.getColumnIndex("transmission"));
                    String color = cursor.getString(cursor.getColumnIndex("color"));
                    byte[] imgBytes = cursor.getBlob(cursor.getColumnIndex("img"));
                    int catID = cursor.getInt(cursor.getColumnIndex("categoryID"));
                    double rating = cursor.getDouble(cursor.getColumnIndex("rating"));

                    String encodedImage = "";
                    if (imgBytes != null && imgBytes.length > 0) {
                        encodedImage = Base64.encodeToString(imgBytes, Base64.DEFAULT);
                    }

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
}
