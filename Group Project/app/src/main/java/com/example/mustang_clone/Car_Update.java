package com.example.mustang_clone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Car_Update extends AppCompatActivity {

    private ImageView updateCarImageBtn;
    private Button updateCarBtn;
    private EditText carModelUpdate, carNameUpdate, yearUpdate, generationUpdate;
    private EditText engineTypeUpdate, horsepowerUpdate, transmissionUpdate, colorUpdate, ratingUpdate;
    private ShapeableImageView backButton;

    private DBHelp dbHelp;
    private String savedImagePath = null;
    private int carId, categoryID;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_update);

        updateCarImageBtn = findViewById(R.id.inputCarImageID);
        updateCarBtn = findViewById(R.id.addCategoryButtonID);
        carModelUpdate = findViewById(R.id.carModelInputID);
        carNameUpdate = findViewById(R.id.carNameInputID);
        yearUpdate = findViewById(R.id.yearInputID);
        generationUpdate = findViewById(R.id.generationInputID);
        engineTypeUpdate = findViewById(R.id.engineTypeInputID);
        horsepowerUpdate = findViewById(R.id.horsepowerInputID);
        transmissionUpdate = findViewById(R.id.transmissionInputID);
        colorUpdate = findViewById(R.id.colorInputID);
        ratingUpdate = findViewById(R.id.ratingInputID);
        backButton = findViewById(R.id.backButtonID);

        dbHelp = new DBHelp(this);

        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, "No data provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carId = intent.getIntExtra("CAR_ID", -1);
        categoryID = intent.getIntExtra("CATEGORY_ID", -1);

        // Set existing data
        carNameUpdate.setText(intent.getStringExtra("CAR_NAME"));
        carModelUpdate.setText(intent.getStringExtra("CAR_MODEL"));
        yearUpdate.setText(intent.getStringExtra("YEAR"));
        generationUpdate.setText(intent.getStringExtra("GENERATION"));
        engineTypeUpdate.setText(intent.getStringExtra("ENGINE_TYPE"));
        horsepowerUpdate.setText(intent.getStringExtra("HORSEPOWER"));
        transmissionUpdate.setText(intent.getStringExtra("TRANSMISSION"));
        colorUpdate.setText(intent.getStringExtra("COLOR"));
        double ratingExtra = intent.getDoubleExtra("RATING", Double.NaN);
        if (!Double.isNaN(ratingExtra)) ratingUpdate.setText(String.valueOf(ratingExtra));

        String carImageExtra = intent.getStringExtra("CAR_IMAGE");
        if (carImageExtra != null && !carImageExtra.isEmpty()) {
            File file = new File(carImageExtra);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) updateCarImageBtn.setImageBitmap(bitmap);
                savedImagePath = file.getAbsolutePath();
            }
        }

        // --- Image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            if (imageUri != null) {
                                String newPath = copyImageToAppStorage(imageUri);
                                if (newPath != null) {
                                    savedImagePath = newPath;
                                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    if (bitmap != null) updateCarImageBtn.setImageBitmap(bitmap);
                                    Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to copy image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        updateCarImageBtn.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(pickIntent);
        });

        updateCarBtn.setOnClickListener(v -> {
            String name = carNameUpdate.getText().toString().trim();
            String model = carModelUpdate.getText().toString().trim();
            String year = yearUpdate.getText().toString().trim();
            String generation = generationUpdate.getText().toString().trim();
            String engineType = engineTypeUpdate.getText().toString().trim();
            String horsepower = horsepowerUpdate.getText().toString().trim();
            String transmission = transmissionUpdate.getText().toString().trim();
            String color = colorUpdate.getText().toString().trim();
            String ratingStr = ratingUpdate.getText().toString().trim();

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

            // ✅ Update car using IMAGE PATH (not BLOB)
            int rowsAffected = dbHelp.updateCar(
                    carId, name, model, year, generation,
                    engineType, horsepower, transmission, color,
                    savedImagePath, categoryID, rating
            );

            if (rowsAffected > 0) {
                Toast.makeText(this, "Car updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update car", Toast.LENGTH_SHORT).show();
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
