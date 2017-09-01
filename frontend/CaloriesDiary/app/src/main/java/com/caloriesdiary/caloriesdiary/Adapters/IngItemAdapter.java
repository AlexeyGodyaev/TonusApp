package com.caloriesdiary.caloriesdiary.Adapters;


import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.Items.FoodItem;
import com.caloriesdiary.caloriesdiary.R;

import java.util.List;

public class IngItemAdapter extends RecyclerView.Adapter<IngItemAdapter.ViewHolder> {
    private final List<FoodItem> listitem;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        public final TextView nameview;
        public final EditText massview;
        public ViewHolder(View view) {
            super(view);
            nameview =  view.findViewById(R.id.recycler_ing_item_name);
            massview =  view.findViewById(R.id.recycler_ing_item_mass);
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
    public void onBindViewHolder(final IngItemAdapter.ViewHolder holder, int position) {
        final FoodItem foodItem = listitem.get(position);
        holder.nameview.setText(foodItem.getName());

        holder.massview.setText(String.valueOf(foodItem.getMass()));
    }

    @Override
    public int getItemCount() {
        return listitem.size();
    }
}
