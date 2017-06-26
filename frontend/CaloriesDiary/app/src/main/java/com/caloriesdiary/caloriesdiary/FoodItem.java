package com.caloriesdiary.caloriesdiary;


public class FoodItem {
    String name;
    int b, j, u, categoryId, calories;
    FoodItem(String name, int b, int j, int u, int categoryId, int calories){
        this.name = name;
        this.b = b;
        this.j = j;
        this.u = u;
        this.categoryId =categoryId;
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}
