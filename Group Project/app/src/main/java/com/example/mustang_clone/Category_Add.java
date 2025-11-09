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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Category_Add extends AppCompatActivity {

    private static final String TAG = "Category_Add";
    private Button inputCarImageBtn, addCategoryBtn;
    private EditText carModelInput, carNameInput;
    private ImageView addCategoryCarImage;
    private ShapeableImageView backButton;
    private DBHelp dbHelp;
    private byte[] selectedImageBytes = null;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);

        // Initialize views
        inputCarImageBtn = findViewById(R.id.inputCarImageID);
        addCategoryBtn = findViewById(R.id.addCategoryButtonID);
        carModelInput = findViewById(R.id.carModelInputID);
        carNameInput = findViewById(R.id.carNameInputID);
        addCategoryCarImage = findViewById(R.id.addCategoryCarImage);
        backButton = findViewById(R.id.backButtonID);

        dbHelp = new DBHelp(this);

        // Initialize image picker launcher
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

                            // Resize bitmap to reasonable size
                            Bitmap resizedBitmap = resizeBitmap(bitmap, 800, 600);

                            // Display selected image
                            addCategoryCarImage.setImageBitmap(resizedBitmap);

                            // Convert to byte array with compression
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                            selectedImageBytes = stream.toByteArray();

                            Log.d(TAG, "Image selected, size: " + selectedImageBytes.length + " bytes");

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
                            Log.e(TAG, "Error loading image", e);
                            Toast.makeText(this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Image selection button
        inputCarImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Add category button
        addCategoryBtn.setOnClickListener(v -> {
            String name = carNameInput.getText().toString().trim();
            String model = carModelInput.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter category name", Toast.LENGTH_SHORT).show();
                carNameInput.requestFocus();
                return;
            }

            if (model.isEmpty()) {
                Toast.makeText(this, "Please enter category model", Toast.LENGTH_SHORT).show();
                carModelInput.requestFocus();
                return;
            }

            if (selectedImageBytes == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable button to prevent double-clicking
            addCategoryBtn.setEnabled(false);

            long id = dbHelp.addCategory(name, selectedImageBytes, model);

            if (id > 0) {
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Category added with ID: " + id);
                finish(); // Go back to Category activity
            } else {
                Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to add category");
                addCategoryBtn.setEnabled(true);
            }
        });

        // Back button
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
        if (addCategoryCarImage != null) {
            addCategoryCarImage.setImageDrawable(null);
        }
    }
}