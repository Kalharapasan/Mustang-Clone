package com.example.mustang_clone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    List<Car> carItems;
    Context context;
    DBHelp dbHelp;

    public CarAdapter(List<Car> carItems, Context context) {
        this.carItems = carItems;
        this.context = context;
        this.dbHelp = new DBHelp(context);
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

        holder.model.setText(item.getCarModel());
        holder.carName.setText(item.getCarName());

        // ✅ Load image (from Base64 or file)
        if (item.getCarImg() != null && !item.getCarImg().isEmpty()) {
            try {
                // First try decoding as file path
                File file = new File(item.getCarImg());
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    holder.image.setImageBitmap(bitmap);
                } else {
                    // fallback: decode Base64 (old data)
                    byte[] imgBytes = Base64.decode(item.getCarImg(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                    holder.image.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Delete button
        holder.deleteBtn.setOnClickListener(v -> {
            dbHelp.deleteCar(item.getCarID());
            carItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, carItems.size());
            Toast.makeText(context, "Car deleted", Toast.LENGTH_SHORT).show();
        });

        // ✅ Edit button — fixed: send safe image path instead of Base64
        holder.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, Car_Update.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

            // ✅ Fixed part
            String imagePath = item.getCarImg();
            if (imagePath != null && new File(imagePath).exists()) {
                // It's a file path
                intent.putExtra("CAR_IMAGE", imagePath);
            } else {
                // Avoid sending huge Base64 strings (causes crash)
                intent.putExtra("CAR_IMAGE", "");
            }

            context.startActivity(intent);
        });

        // View details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CarView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return carItems.size();
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
        }
    }
}
