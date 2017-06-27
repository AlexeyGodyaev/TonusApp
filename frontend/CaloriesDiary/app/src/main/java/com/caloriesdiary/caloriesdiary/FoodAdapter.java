package com.caloriesdiary.caloriesdiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class FoodAdapter extends BaseAdapter{

    private List<FoodItem> list;
    private LayoutInflater layoutInflater;

    public  FoodAdapter(Context context, List<FoodItem> list){
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if(view == null){
            view = layoutInflater.inflate(R.layout.list_item, viewGroup, false);
        }

        FoodItem foodItem = getFoodItem(i);

        TextView productName, bJU, calories;
        Button addProduct;
        productName = (TextView) view.findViewById(R.id.productName);
        productName.setText(foodItem.getName());

        bJU = (TextView) view.findViewById(R.id.bJU);
        bJU.setText(foodItem.getB().toString()+"/"+foodItem.getJ().toString()+"/"+foodItem.getU().toString());

        calories = (TextView) view.findViewById(R.id.productCalories);
        calories.setText(foodItem.getCalories().toString());

        addProduct = (Button) view.findViewById(R.id.addProductButton);

        return view;
    }

    private FoodItem getFoodItem(int position){
        return (FoodItem) getItem(position);
    }
}
