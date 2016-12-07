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
import com.example.lenovo.correctly.models.Level;
import com.example.lenovo.correctly.models.Topic;
import com.example.lenovo.correctly.utils.BitmapUtils;
import com.example.lenovo.correctly.utils.FragmentLoader;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class LevelsFragment extends Fragment implements CardAdapter.Listener {

    CardAdapter mAdapter;
    private String topic;

    public LevelsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.topic = (String) args.get("topic");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_levels, container,
                false);
        getActivity().setTitle(topic);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id
                .recycler_view);

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                (getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CardAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);

        new LoadLevelsTask(topic).execute();
        return view;
    }

    @Override
    public void onItemClicked(CardItem level) {
        Log.v("test", level.name);
        Fragment fragment = null;
        Bundle args = new Bundle();
        args.putString("topic", this.topic);
        if (level.name.equals("Basic Words")) {
            args.putString("level", "Basic Words");
            fragment = new WordLearnFragment();
        }
        if (level.name.equals("Sentences")) {
            args.putString("level", "Sentences");
            fragment = new SentenceLearnFragment();
        }
        new FragmentLoader(getFragmentManager(), args, fragment).Load();
    }

    class LoadLevelsTask extends AsyncTask<Void, Void, List<CardItem>> {
        String topic_name;

        LoadLevelsTask(String topic_name) {
            super();
            this.topic_name = topic_name;
        }
        @Override
        protected List<CardItem> doInBackground(Void... params) {
            List<CardItem> items = new ArrayList<>();
            Realm realm = Realm.getDefaultInstance();
            Topic level_topic = realm.where(Topic.class).equalTo("topic_name",
                    topic_name).findFirst();
            RealmResults<Level> levels = level_topic.getAllLevels();
            for (int i = 0; i < levels.size(); i++) {
                Level level = levels.get(i);
                CardItem item = new CardItem();
                item.name = level.getLevelName();
                item.image = level.getLevelImg();
                item.imageBitmap = BitmapUtils.getBitmapFromAsset(
                        getContext().getAssets(), item.image);
                item.progress = level.getProgress();
                items.add(item);
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<CardItem> levels) {
            mAdapter.getItems().addAll(levels);
            mAdapter.notifyDataSetChanged();
        }
    }

}
