package com.example.mustang_clone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

public class CarView extends AppCompatActivity {

    private ShapeableImageView carImage, backButton;
    private TextView carName, carModel, year, generation, engineType;
    private TextView horsepower, transmission, color, rating;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_view);

        carImage = findViewById(R.id.carImageView);
        backButton = findViewById(R.id.backButtonID);
        carName = findViewById(R.id.carNameTextView);
        carModel = findViewById(R.id.carModelTextView);
        year = findViewById(R.id.yearTextView);
        generation = findViewById(R.id.generationTextView);
        engineType = findViewById(R.id.engineTypeTextView);
        horsepower = findViewById(R.id.horsepowerTextView);
        transmission = findViewById(R.id.transmissionTextView);
        color = findViewById(R.id.colorTextView);
        rating = findViewById(R.id.ratingTextView);

        Intent intent = getIntent();
        String name = intent.getStringExtra("CAR_NAME");
        String model = intent.getStringExtra("CAR_MODEL");
        String yearStr = intent.getStringExtra("YEAR");
        String gen = intent.getStringExtra("GENERATION");
        String engine = intent.getStringExtra("ENGINE_TYPE");
        String hp = intent.getStringExtra("HORSEPOWER");
        String trans = intent.getStringExtra("TRANSMISSION");
        String col = intent.getStringExtra("COLOR");
        String carImg = intent.getStringExtra("CAR_IMAGE");
        double rat = intent.getDoubleExtra("RATING", 0.0);

        carName.setText(name);
        carModel.setText(model);
        year.setText("Year: " + yearStr);
        generation.setText("Generation: " + gen);
        engineType.setText("Engine: " + engine);
        horsepower.setText("Power: " + hp);
        transmission.setText("Transmission: " + trans);
        color.setText("Color: " + col);
        rating.setText("Rating: " + rat + " ⭐");

        if (carImg != null && !carImg.trim().isEmpty()) {
            try {
                byte[] imgBytes = Base64.decode(carImg, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                if (bitmap != null) carImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        backButton.setOnClickListener(v -> finish());
    }
}
