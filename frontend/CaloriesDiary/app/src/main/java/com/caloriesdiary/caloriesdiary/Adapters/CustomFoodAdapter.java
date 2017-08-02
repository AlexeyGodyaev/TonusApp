package com.caloriesdiary.caloriesdiary.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.caloriesdiary.caloriesdiary.Items.FoodItem;
import com.caloriesdiary.caloriesdiary.R;

import java.util.List;


public class CustomFoodAdapter extends RecyclerView.Adapter<CustomFoodAdapter.ViewHolder>  {
    private List<FoodItem> listitem;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        public CheckBox checkBox;
        public TextView b,j,u,kcal,id;

        public ViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.add_food_checkbox);
            b = (TextView) view.findViewById(R.id.viewB);
            j = (TextView) view.findViewById(R.id.viewJ);
            u = (TextView) view.findViewById(R.id.viewU);
            kcal = (TextView) view.findViewById(R.id.viewKc);
            id = (TextView) view.findViewById(R.id.viewID);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

        }

    }


    public CustomFoodAdapter(List<FoodItem> items) {
        listitem = items;
    }

    @Override
    public CustomFoodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_food_dialog_recycle_item,parent,false);

        CustomFoodAdapter.ViewHolder vh = new CustomFoodAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(CustomFoodAdapter.ViewHolder holder, int position) {
        FoodItem foodItem = listitem.get(position);
        holder.checkBox.setText(foodItem.getName());
        holder.b.setText(String.valueOf(foodItem.getB()));
        holder.j.setText(String.valueOf(foodItem.getJ()));
        holder.u.setText(String.valueOf(foodItem.getU()));
        holder.kcal.setText(String.valueOf(foodItem.getCalories()));
        holder.id.setText(String.valueOf(foodItem.getId()));

    }

    @Override
    public int getItemCount() {
        return listitem.size();
    }
}
