package com.caloriesdiary.caloriesdiary;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class RecycleFoodAdapter extends RecyclerView.Adapter<RecycleFoodAdapter.ViewHolder>  {
    private final List<FoodItem> listitem;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        public final TextView nameview;
        public final TextView caloriesview;
        public final TextView bjuview;
        public ViewHolder(View view) {
            super(view);
            nameview =  view.findViewById(R.id.recycler_food_item_name);
            caloriesview =  view.findViewById(R.id.recycler_food_item_calories);
            bjuview =  view.findViewById(R.id.recycler_food_item_bju);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

        }

    }


    public RecycleFoodAdapter(List<FoodItem> items) {
        listitem = items;
    }

    @Override
    public RecycleFoodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_food_list_item,parent,false);

        return new RecycleFoodAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecycleFoodAdapter.ViewHolder holder, int position) {
        FoodItem foodItem = listitem.get(position);
        holder.nameview.setText(foodItem.getName());
        holder.caloriesview.setText(foodItem.getCalories().toString());

        holder.bjuview.setText(foodItem.getB().toString()+"/"+foodItem.getJ().toString()+"/"+foodItem.getU().toString());
    }

    @Override
    public int getItemCount() {
        return listitem.size();
    }
}
