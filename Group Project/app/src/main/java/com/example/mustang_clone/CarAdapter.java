package com.example.mustang_clone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Car> carItems;
    Context context;
    DBHelp dbHelp;

    public CarAdapter(List<Car> carItems, Context context) {
        this.carItems = carItems;
        this.context = context;
        dbHelp = new DBHelp(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_layout, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CarViewHolder vh = (CarViewHolder) holder;
        Car car = carItems.get(position);

        vh.model_name.setText(car.getCarName());
        vh.year.setText(car.getYear());
        vh.generation.setText(car.getGeneration());
        vh.engine_Type.setText(car.getEngineType());
        vh.transmission.setText(car.getTransmission());
        vh.description.setText(car.getHorsepower());
        vh.ratingBar.setRating((float) car.getRating());

        // Image loading logic
        if (car.getImg() != null && !car.getImg().isEmpty()) {
            if (car.getImg().startsWith("http")) {
                Glide.with(context)
                        .load(car.getImg())
                        .placeholder(R.drawable.cobramustanf3d)
                        .into(vh.imageView);
            } else {
                File file = new File(car.getImg());
                if (file.exists()) {
                    Glide.with(context)
                            .load(Uri.fromFile(file))
                            .placeholder(R.drawable.cobramustanf3d)
                            .into(vh.imageView);
                } else {
                    vh.imageView.setImageResource(R.drawable.cobramustanf3d);
                }
            }
        } else {
            vh.imageView.setImageResource(R.drawable.cobramustanf3d);
        }

        // Edit button
        vh.edit_Btn.setOnClickListener(v -> {
            Intent intent = new Intent(context,Car_Update.class);
            intent.putExtra("carID", car.getCarID());
            context.startActivity(intent);
        });

        // Delete button
        vh.delete_Btn.setOnClickListener(v -> {
            dbHelp.deleteCar(car.getCarID());
            carItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, carItems.size());
            Toast.makeText(context, "Car deleted successfully", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return carItems.size();
    }

    private static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView model_name, year, generation, engine_Type, speed, transmission, description;
        RatingBar ratingBar;
        Button edit_Btn, delete_Btn;
        ImageView imageView;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            model_name = itemView.findViewById(R.id.model_name_ID);
            year = itemView.findViewById(R.id.year_ID);
            generation = itemView.findViewById(R.id.generation_ID);
            engine_Type = itemView.findViewById(R.id.engine_Type_ID);
            speed = itemView.findViewById(R.id.speed_ID);
            transmission = itemView.findViewById(R.id.transmission_ID);
            description = itemView.findViewById(R.id.description_ID);
            ratingBar = itemView.findViewById(R.id.ratingBarID);
            imageView = itemView.findViewById(R.id.image3dID);
            edit_Btn = itemView.findViewById(R.id.edit_Btn_ID);
            delete_Btn = itemView.findViewById(R.id.delete_Btn_ID);
        }
    }
}
