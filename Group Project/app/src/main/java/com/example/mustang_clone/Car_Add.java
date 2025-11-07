package com.example.mustang_clone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Car_Add extends AppCompatActivity {

    private Button inputCarImageBtn, addCarBtn;
    private EditText carModelInput, carNameInput, yearInput, generationInput;
    private EditText engineTypeInput, horsepowerInput, transmissionInput, colorInput, ratingInput;
    private ShapeableImageView backButton;
    private DBHelp dbHelp;
    private byte[] selectedImageBytes = null;
    private int categoryID;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_add);

        // Initialize views
        inputCarImageBtn = findViewById(R.id.inputCarImageID);
        addCarBtn = findViewById(R.id.addCategoryButtonID);
        carModelInput = findViewById(R.id.carModelInputID);
        carNameInput = findViewById(R.id.carNameInputID);
        yearInput = findViewById(R.id.yearInputID);
        generationInput = findViewById(R.id.generationInputID);
        engineTypeInput = findViewById(R.id.engineTypeInputID);
        horsepowerInput = findViewById(R.id.horsepowerInputID);
        transmissionInput = findViewById(R.id.transmissionInputID);
        colorInput = findViewById(R.id.colorInputID);
        ratingInput = findViewById(R.id.ratingInputID);
        backButton = findViewById(R.id.backButtonID);

        dbHelp = new DBHelp(this);

        // Get category ID from intent
        categoryID = getIntent().getIntExtra("CATEGORY_ID", -1);

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            // Convert to byte array
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            selectedImageBytes = stream.toByteArray();

                            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
        );

        // Image selection button
        inputCarImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Add car button
        addCarBtn.setOnClickListener(v -> {
            String name = carNameInput.getText().toString().trim();
            String model = carModelInput.getText().toString().trim();
            String year = yearInput.getText().toString().trim();
            String generation = generationInput.getText().toString().trim();
            String engineType = engineTypeInput.getText().toString().trim();
            String horsepower = horsepowerInput.getText().toString().trim();
            String transmission = transmissionInput.getText().toString().trim();
            String color = colorInput.getText().toString().trim();
            String ratingStr = ratingInput.getText().toString().trim();

            // Validation
            if (name.isEmpty() || model.isEmpty() || year.isEmpty() || generation.isEmpty() ||
                    engineType.isEmpty() || horsepower.isEmpty() || transmission.isEmpty() ||
                    color.isEmpty() || ratingStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageBytes == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            double rating;
            try {
                rating = Double.parseDouble(ratingStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid rating format", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = dbHelp.addCar(name, model, year, generation, engineType,
                    horsepower, transmission, color, selectedImageBytes, categoryID, rating);

            if (id > 0) {
                Toast.makeText(this, "Car added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add car", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button
        backButton.setOnClickListener(v -> finish());
    }
}