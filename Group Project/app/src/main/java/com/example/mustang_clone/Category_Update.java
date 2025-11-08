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
import java.io.InputStream;

public class Category_Update extends AppCompatActivity {

    private Button updateCarImageBtn, updateCategoryBtn;
    private EditText carModelUpdate, carNameUpdate;
    private ImageView updateCategoryCarImage;
    private ShapeableImageView backButton;
    private DBHelp dbHelp;
    private int categoryId;
    private String savedImagePath = null;

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

            if (categoryName != null) carNameUpdate.setText(categoryName);
            if (categoryModel != null) carModelUpdate.setText(categoryModel);

            // ✅ Fixed image handling (supports both Base64 and file paths)
            if (categoryImage != null && !categoryImage.isEmpty()) {
                try {
                    File imgFile = new File(categoryImage);
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        updateCategoryCarImage.setImageBitmap(bitmap);
                        savedImagePath = categoryImage;
                    } else {
                        byte[] imgBytes = Base64.decode(categoryImage, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                        updateCategoryCarImage.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            savedImagePath = imageUri.getPath();
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            updateCategoryCarImage.setImageBitmap(bitmap);
                            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
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

            if (name.isEmpty() || model.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (savedImagePath == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert file to bytes for DB
            byte[] imageBytes = dbHelp.readImageFile(savedImagePath);
            int rows = dbHelp.updateCategory(categoryId, name, imageBytes, model);
            if (rows > 0) {
                Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update category", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> finish());
    }
}