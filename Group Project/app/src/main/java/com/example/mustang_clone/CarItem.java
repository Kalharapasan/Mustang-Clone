package com.example.mustang_clone;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CarItem extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CarAdapter carAdapter;
    private List<Car> carList;
    private DBHelp dbHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_item);

        recyclerView = findViewById(R.id.carRecyclerViewCars);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelp = new DBHelp(this);
        carList = new ArrayList<>();

        loadCars();

        carAdapter = new CarAdapter(carList, this);
        recyclerView.setAdapter(carAdapter);
    }

    private void loadCars() {
        carList.clear();
        Cursor cursor = dbHelp.getAllCars();
        if (cursor.moveToFirst()) {
            do {
                Car car = new Car(
                        cursor.getInt(cursor.getColumnIndexOrThrow("carID")),
                        cursor.getString(cursor.getColumnIndexOrThrow("carName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("carModel")),
                        cursor.getString(cursor.getColumnIndexOrThrow("year")),
                        cursor.getString(cursor.getColumnIndexOrThrow("generation")),
                        cursor.getString(cursor.getColumnIndexOrThrow("engineType")),
                        cursor.getString(cursor.getColumnIndexOrThrow("horsepower")),
                        cursor.getString(cursor.getColumnIndexOrThrow("transmission")),
                        cursor.getString(cursor.getColumnIndexOrThrow("color")),
                        cursor.getString(cursor.getColumnIndexOrThrow("img")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("categoryID")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("rating"))
                );
                carList.add(car);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
