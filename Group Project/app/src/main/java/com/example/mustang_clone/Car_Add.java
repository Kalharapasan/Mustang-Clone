package com.example.mustang_clone;

import android.content.Intent;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Car_Add extends AppCompatActivity {

    private Button inputCarImageBtn, addCarBtn;
    private EditText carModelInput, carNameInput, yearInput, generationInput;
    private EditText engineTypeInput, horsepowerInput, transmissionInput, colorInput, ratingInput;
    private ShapeableImageView backButton;
    private DBHelp dbHelp;
    private String savedImagePath = null;
    private int categoryID;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_add);

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
        categoryID = getIntent().getIntExtra("CATEGORY_ID", -1);

        // --- Image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            savedImagePath = copyImageToAppStorage(imageUri);
                            if (savedImagePath != null) {
                                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to copy image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        inputCarImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

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

            if (name.isEmpty() || model.isEmpty() || year.isEmpty() || generation.isEmpty() ||
                    engineType.isEmpty() || horsepower.isEmpty() || transmission.isEmpty() ||
                    color.isEmpty() || ratingStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (savedImagePath == null) {
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

            // ✅ Add car using IMAGE PATH (not BLOB)
            long id = dbHelp.addCar(name, model, year, generation, engineType,
                    horsepower, transmission, color, savedImagePath, categoryID, rating);

            if (id > 0) {
                Toast.makeText(this, "Car added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add car", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private String copyImageToAppStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File directory = new File(getFilesDir(), "car_images");
            if (!directory.exists() && !directory.mkdirs()) return null;

            String fileName = "car_" + System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);

            try (OutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
