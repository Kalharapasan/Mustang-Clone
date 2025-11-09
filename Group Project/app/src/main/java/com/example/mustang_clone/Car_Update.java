package com.example.mustang_clone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Car_Update extends AppCompatActivity {

    private static final String TAG = "Car_Update";
    private AppCompatButton updateCarImageBtn;  // Changed from ImageView to AppCompatButton
    private AppCompatButton updateCarBtn;
    private EditText carModelUpdate, carNameUpdate, yearUpdate, generationUpdate;
    private EditText engineTypeUpdate, horsepowerUpdate, transmissionUpdate, colorUpdate, ratingUpdate;
    private ShapeableImageView backButton;

    private DBHelp dbHelp;
    private byte[] selectedImageBytes = null;
    private int carId = -1;
    private int categoryID = -1;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_car_update);
            Log.d(TAG, "onCreate started");

            // Initialize all views first
            initializeViews();

            // Initialize database helper
            dbHelp = new DBHelp(this);

            // Get intent and extract data
            Intent intent = getIntent();
            if (intent == null) {
                Log.e(TAG, "Intent is null");
                Toast.makeText(this, "Error: No data received", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Extract data from intent with default values
            carId = intent.getIntExtra("CAR_ID", -1);
            categoryID = intent.getIntExtra("CATEGORY_ID", -1);

            Log.d(TAG, "Car ID: " + carId);
            Log.d(TAG, "Category ID: " + categoryID);

            // Validate essential data
            if (carId == -1) {
                Log.e(TAG, "Invalid car ID");
                Toast.makeText(this, "Error: Invalid car ID", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Load car data into fields
            loadCarData(intent);

            // Setup image picker
            setupImagePicker();

            // Setup click listeners
            setupClickListeners();

            Log.d(TAG, "onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing update screen: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            updateCarImageBtn = findViewById(R.id.inputCarImageID);  // AppCompatButton in XML
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

            // Verify all views are not null
            if (updateCarImageBtn == null || updateCarBtn == null || carModelUpdate == null ||
                    carNameUpdate == null || yearUpdate == null || generationUpdate == null ||
                    engineTypeUpdate == null || horsepowerUpdate == null || transmissionUpdate == null ||
                    colorUpdate == null || ratingUpdate == null || backButton == null) {
                throw new RuntimeException("One or more views not found in layout");
            }

            Log.d(TAG, "All views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            throw e;
        }
    }

    private void loadCarData(Intent intent) {
        try {
            // Get all data with null checks
            String carName = intent.getStringExtra("CAR_NAME");
            String carModel = intent.getStringExtra("CAR_MODEL");
            String year = intent.getStringExtra("YEAR");
            String generation = intent.getStringExtra("GENERATION");
            String engineType = intent.getStringExtra("ENGINE_TYPE");
            String horsepower = intent.getStringExtra("HORSEPOWER");
            String transmission = intent.getStringExtra("TRANSMISSION");
            String color = intent.getStringExtra("COLOR");
            double rating = intent.getDoubleExtra("RATING", 0.0);
            String carImageBase64 = intent.getStringExtra("CAR_IMAGE");

            // Set text fields with null safety
            if (carName != null && !carName.isEmpty()) {
                carNameUpdate.setText(carName);
            }
            if (carModel != null && !carModel.isEmpty()) {
                carModelUpdate.setText(carModel);
            }
            if (year != null && !year.isEmpty()) {
                yearUpdate.setText(year);
            }
            if (generation != null && !generation.isEmpty()) {
                generationUpdate.setText(generation);
            }
            if (engineType != null && !engineType.isEmpty()) {
                engineTypeUpdate.setText(engineType);
            }
            if (horsepower != null && !horsepower.isEmpty()) {
                horsepowerUpdate.setText(horsepower);
            }
            if (transmission != null && !transmission.isEmpty()) {
                transmissionUpdate.setText(transmission);
            }
            if (color != null && !color.isEmpty()) {
                colorUpdate.setText(color);
            }
            if (rating > 0) {
                ratingUpdate.setText(String.valueOf(rating));
            }

            // Load existing image - update button text if image exists
            loadExistingImage(carImageBase64);

            Log.d(TAG, "Car data loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error loading car data", e);
            Toast.makeText(this, "Some data could not be loaded", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadExistingImage(String carImageBase64) {
        try {
            if (carImageBase64 != null && !carImageBase64.isEmpty()) {
                Log.d(TAG, "Loading existing image from Base64");

                byte[] imgBytes = Base64.decode(carImageBase64, Base64.DEFAULT);
                selectedImageBytes = imgBytes; // Keep existing image

                // Update button text to show image is loaded
                updateCarImageBtn.setText("Image Loaded ✓");
                Log.d(TAG, "Existing image loaded successfully, size: " + imgBytes.length + " bytes");
            } else {
                Log.d(TAG, "No image data provided");
                updateCarImageBtn.setText("Choose Car Image");
                selectedImageBytes = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading existing image", e);
            updateCarImageBtn.setText("Choose Car Image");
            selectedImageBytes = null;
        }
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            handleNewImageSelection(imageUri);
                        }
                    }
                }
        );
    }

    private void handleNewImageSelection(Uri imageUri) {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                if (bitmap != null) {
                    // Resize bitmap
                    Bitmap resizedBitmap = resizeBitmap(bitmap, 800, 600);

                    // Convert to byte array with compression
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                    selectedImageBytes = stream.toByteArray();

                    Log.d(TAG, "New image selected, size: " + selectedImageBytes.length + " bytes");

                    // If still too large, compress more
                    if (selectedImageBytes.length > 500000) {
                        stream.reset();
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                        selectedImageBytes = stream.toByteArray();
                        Log.d(TAG, "Image recompressed, new size: " + selectedImageBytes.length + " bytes");
                    }

                    // Update button text
                    updateCarImageBtn.setText("Image Selected ✓");
                    Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();

                    // Clean up
                    stream.close();
                    if (!bitmap.isRecycled() && bitmap != resizedBitmap) {
                        bitmap.recycle();
                    }
                } else {
                    Toast.makeText(this, "Failed to decode image", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to decode bitmap");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling new image selection", e);
            Toast.makeText(this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error closing input stream", e);
            }
        }
    }

    private void setupClickListeners() {
        updateCarImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    imagePickerLauncher.launch(pickIntent);
                } catch (Exception e) {
                    Log.e(TAG, "Error launching image picker", e);
                    Toast.makeText(Car_Update.this, "Error opening image picker", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCar();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateCar() {
        try {
            // Get all input values
            String name = carNameUpdate.getText().toString().trim();
            String model = carModelUpdate.getText().toString().trim();
            String year = yearUpdate.getText().toString().trim();
            String generation = generationUpdate.getText().toString().trim();
            String engineType = engineTypeUpdate.getText().toString().trim();
            String horsepower = horsepowerUpdate.getText().toString().trim();
            String transmission = transmissionUpdate.getText().toString().trim();
            String color = colorUpdate.getText().toString().trim();
            String ratingStr = ratingUpdate.getText().toString().trim();

            // Validate car ID
            if (carId == -1) {
                Toast.makeText(this, "Invalid car ID", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Invalid car ID in updateCar");
                return;
            }

            // Validate all fields are filled
            if (name.isEmpty() || model.isEmpty() || year.isEmpty() || generation.isEmpty() ||
                    engineType.isEmpty() || horsepower.isEmpty() || transmission.isEmpty() ||
                    color.isEmpty() || ratingStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate image is selected
            if (selectedImageBytes == null || selectedImageBytes.length == 0) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate rating
            double rating;
            try {
                rating = Double.parseDouble(ratingStr);
                if (rating < 0 || rating > 5) {
                    Toast.makeText(this, "Rating must be between 0 and 5", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid rating format", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Invalid rating format: " + ratingStr, e);
                return;
            }

            // Disable button to prevent double-clicking
            updateCarBtn.setEnabled(false);
            updateCarBtn.setText("Updating...");

            // Update car in database
            int rows = dbHelp.updateCar(
                    carId, name, model, year, generation,
                    engineType, horsepower, transmission, color,
                    selectedImageBytes, categoryID, rating
            );

            if (rows > 0) {
                Toast.makeText(this, "Car updated successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Car updated successfully - ID: " + carId);
                finish();
            } else {
                Toast.makeText(this, "Failed to update car", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to update car - rows affected: " + rows);
                updateCarBtn.setEnabled(true);
                updateCarBtn.setText("UPDATE CAR");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in updateCar", e);
            Toast.makeText(this, "Error updating car: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            updateCarBtn.setEnabled(true);
            updateCarBtn.setText("UPDATE CAR");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Activity destroyed");
    }
}