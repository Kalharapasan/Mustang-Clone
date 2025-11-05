package com.example.mustang_clone;

public class CategoryItem {
    private int categoryID;
    private  String categoryIMG;
    private String categoryName;
    private String categoryModel;

    public CategoryItem(int categoryID, String categoryIMG, String categoryName, String categoryModel) {
        this.categoryID = categoryID;
        this.categoryIMG = categoryIMG;
        this.categoryName = categoryName;
        this.categoryModel = categoryModel;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryIMG() {
        return categoryIMG;
    }

    public void setCategoryIMG(String categoryIMG) {
        this.categoryIMG = categoryIMG;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(String categoryModel) {
        this.categoryModel = categoryModel;
    }
}
