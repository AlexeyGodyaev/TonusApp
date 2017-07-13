package com.caloriesdiary.caloriesdiary;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ViewHolder> {


    private List<ArchiveItem> listitem;


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameview,dateview;
        public ViewHolder(View view) {
            super(view);
            nameview = (TextView) view.findViewById(R.id.archive_item_name);
            dateview = (TextView) view.findViewById(R.id.archive_item_date);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

        }
    }


    public ArchiveAdapter(List<ArchiveItem> items) {
        listitem = items;
    }



    @Override
    public ArchiveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.archive_item,parent,false);

        ArchiveAdapter.ViewHolder vh = new ArchiveAdapter.ViewHolder(view);
        return vh;

    }

    @Override
    public void onBindViewHolder(ArchiveAdapter.ViewHolder holder, int position) {
        ArchiveItem actionItem = listitem.get(position);
        if (actionItem.getName().toString().length()>15)
            holder.nameview.setText(actionItem.getName().toString().substring(0,15) + "...");
        else holder.nameview.setText(actionItem.getName().toString());
        holder.dateview.setText(actionItem.getDate().toString());
    }

    @Override
    public int getItemCount() {
        return listitem.size();
    }
}
