package com.example.lenovo.correctly.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.correctly.R;
import com.example.lenovo.correctly.entity.Topic;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>
        implements View.OnClickListener {

    private List<Topic> mItems;
    private Listener mListener;

    public CardAdapter(List<Topic> items, Listener listener) {
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
        Topic topic = mItems.get(i);
        viewHolder.tvMovie.setText(topic.name);
        viewHolder.imgThumbnail.setImageBitmap(topic.imageBitmap);
        if (mListener != null) {
            viewHolder.cardView.setOnClickListener(this);
            viewHolder.cardView.setTag(topic);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof CardView) {
            Topic topic = (Topic) v.getTag();
            mListener.onItemClicked(topic);
        }
    }

    public List<Topic> getItems() {
        return mItems;
    }

    public interface Listener {
        void onItemClicked(Topic topic);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvMovie;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvMovie = (TextView) itemView.findViewById(R.id.tv_movie);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
