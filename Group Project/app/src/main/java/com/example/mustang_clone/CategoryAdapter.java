package com.example.mustang_clone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<CategoryItem> categoryItem;
    DBHelp dbHelp;

    public CategoryAdapter(Context context, List<CategoryItem> categoryItem) {
        this.context = context;
        this.categoryItem = categoryItem;
        dbHelp = new DBHelp(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryViewHolder viewHolder = (CategoryViewHolder) holder;
        CategoryItem item = categoryItem.get(position);

        viewHolder.category.setText(item.getCategoryName());
        viewHolder.model.setText(item.getCategoryModel());

        if (item.getCategoryIMG() != null && !item.getCategoryIMG().isEmpty()) {
            byte[] imgBytes = android.util.Base64.decode(item.getCategoryIMG(), android.util.Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            viewHolder.image.setImageBitmap(bitmap);
        }

        viewHolder.delete_Btn.setOnClickListener(v -> {
            dbHelp.deleteCategory(item.getCategoryID());
            categoryItem.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, categoryItem.size());
            Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
        });

        viewHolder.edit_Btn.setOnClickListener(v -> {
            Intent intent = new Intent(context, Category_Update.class);
            intent.putExtra("CATEGORY_ID", item.getCategoryID());
            intent.putExtra("CATEGORY_NAME", item.getCategoryName());
            intent.putExtra("CATEGORY_MODEL", item.getCategoryModel());
            intent.putExtra("CATEGORY_IMAGE", item.getCategoryIMG());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryItem.size();
    }

    private class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView image, delete_Btn, edit_Btn;
        TextView category, model;

        public CategoryViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image1);
            delete_Btn = view.findViewById(R.id.delete_Btn_ID);
            edit_Btn = view.findViewById(R.id.edit_Btn_ID);
            category = view.findViewById(R.id.category_ID);
            model = view.findViewById(R.id.model_ID);
        }
    }
}