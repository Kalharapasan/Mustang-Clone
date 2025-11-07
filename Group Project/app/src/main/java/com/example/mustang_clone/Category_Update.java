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

        // Receive intent data
        Intent intent = getIntent();
        if (intent != null) {
            categoryId = intent.getIntExtra("CATEGORY_ID", -1);
            carNameUpdate.setText(intent.getStringExtra("CATEGORY_NAME"));
            carModelUpdate.setText(intent.getStringExtra("CATEGORY_MODEL"));

            String categoryImage = intent.getStringExtra("CATEGORY_IMAGE");
            if (categoryImage != null && !categoryImage.isEmpty()) {
                try {
                    byte[] imgBytes = Base64.decode(categoryImage, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                    updateCategoryCarImage.setImageBitmap(bitmap);
                    selectedImageBytes = imgBytes;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            updateCategoryCarImage.setImageBitmap(bitmap);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            selectedImageBytes = stream.toByteArray();
                            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // Button to pick image
        updateCarImageBtn.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(pickIntent);
        });

        updateCategoryBtn.setOnClickListener(v -> {
            String name = carNameUpdate.getText().toString().trim();
            String model = carModelUpdate.getText().toString().trim();

            if (name.isEmpty() || model.isEmpty() || selectedImageBytes == null) {
                Toast.makeText(this, "Please fill all fields and select image", Toast.LENGTH_SHORT).show();
                return;
            }

            int rows = dbHelp.updateCategory(categoryId, name, selectedImageBytes, model);
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
