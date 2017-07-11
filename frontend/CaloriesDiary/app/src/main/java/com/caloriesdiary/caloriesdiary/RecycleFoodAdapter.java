package com.caloriesdiary.caloriesdiary;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class RecycleFoodAdapter extends RecyclerView.Adapter<RecycleFoodAdapter.ViewHolder>  {
    private List<FoodItem> listitem;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameview,caloriesview,bjuview;
        public ViewHolder(View view) {
            super(view);
            nameview = (TextView) view.findViewById(R.id.recycler_food_item_name);
            caloriesview = (TextView) view.findViewById(R.id.recycler_food_item_calories);
            bjuview = (TextView) view.findViewById(R.id.recycler_food_item_bju);
        }

    }


    public RecycleFoodAdapter(List<FoodItem> items) {
        listitem = items;
    }

    @Override
    public RecycleFoodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_food_list_item,parent,false);

        RecycleFoodAdapter.ViewHolder vh = new RecycleFoodAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecycleFoodAdapter.ViewHolder holder, int position) {
        FoodItem foodItem = listitem.get(position);
        holder.nameview.setText(foodItem.getName().toString());
        holder.caloriesview.setText(foodItem.getCalories().toString());
        holder.bjuview.setText(foodItem.getB().toString()+"/"+foodItem.getJ().toString()+"/"+foodItem.getU().toString());
    }

    @Override
    public int getItemCount() {
        return listitem.size();
    }
}
