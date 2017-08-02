package com.caloriesdiary.caloriesdiary.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caloriesdiary.caloriesdiary.Items.ActionItem;
import com.caloriesdiary.caloriesdiary.R;

import java.util.List;


public class RecycleActionAdapter extends RecyclerView.Adapter<RecycleActionAdapter.ViewHolder> {


    private List<ActionItem> listitem;


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public  TextView nameview,caloriesview;
        public ViewHolder(View view) {
            super(view);
            nameview = view.findViewById(R.id.recycler_action_item_name);
            caloriesview =  view.findViewById(R.id.recycler_action_item_calories);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

        }
    }


    public RecycleActionAdapter(List<ActionItem> items) {
        listitem = items;
    }



    @Override
    public RecycleActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_action_list_item,parent,false);


        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ActionItem actionItem = listitem.get(position);
        holder.nameview.setText(actionItem.getName());
        holder.caloriesview.setText(actionItem.getCalories().toString());
    }

    @Override
    public int getItemCount() {
        return listitem.size();
    }
}
