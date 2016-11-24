package com.example.lenovo.correctly.fragments;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lenovo.correctly.R;
import com.example.lenovo.correctly.adapter.CardAdapter;
import com.example.lenovo.correctly.entity.Topic;
import com.example.lenovo.correctly.utils.BitmapUtils;
import com.example.lenovo.correctly.utils.FileReader;

import java.io.IOException;
import java.util.List;


public class TopicsFragment extends Fragment implements CardAdapter.Listener {
    private RecyclerView.Adapter mAdapter;

    public TopicsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onItemClicked(Topic topic) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View myFragmentView = inflater.inflate(R.layout.fragment_topics, container, false);
        RecyclerView mRecyclerView = (RecyclerView) myFragmentView.findViewById(R.id.my_recycler_view);
        //assert mRecyclerView != null;
        if (mRecyclerView == null)
            Toast.makeText(getContext(), "empty :/", Toast.LENGTH_LONG);
        else
            Toast.makeText(getContext(), ":0", Toast.LENGTH_LONG);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CardAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);

        new LoadTopicsTask().execute();
        return myFragmentView;
        // Inflate the layout for this fragment

    }


    class LoadTopicsTask extends AsyncTask<Void, Void, List<Topic>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<Topic> doInBackground(Void... params) {
            try {
                Gson gson = new Gson();
                List<Topic> topics = gson.fromJson(
                        FileReader.getStringFromFile(
                                getContext().getAssets(), "topics.json"),
                        new TypeToken<List<Topic>>() {}.getType());

                for (Topic topic : topics) {
                    topic.imageBitmap = BitmapUtils.getBitmapFromAsset(
                            getContext().getAssets(), topic.image);
                }

                return topics;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Topic> topics) {
            ((CardAdapter) mAdapter).getItems().addAll(topics);
            mAdapter.notifyDataSetChanged();
        }
    }
}