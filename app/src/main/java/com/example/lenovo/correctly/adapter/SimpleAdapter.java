package com.example.lenovo.correctly.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.correctly.R;

import java.util.ArrayList;
import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

    List<String> mItems;

    public SimpleAdapter() {
        super();
        mItems = new ArrayList<String>();
        mItems.add("Travel");
        mItems.add("City");
        mItems.add("Family");
        mItems.add("Calendar");
        mItems.add("School");
        mItems.add("Home");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_simple_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.itemView.setText(mItems.get(i));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = (TextView) itemView.findViewById(R.id.tv_recycler_view_item);
        }
    }
}
