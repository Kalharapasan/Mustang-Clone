package com.example.mustang_clone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

public class CarView extends AppCompatActivity {

    private ShapeableImageView backButton;
    private ImageView carImageView, deleteBtn, editBtn;
    private TextView carName, carModel, year, generation, engineType;
    private TextView horsepower, transmission, color, ratingText;
    private RatingBar ratingBar;
    private DBHelp dbHelp;
    private int carId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_view);

        // Initialize views
        initializeViews();

        // Initialize database helper
        dbHelp = new DBHelp(this);

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            carId = intent.getIntExtra("CAR_ID", -1);
            displayCarDetails(intent);
        }

        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        carImageView = findViewById(R.id.carImageView);
        backButton = findViewById(R.id.backButtonID);
        carName = findViewById(R.id.carNameTextView);
        carModel = findViewById(R.id.carModelTextView);
        year = findViewById(R.id.yearTextView);
        generation = findViewById(R.id.generationTextView);
        engineType = findViewById(R.id.engineTypeTextView);
        horsepower = findViewById(R.id.horsepowerTextView);
        transmission = findViewById(R.id.transmissionTextView);
        color = findViewById(R.id.colorTextView);
        ratingText = findViewById(R.id.ratingTextView);
        ratingBar = findViewById(R.id.ratingBarID);
        deleteBtn = findViewById(R.id.delete_Btn_ID);
        editBtn = findViewById(R.id.edit_Btn_ID);
    }

    private void displayCarDetails(Intent intent) {
        try {
            // Get all the car details from intent
            String nameStr = intent.getStringExtra("CAR_NAME");
            String modelStr = intent.getStringExtra("CAR_MODEL");
            String yearStr = intent.getStringExtra("YEAR");
            String genStr = intent.getStringExtra("GENERATION");
            String engineStr = intent.getStringExtra("ENGINE_TYPE");
            String hpStr = intent.getStringExtra("HORSEPOWER");
            String transStr = intent.getStringExtra("TRANSMISSION");
            String colorStr = intent.getStringExtra("COLOR");
            double ratingValue = intent.getDoubleExtra("RATING", 0.0);
            String carImg = intent.getStringExtra("CAR_IMAGE");

            // Set text values with null checks
            if (nameStr != null) carName.setText(nameStr);
            if (modelStr != null) carModel.setText("Model: " + modelStr);
            if (yearStr != null) year.setText("Year: " + yearStr);
            if (genStr != null) generation.setText("Generation: " + genStr);
            if (engineStr != null) engineType.setText("Engine: " + engineStr);
            if (hpStr != null) horsepower.setText("Power: " + hpStr);
            if (transStr != null) transmission.setText("Transmission: " + transStr);
            if (colorStr != null) color.setText("Color: " + colorStr);

            // Set rating
            ratingText.setText(String.format("Rating: %.1f ⭐", ratingValue));
            ratingBar.setRating((float) ratingValue);

            // Load and display image
            if (carImg != null && !carImg.isEmpty()) {
                try {
                    byte[] imgBytes = Base64.decode(carImg, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                    if (bitmap != null) {
                        carImageView.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error displaying car details", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        // Back button click listener
        backButton.setOnClickListener(v -> finish());

        // Delete button click listener
        if (deleteBtn != null) {
            deleteBtn.setOnClickListener(v -> {
                if (carId != -1) {
                    dbHelp.deleteCar(carId);
                    Toast.makeText(this, "Car deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        // Edit button click listener
        if (editBtn != null) {
            editBtn.setOnClickListener(v -> {
                if (carId != -1) {
                    Intent editIntent = new Intent(CarView.this, Car_Update.class);
                    // Pass all the car details to the update activity
                    editIntent.putExtras(getIntent().getExtras());
                    startActivity(editIntent);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the data when coming back from edit
        if (carId != -1) {
            // You might want to reload the car data from the database here
            // For now, we'll just use the intent data
            displayCarDetails(getIntent());
        }
    }
}