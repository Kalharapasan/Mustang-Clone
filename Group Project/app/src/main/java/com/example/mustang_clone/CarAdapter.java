package com.example.mustang_clone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private static final String TAG = "CarAdapter";
    private final List<Car> carItems;
    private final Context context;
    private final DBHelp dbHelp;

    public CarAdapter(List<Car> carItems, Context context) {
        this.carItems = carItems;
        this.context = context;
        this.dbHelp = new DBHelp(context);
        Log.d(TAG, "CarAdapter created with " + carItems.size() + " items");
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_layout, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car item = carItems.get(position);

        Log.d(TAG, "Binding car at position " + position + ": " + item.getCarName());

        // Set text data
        holder.model.setText(item.getCarModel());
        holder.carName.setText(item.getCarName());

        // Load image from Base64
        Bitmap bitmap = null;
        try {
            if (item.getCarImg() != null && !item.getCarImg().isEmpty()) {
                byte[] imgBytes = Base64.decode(item.getCarImg(), Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                if (bitmap != null) {
                    Log.d(TAG, "Image loaded for car: " + item.getCarName());
                } else {
                    Log.w(TAG, "Failed to decode bitmap for: " + item.getCarName());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading car image", e);
        }

        if (bitmap != null) {
            holder.image.setImageBitmap(bitmap);
        } else {
            holder.image.setImageResource(R.drawable.cobramustanf3d);
        }

        // Delete button
        holder.deleteBtn.setOnClickListener(v -> {
            try {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Car currentItem = carItems.get(pos);

                    // Delete from database
                    dbHelp.deleteCar(currentItem.getCarID());

                    // Remove from list and notify
                    carItems.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, carItems.size());

                    Toast.makeText(context, "Car deleted", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Car deleted: " + currentItem.getCarName());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deleting car", e);
                Toast.makeText(context, "Error deleting car", Toast.LENGTH_SHORT).show();
            }
        });

        // Edit button - Pass Base64 encoded image
        holder.editBtn.setOnClickListener(v -> {
            try {
                Log.d(TAG, "Edit button clicked for car: " + item.getCarName());

                Intent intent = new Intent(context, Car_Update.class);

                // Add all required data
                intent.putExtra("CAR_ID", item.getCarID());
                intent.putExtra("CAR_NAME", item.getCarName());
                intent.putExtra("CAR_MODEL", item.getCarModel());
                intent.putExtra("YEAR", item.getYear());
                intent.putExtra("GENERATION", item.getGeneration());
                intent.putExtra("ENGINE_TYPE", item.getEngineType());
                intent.putExtra("HORSEPOWER", item.getHorsepower());
                intent.putExtra("TRANSMISSION", item.getTransmission());
                intent.putExtra("COLOR", item.getColor());
                intent.putExtra("CATEGORY_ID", item.getCategoryID());
                intent.putExtra("RATING", item.getRating());

                // Handle Base64 image
                String imageBase64 = item.getCarImg();
                if (imageBase64 != null && !imageBase64.isEmpty()) {
                    intent.putExtra("CAR_IMAGE", imageBase64);
                    Log.d(TAG, "Image Base64 added");
                } else {
                    intent.putExtra("CAR_IMAGE", "");
                    Log.d(TAG, "No image available");
                }

                // Log all data being passed
                Log.d(TAG, "Starting Car_Update with data:");
                Log.d(TAG, "  CAR_ID: " + item.getCarID());
                Log.d(TAG, "  CAR_NAME: " + item.getCarName());
                Log.d(TAG, "  CATEGORY_ID: " + item.getCategoryID());
                Log.d(TAG, "  RATING: " + item.getRating());

                // Start activity
                context.startActivity(intent);

            } catch (Exception e) {
                Log.e(TAG, "Error opening Car_Update", e);
                Toast.makeText(context, "Error opening update screen: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Item click - View car details
        holder.itemView.setOnClickListener(v -> {
            try {
                Log.d(TAG, "Item clicked for car: " + item.getCarName());

                Intent intent = new Intent(context, CarView.class);

                intent.putExtra("CAR_ID", item.getCarID());
                intent.putExtra("CAR_NAME", item.getCarName());
                intent.putExtra("CAR_MODEL", item.getCarModel());
                intent.putExtra("YEAR", item.getYear());
                intent.putExtra("GENERATION", item.getGeneration());
                intent.putExtra("ENGINE_TYPE", item.getEngineType());
                intent.putExtra("HORSEPOWER", item.getHorsepower());
                intent.putExtra("TRANSMISSION", item.getTransmission());
                intent.putExtra("COLOR", item.getColor());
                intent.putExtra("CAR_IMAGE", item.getCarImg());
                intent.putExtra("RATING", item.getRating());
                intent.putExtra("CATEGORY_ID", item.getCategoryID());

                context.startActivity(intent);

            } catch (Exception e) {
                Log.e(TAG, "Error opening CarView", e);
                Toast.makeText(context, "Error opening car details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = carItems != null ? carItems.size() : 0;
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        ImageView deleteBtn, editBtn;
        TextView model, carName;

        public CarViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image1);
            deleteBtn = view.findViewById(R.id.delete_Btn_ID);
            editBtn = view.findViewById(R.id.edit_Btn_ID);
            model = view.findViewById(R.id.model_ID);
            carName = view.findViewById(R.id.car_Name_ID);

            Log.d("CarViewHolder", "ViewHolder created");
        }
    }
}