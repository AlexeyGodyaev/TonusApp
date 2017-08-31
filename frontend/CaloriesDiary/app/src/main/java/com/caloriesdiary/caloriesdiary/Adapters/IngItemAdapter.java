package com.caloriesdiary.caloriesdiary.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caloriesdiary.caloriesdiary.Items.FoodItem;
import com.caloriesdiary.caloriesdiary.R;

import java.util.List;

public class IngItemAdapter extends RecyclerView.Adapter<IngItemAdapter.ViewHolder> {
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


    public IngItemAdapter(List<FoodItem> items) {
        listitem = items;
    }

    @Override
    public IngItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_ing_list_item,parent,false);

        return new IngItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngItemAdapter.ViewHolder holder, int position) {
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
