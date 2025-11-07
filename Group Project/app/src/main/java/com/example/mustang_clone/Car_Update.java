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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Car_Update extends AppCompatActivity {

    private ImageView updateCarImageBtn; // ✅ FIXED type (was Button)
    private Button updateCarBtn;
    private EditText carModelUpdate, carNameUpdate, yearUpdate, generationUpdate;
    private EditText engineTypeUpdate, horsepowerUpdate, transmissionUpdate, colorUpdate, ratingUpdate;
    private ShapeableImageView backButton;
    private DBHelp dbHelp;
    private byte[] selectedImageBytes = null;
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
        carId = intent.getIntExtra("CAR_ID", -1);
        categoryID = intent.getIntExtra("CATEGORY_ID", -1);

        carNameUpdate.setText(intent.getStringExtra("CAR_NAME"));
        carModelUpdate.setText(intent.getStringExtra("CAR_MODEL"));
        yearUpdate.setText(intent.getStringExtra("YEAR"));
        generationUpdate.setText(intent.getStringExtra("GENERATION"));
        engineTypeUpdate.setText(intent.getStringExtra("ENGINE_TYPE"));
        horsepowerUpdate.setText(intent.getStringExtra("HORSEPOWER"));
        transmissionUpdate.setText(intent.getStringExtra("TRANSMISSION"));
        colorUpdate.setText(intent.getStringExtra("COLOR"));
        ratingUpdate.setText(String.valueOf(intent.getDoubleExtra("RATING", 0.0)));

        String carImage = intent.getStringExtra("CAR_IMAGE");
        if (carImage != null && !carImage.isEmpty()) {
            selectedImageBytes = Base64.decode(carImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(selectedImageBytes, 0, selectedImageBytes.length);
            updateCarImageBtn.setImageBitmap(bitmap);
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            updateCarImageBtn.setImageBitmap(bitmap);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            selectedImageBytes = stream.toByteArray();
                        } catch (Exception e) {
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
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

            double rating;
            try {
                rating = Double.parseDouble(ratingStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid rating format", Toast.LENGTH_SHORT).show();
                return;
            }

            int rowsAffected = dbHelp.updateCar(carId, name, model, year, generation,
                    engineType, horsepower, transmission, color, selectedImageBytes, categoryID, rating);

            if (rowsAffected > 0) {
                Toast.makeText(this, "Car updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update car", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> finish());
    }
}
