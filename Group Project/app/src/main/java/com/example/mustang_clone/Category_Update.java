package com.example.mustang_clone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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

public class Category_Update extends AppCompatActivity {

    private static final String TAG = "Category_Update";
    private Button updateCarImageBtn, updateCategoryBtn;
    private EditText carModelUpdate, carNameUpdate;
    private ImageView updateCategoryCarImage;
    private ShapeableImageView backButton;
    private DBHelp dbHelp;
    private int categoryId;
    private byte[] selectedImageBytes = null;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_update);

        updateCarImageBtn = findViewById(R.id.updateCarImageID);
        updateCategoryBtn = findViewById(R.id.addCategoryButtonID);
        carModelUpdate = findViewById(R.id.carModelUpdateID);
        carNameUpdate = findViewById(R.id.carNameUpdateID);
        updateCategoryCarImage = findViewById(R.id.updateCategoryCarImage);
        backButton = findViewById(R.id.backButtonID);

        dbHelp = new DBHelp(this);

        Intent intent = getIntent();
        if (intent != null) {
            categoryId = intent.getIntExtra("categoryID", -1);
            String categoryName = intent.getStringExtra("categoryName");
            String categoryModel = intent.getStringExtra("categoryModel");
            String categoryImage = intent.getStringExtra("categoryImg");

            Log.d(TAG, "Updating category ID: " + categoryId);

            if (categoryName != null) {
                carNameUpdate.setText(categoryName);
            }
            if (categoryModel != null) {
                carModelUpdate.setText(categoryModel);
            }

            if (categoryImage != null && !categoryImage.isEmpty()) {
                try {
                    byte[] imgBytes = Base64.decode(categoryImage, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                    if (bitmap != null) {
                        updateCategoryCarImage.setImageBitmap(bitmap);
                        selectedImageBytes = imgBytes; // Keep existing image
                        Log.d(TAG, "Existing image loaded successfully");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading existing image", e);
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "No data provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            if (bitmap == null) {
                                Toast.makeText(this, "Failed to decode image", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Resize bitmap
                            Bitmap resizedBitmap = resizeBitmap(bitmap, 800, 600);

                            // Display new image
                            updateCategoryCarImage.setImageBitmap(resizedBitmap);

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

                            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();

                            // Clean up
                            stream.close();
                            if (inputStream != null) inputStream.close();
                            if (!bitmap.isRecycled() && bitmap != resizedBitmap) {
                                bitmap.recycle();
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "Error loading new image", e);
                            Toast.makeText(this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        updateCarImageBtn.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(pickIntent);
        });

        updateCategoryBtn.setOnClickListener(v -> {
            String name = carNameUpdate.getText().toString().trim();
            String model = carModelUpdate.getText().toString().trim();

            if (categoryId == -1) {
                Toast.makeText(this, "Invalid category ID", Toast.LENGTH_SHORT).show();
                return;
            }

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter category name", Toast.LENGTH_SHORT).show();
                carNameUpdate.requestFocus();
                return;
            }

            if (model.isEmpty()) {
                Toast.makeText(this, "Please enter category model", Toast.LENGTH_SHORT).show();
                carModelUpdate.requestFocus();
                return;
            }

            if (selectedImageBytes == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable button to prevent double-clicking
            updateCategoryBtn.setEnabled(false);

            int rows = dbHelp.updateCategory(categoryId, name, selectedImageBytes, model);

            if (rows > 0) {
                Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Category updated successfully");
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update category", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to update category");
                updateCategoryBtn.setEnabled(true);
            }
        });

        backButton.setOnClickListener(v -> finish());
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
        // Clean up to prevent memory leaks
        if (updateCategoryCarImage != null) {
            updateCategoryCarImage.setImageDrawable(null);
        }
    }
}