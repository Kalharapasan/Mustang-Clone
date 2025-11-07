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

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<CategoryItem> categoryItem;
    private final DBHelp dbHelp;

    public CategoryAdapter(Context context, List<CategoryItem> categoryItem) {
        this.context = context;
        this.categoryItem = categoryItem;
        this.dbHelp = new DBHelp(context);
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        CategoryItem item = categoryItem.get(position);

        holder.category.setText(item.getCategoryName() != null ? item.getCategoryName() : "N/A");
        holder.model.setText(item.getCategoryModel() != null ? item.getCategoryModel() : "N/A");

        // Decode Base64 image safely
        if (item.getCategoryIMG() != null && !item.getCategoryIMG().isEmpty()) {
            try {
                byte[] imgBytes = Base64.decode(item.getCategoryIMG(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                if (bitmap != null) holder.image.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Delete button
        holder.delete_Btn.setOnClickListener(v -> {
            try {
                dbHelp.deleteCategory(item.getCategoryID());
                categoryItem.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, categoryItem.size());
                Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error deleting category", Toast.LENGTH_SHORT).show();
            }
        });

        // Edit button
        holder.edit_Btn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(context, Category_Update.class);
                intent.putExtra("CATEGORY_ID", item.getCategoryID());
                intent.putExtra("CATEGORY_NAME", item.getCategoryName());
                intent.putExtra("CATEGORY_MODEL", item.getCategoryModel());
                intent.putExtra("CATEGORY_IMAGE", item.getCategoryIMG());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error opening update screen", Toast.LENGTH_SHORT).show();
            }
        });


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CarItem.class);
            intent.putExtra("CATEGORY_ID", item.getCategoryID());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryItem != null ? categoryItem.size() : 0;
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
        }
    }
}
