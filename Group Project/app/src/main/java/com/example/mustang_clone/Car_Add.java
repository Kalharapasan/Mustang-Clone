package com.example.mustang_clone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

    private static final String TAG = "Car_Add";
    private Button inputCarImageBtn, addCarBtn;
    private EditText carModelInput, carNameInput, yearInput, generationInput;
    private EditText engineTypeInput, horsepowerInput, transmissionInput, colorInput, ratingInput;
    private ShapeableImageView backButton, addCarImageView;
    private DBHelp dbHelp;
    private byte[] selectedImageBytes = null;
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

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            handleImageSelection(imageUri);
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

            if (selectedImageBytes == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            double rating;
            try {
                rating = Double.parseDouble(ratingStr);
                if (rating < 0 || rating > 5) {
                    Toast.makeText(this, "Rating must be between 0 and 5", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid rating format", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = dbHelp.addCar(name, model, year, generation, engineType,
                    horsepower, transmission, color, selectedImageBytes, categoryID, rating);

            if (id > 0) {
                Toast.makeText(this, "Car added successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Car added with ID: " + id);
                finish();
            } else {
                Toast.makeText(this, "Failed to add car", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to add car");
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void handleImageSelection(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Toast.makeText(this, "Failed to read image", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap == null) {
                Toast.makeText(this, "Failed to decode image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Resize bitmap to reasonable size (max 800x600)
            Bitmap resizedBitmap = resizeBitmap(bitmap, 800, 600);

            // Convert to byte array with compression
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            selectedImageBytes = stream.toByteArray();

            Log.d(TAG, "Image selected, size: " + selectedImageBytes.length + " bytes");

            // If still too large (> 500KB), compress more
            if (selectedImageBytes.length > 500000) {
                stream.reset();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                selectedImageBytes = stream.toByteArray();
                Log.d(TAG, "Image recompressed, new size: " + selectedImageBytes.length + " bytes");
            }

            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();

            // Clean up
            stream.close();
            if (!resizedBitmap.isRecycled()) {
                resizedBitmap.recycle();
            }
            if (!bitmap.isRecycled() && bitmap != resizedBitmap) {
                bitmap.recycle();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading image", e);
            Toast.makeText(this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap resizeBitmap(Bitmap image, int maxWidth, int maxHeight) {
        if (image == null) return null;

        int width = image.getWidth();
        int height = image.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
    }
}