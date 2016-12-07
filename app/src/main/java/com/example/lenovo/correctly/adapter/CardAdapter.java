package com.example.lenovo.correctly.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lenovo.correctly.R;
import com.example.lenovo.correctly.entity.CardItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>
        implements View.OnClickListener {

    private List<CardItem> mItems;
    private Listener mListener;

    public CardAdapter(List<CardItem> items, Listener listener) {
        if (items == null) {
            items = new ArrayList<>();
        }
        mItems = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_card_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        CardItem item = mItems.get(i);
        String title;
        if (item.progress > -1) {
            String progressMsg;
            if (item.progress == 0) {
                progressMsg = " [New Level]";
            } else {
                progressMsg = String.format(Locale.getDefault(), " [%d%% " +
                        "Complete]", item.progress);
            }
            title = String.format("%s%s", item.name,
                    progressMsg);
        } else {
            title = item.name;
        }
        viewHolder.cardTitle.setText(title);
        viewHolder.imgThumbnail.setImageBitmap(item.imageBitmap);
        viewHolder.progressBar.setProgress(item.progress);
        if (mListener != null) {
            viewHolder.cardView.setOnClickListener(this);
            viewHolder.cardView.setTag(item);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof CardView) {
            CardItem item = (CardItem) v.getTag();
            mListener.onItemClicked(item);
        }
    }

    public List<CardItem> getItems() {
        return mItems;
    }

    public interface Listener {
        void onItemClicked(CardItem item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView cardTitle;
        CardView cardView;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id
                    .img_thumbnail);
            cardTitle = (TextView) itemView.findViewById(R.id.card_title);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }
}
