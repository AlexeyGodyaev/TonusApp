package com.caloriesdiary.caloriesdiary.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.caloriesdiary.caloriesdiary.Items.ActionItem;
import com.caloriesdiary.caloriesdiary.R;

import java.util.List;


public class ActionsAdapter extends BaseAdapter {

    private final List<ActionItem> list;
    private final LayoutInflater layoutInflater;

    public  ActionsAdapter(Context context, List<ActionItem> list){
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

        ActionItem actionItem = getActionItem(i);

        final TextView productName, calories;
        productName =  view.findViewById(R.id.productName);
        productName.setText(actionItem.getName());

        calories = view.findViewById(R.id.productCalories);
        calories.setText(actionItem.getCalories() +" ккал");

        return view;
    }

    private ActionItem getActionItem(int position){
        return (ActionItem) getItem(position);
    }
}
