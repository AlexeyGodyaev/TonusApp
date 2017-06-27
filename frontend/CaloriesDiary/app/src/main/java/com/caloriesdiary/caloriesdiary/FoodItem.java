package com.caloriesdiary.caloriesdiary;


public class FoodItem {
    String name;
    Float b, j, u, calories;
    int categoryId;

    FoodItem(String name, Float b, Float j, Float u, int categoryId, Float calories){
        this.name = name;
        this.b = b;
        this.j = j;
        this.u = u;
        this.categoryId =categoryId;
        this.calories = calories;
    }

    FoodItem(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getB() {
        return b;
    }

    public void setB(Float b) {
        this.b = b;
    }

    public Float getJ() {
        return j;
    }

    public void setJ(Float j) {
        this.j = j;
    }

    public Float getU() {
        return u;
    }

    public void setU(Float u) {
        this.u = u;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Float getCalories() {
        return calories;
    }

    public void setCalories(Float calories) {
        this.calories = calories;
    }
}
