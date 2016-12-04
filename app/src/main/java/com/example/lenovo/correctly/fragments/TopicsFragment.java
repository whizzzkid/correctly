package com.example.lenovo.correctly.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lenovo.correctly.R;
import com.example.lenovo.correctly.adapter.CardAdapter;
import com.example.lenovo.correctly.entity.CardItem;
import com.example.lenovo.correctly.models.Topic;
import com.example.lenovo.correctly.utils.BitmapUtils;
import com.example.lenovo.correctly.utils.FragmentLoader;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.ContentValues.TAG;

public class TopicsFragment extends Fragment implements CardAdapter.Listener {
    private RecyclerView.Adapter mAdapter;

    public TopicsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onItemClicked(CardItem topic) {
        Log.v(TAG, String.valueOf(topic.name));
        Bundle args = new Bundle();
        args.putString("topic", topic.name);
        new FragmentLoader(getFragmentManager(), args,
                new LevelsFragment()).Load();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_topics, container, false);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id
                .recycler_view);

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                (getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CardAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);

        new LoadTopicsTask().execute();
        return view;
    }


    class LoadTopicsTask extends AsyncTask<Void, Void, List<CardItem>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<CardItem> doInBackground(Void... params) {
            Realm realm = Realm.getDefaultInstance();
            RealmResults topics = realm.where(Topic.class).findAllSorted
                    ("order");
            List<CardItem> items = new ArrayList<>();

            for (int i = 0; i < topics.size(); i++) {
                Topic topic = (Topic) topics.get(i);
                CardItem item = new CardItem();
                item.name = topic.getTopicName();
                item.image = topic.getTopicImg();
                item.imageBitmap = BitmapUtils.getBitmapFromAsset(
                        getContext().getAssets(), item.image);
                items.add(item);
            }
            realm.close();

            return items;
        }

        @Override
        protected void onPostExecute(List<CardItem> topics) {
            ((CardAdapter) mAdapter).getItems().addAll(topics);
            mAdapter.notifyDataSetChanged();
        }
    }
}