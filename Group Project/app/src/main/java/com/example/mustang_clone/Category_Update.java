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

public class Category_Update extends AppCompatActivity {

    private Button updateCarImageBtn, updateCategoryBtn;
    private EditText carModelUpdate, carNameUpdate;
    private ImageView updateCategoryCarImage;
    private ShapeableImageView backButton;
    private DBHelp dbHelp;
    private byte[] selectedImageBytes = null;
    private int categoryId;
    private boolean imageChanged = false;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_update);

        // Initialize views
        updateCarImageBtn = findViewById(R.id.updateCarImageID);
        updateCategoryBtn = findViewById(R.id.addCategoryButtonID);
        carModelUpdate = findViewById(R.id.carModelUpdateID);
        carNameUpdate = findViewById(R.id.carNameUpdateID);
        updateCategoryCarImage = findViewById(R.id.updateCategoryCarImage);
        backButton = findViewById(R.id.backButtonID);

        dbHelp = new DBHelp(this);

        // Get data from intent
        Intent intent = getIntent();
        categoryId = intent.getIntExtra("CATEGORY_ID", -1);
        String categoryName = intent.getStringExtra("CATEGORY_NAME");
        String categoryModel = intent.getStringExtra("CATEGORY_MODEL");
        String categoryImage = intent.getStringExtra("CATEGORY_IMAGE");

        // Set existing data
        carNameUpdate.setText(categoryName);
        carModelUpdate.setText(categoryModel);

        if (categoryImage != null && !categoryImage.isEmpty()) {
            byte[] imgBytes = Base64.decode(categoryImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            updateCategoryCarImage.setImageBitmap(bitmap);
            selectedImageBytes = imgBytes;
        }

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            // Display selected image
                            updateCategoryCarImage.setImageBitmap(bitmap);

                            // Convert to byte array
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            selectedImageBytes = stream.toByteArray();
                            imageChanged = true;

                            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
        );

        // Image selection button
        updateCarImageBtn.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(pickIntent);
        });

        // Update category button
        updateCategoryBtn.setOnClickListener(v -> {
            String name = carNameUpdate.getText().toString().trim();
            String model = carModelUpdate.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter category name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (model.isEmpty()) {
                Toast.makeText(this, "Please enter category model", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageBytes == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            int rowsAffected = dbHelp.updateCategory(categoryId, name, selectedImageBytes, model);

            if (rowsAffected > 0) {
                Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                finish(); // Go back to Category activity
            } else {
                Toast.makeText(this, "Failed to update category", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button
        backButton.setOnClickListener(v -> finish());
    }
}