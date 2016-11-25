package com.example.lenovo.correctly.fragments;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import com.example.lenovo.correctly.entity.Topic;
import com.example.lenovo.correctly.utils.BitmapUtils;
import com.example.lenovo.correctly.utils.FileReader;
import com.example.lenovo.correctly.utils.FragmentLoader;

import java.io.IOException;
import java.util.List;


public class LevelsFragment extends Fragment implements CardAdapter.Listener {

    private String topic;
    CardAdapter mAdapter;

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
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id
                .recycler_view);

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                (getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CardAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);

        new LoadLevelsTask().execute();
        return view;
    }

    @Override
    public void onItemClicked(Topic topic) {
        Log.v("test", topic.name);
        Bundle args = new Bundle();
        if (topic.name.equals("Basic Words")) {
            args.putString("challenge", "Bonjour");
            args.putString("translation", "Good Morning");
            new FragmentLoader(getFragmentManager(), args,
                    new WordLearnFragment()).Load();
        }
        if (topic.name.equals("Sentences")) {
            args.putString("challenge", "Bonjour, voici le magasin.");
            args.putString("translation", "Good morning, here is the store.");
            new FragmentLoader(getFragmentManager(), args,
                    new SentenceLearnFragment()).Load();
        }
    }

    class LoadLevelsTask extends AsyncTask<Void, Void, List<Topic>> {

        @Override
        protected List<Topic> doInBackground(Void... params) {
            try {
                Gson gson = new Gson();
                List<Topic> levels = gson.fromJson(
                        FileReader.getStringFromFile(
                                getContext().getAssets(), "levels.json"),
                        new TypeToken<List<Topic>>() {}.getType());

                for (Topic level : levels) {
                    level.imageBitmap = BitmapUtils.getBitmapFromAsset(
                            getContext().getAssets(), level.image);
                }

                return levels;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Topic> levels) {
            ((CardAdapter) mAdapter).getItems().addAll(levels);
            mAdapter.notifyDataSetChanged();
        }
    }

}
