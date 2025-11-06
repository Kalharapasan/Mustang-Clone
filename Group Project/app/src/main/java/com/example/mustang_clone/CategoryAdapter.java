package com.example.mustang_clone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<CategoryItem> categoryItem;


    public CategoryAdapter(Context context, List<CategoryItem> categoryItem) {
        this.context = context;
        this.categoryItem = categoryItem;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return categoryItem.size();
    }


    private static class CategoryViewHolder extends RecyclerView.ViewHolder {


        public CategoryViewHolder(@NonNull View view) {
            super(view);
        }
    }
}
