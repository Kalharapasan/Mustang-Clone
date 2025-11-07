package com.example.mustang_clone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

                            // Display selected image
                            addCategoryCarImage.setImageBitmap(bitmap);

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

        // Add category button
        addCategoryBtn.setOnClickListener(v -> {
            String name = carNameInput.getText().toString().trim();
            String model = carModelInput.getText().toString().trim();

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

            long id = dbHelp.addCategory(name, selectedImageBytes, model);

            if (id > 0) {
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                finish(); // Go back to Category activity
            } else {
                Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button
        backButton.setOnClickListener(v -> finish());
    }
}