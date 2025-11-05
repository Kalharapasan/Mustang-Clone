package com.example.mustang_clone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    List <CategoryItem>categoryItem;

    private CategoryAdapter( Context context,List <CategoryItem>categoryItem) {
        this.context = context;
        this.categoryItem = categoryItem;
    }




    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_category,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        

    }

    @Override
    public int getItemCount() {
        return categoryItem.size();
    }

    private class CategoryViewHolder extends RecyclerView.ViewHolder {
        public CategoryViewHolder(View view) {
            super();
        }
    }
}
