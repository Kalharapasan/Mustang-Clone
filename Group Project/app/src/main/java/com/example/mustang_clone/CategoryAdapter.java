package com.example.mustang_clone;

import android.app.Activity;
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

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private static final String TAG = "CategoryAdapter";
    private final Context context;
    private final List<CategoryItem> categoryItem;
    private final DBHelp dbHelp;

    public CategoryAdapter(Context context, List<CategoryItem> categoryItem) {
        this.context = context;
        this.categoryItem = categoryItem;
        this.dbHelp = new DBHelp(context);
        Log.d(TAG, "Adapter created with " + categoryItem.size() + " items");
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(context).inflate(R.layout.category_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position);

        CategoryItem item = categoryItem.get(position);

        String categoryName = item.getCategoryName() != null ? item.getCategoryName() : "N/A";
        String categoryModel = item.getCategoryModel() != null ? item.getCategoryModel() : "N/A";

        holder.category.setText(categoryName);
        holder.model.setText(categoryModel);

        Log.d(TAG, "Binding category: " + categoryName + " - " + categoryModel);

        // Load image
        if (item.getCategoryIMG() != null && !item.getCategoryIMG().isEmpty()) {
            try {
                byte[] imgBytes = Base64.decode(item.getCategoryIMG(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                if (bitmap != null) {
                    holder.image.setImageBitmap(bitmap);
                    Log.d(TAG, "Image loaded successfully for: " + categoryName);
                } else {
                    holder.image.setImageResource(R.drawable.cobramustang);
                    Log.w(TAG, "Failed to decode bitmap for: " + categoryName);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error decoding image for: " + categoryName, e);
                holder.image.setImageResource(R.drawable.cobramustang);
            }
        } else {
            holder.image.setImageResource(R.drawable.cobramustang);
            Log.w(TAG, "No image data for: " + categoryName);
        }

        // Delete button
        holder.delete_Btn.setOnClickListener(v -> {
            try {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    Log.w(TAG, "Invalid adapter position");
                    return;
                }

                CategoryItem currentItem = categoryItem.get(adapterPosition);

                // Delete from database
                dbHelp.deleteCategory(currentItem.getCategoryID());

                // Remove from list and notify adapter
                categoryItem.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                notifyItemRangeChanged(adapterPosition, categoryItem.size());

                Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Category deleted: " + currentItem.getCategoryName());
            } catch (Exception e) {
                Log.e(TAG, "Error deleting category", e);
                Toast.makeText(context, "Error deleting category", Toast.LENGTH_SHORT).show();
            }
        });

        // Edit button
        holder.edit_Btn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(context, Category_Update.class);
                intent.putExtra("categoryID", item.getCategoryID());
                intent.putExtra("categoryName", item.getCategoryName());
                intent.putExtra("categoryModel", item.getCategoryModel());
                intent.putExtra("categoryImg", item.getCategoryIMG());

                // Start activity for result if context is an Activity
                if (context instanceof Activity) {
                    ((Activity) context).startActivity(intent);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

                Log.d(TAG, "Opening update screen for: " + item.getCategoryName());
            } catch (Exception e) {
                Log.e(TAG, "Error opening update screen", e);
                Toast.makeText(context, "Error opening update screen", Toast.LENGTH_SHORT).show();
            }
        });

        // Item click - navigate to cars
        holder.itemView.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(context, CarItem.class);
                intent.putExtra("CATEGORY_ID", item.getCategoryID());

                if (context instanceof Activity) {
                    ((Activity) context).startActivity(intent);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

                Log.d(TAG, "Opening cars for category: " + item.getCategoryName() +
                        " (ID: " + item.getCategoryID() + ")");
            } catch (Exception e) {
                Log.e(TAG, "Error opening car list", e);
                Toast.makeText(context, "Error opening car list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = categoryItem != null ? categoryItem.size() : 0;
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView image, delete_Btn, edit_Btn;
        TextView category, model;

        CategoryViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image1);
            delete_Btn = view.findViewById(R.id.delete_Btn_ID);
            edit_Btn = view.findViewById(R.id.edit_Btn_ID);
            category = view.findViewById(R.id.category_ID);
            model = view.findViewById(R.id.model_ID);

            Log.d("CategoryViewHolder", "ViewHolder created");
        }
    }
}