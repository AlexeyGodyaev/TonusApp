package com.caloriesdiary.caloriesdiary;

class ActionItem {
    private String name;
    private Float calories;
    private int id;

    ActionItem(String name, Float calories, int id) {
        this.name = name;
        this.calories = calories;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getCalories() {
        return calories;
    }

    public void setCalories(Float calories) {
        this.calories = calories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
