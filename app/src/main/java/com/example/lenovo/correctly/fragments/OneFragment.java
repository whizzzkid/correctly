package com.example.lenovo.correctly.fragments;

import com.example.lenovo.correctly.entity.Topic;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lenovo.correctly.R;
import com.example.lenovo.correctly.adapter.CardAdapter;
import com.example.lenovo.correctly.utils.BitmapUtils;
import com.example.lenovo.correctly.utils.FileReader;

import java.io.IOException;
import java.util.List;


public class OneFragment extends Fragment implements CardAdapter.Listener {
    private View myFragmentView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       /* */

    }

    @Override
    public void onItemClicked(Topic topic) {
        if (topic != null) {
            //Toast.makeText(getContext(), "You just selected " + topic.name + "!", Toast.LENGTH_SHORT).show();
        }

        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);


        viewPager.setCurrentItem(1, true);


// Commit the transaction

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        myFragmentView = inflater.inflate(R.layout.fragment_one, container, false);
        mRecyclerView = (RecyclerView) myFragmentView.findViewById(R.id.my_recycler_view);
        //assert mRecyclerView != null;
        if (mRecyclerView == null)
            Toast.makeText(getContext(), "empty :/", Toast.LENGTH_LONG);
        else
            Toast.makeText(getContext(), ":0", Toast.LENGTH_LONG);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CardAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);

        new LoadMoviesTask().execute();
        return myFragmentView;
        // Inflate the layout for this fragment

    }


    class LoadMoviesTask extends AsyncTask<Void, Void, List<Topic>> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getContext(), getString(R.string.title_loading),
                    getString(R.string.msg_loading), true);
        }

        @Override
        protected List<Topic> doInBackground(Void... params) {
            try {
                String strMovies = FileReader.getStringFromFile(getContext().getAssets(), "topics.json");
                Gson gson = new Gson();
                List<Topic> movies = gson.fromJson(strMovies, new TypeToken<List<Topic>>() {
                }.getType());

                for (Topic topic : movies) {
                    topic.imageBitmap = BitmapUtils.getBitmapFromAsset(getContext().getAssets(), topic.image);
                }

                return movies;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Topic> movies) {
            dialog.dismiss();
            ((CardAdapter) mAdapter).getItems().addAll(movies);
            mAdapter.notifyDataSetChanged();
        }
    }
}

