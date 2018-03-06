package com.codepath.apps.restclienttemplate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.DetailsActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.Tweet_Table;
import com.codepath.apps.restclienttemplate.rest.RestApplication;
import com.codepath.apps.restclienttemplate.rest.RestClient;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.utils.MyUtils.isOnline;

/**
 * Created by Ermano
 * on 3/4/2018.
 */

public class FragmentFavorites extends Fragment {

    @BindView(R.id.frag_generic_rvItems)
    RecyclerView rvTweets;

    String screen_name;

    List<Tweet> tweets;

    TweetAdapter adapter;

    EndlessRecyclerViewScrollListener scrollListener;

    public FragmentFavorites() {
    }

    public static FragmentFavorites newInstance(String screen_name) {
        FragmentFavorites fragmentFavorites= new FragmentFavorites();
        Bundle bundle = new Bundle();
        bundle.putString("SCREEN_NAME", screen_name);
        fragmentFavorites.setArguments(bundle);
        return fragmentFavorites;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generic_recycleview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        screen_name = getArguments().getString("SCREEN_NAME");


        // Init Bind of views
        ButterKnife.bind(this, view);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        tweets = new ArrayList<>();

        adapter = new TweetAdapter(getContext(), tweets);

        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addItemDecoration(new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation()));
        rvTweets.setAdapter(adapter);

        // set up scroll listener
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadTweets(page);
            }
        };

        rvTweets.addOnScrollListener(scrollListener);


        //Add on click support
        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("TWEET", Parcels.wrap(adapter.getItem(position)));
                startActivity(intent);
            }
        });


        loadTweets(1);
    }


    private void loadTweets(final int page) {

        if (!isOnline()) {

            bindTweets(
                    SQLite.select()
                            .from(Tweet.class)
                            .where(Tweet_Table.type_tweet.eq(0))
                            .queryList()
            );

        } else {
            RestClient client = RestApplication.getRestClient();
            client.getUserFavorites(page, screen_name, new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, final JSONArray jsonArray) {
                    Log.d("DEBUG", "page : " + page + " / timeline: " + jsonArray.toString());
                    // Load json array into model classes
                    try {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                List<Tweet> tweetList = Tweet.getTweets(jsonArray);

                                bindTweets(tweetList);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }


    }

    private void bindTweets(List<Tweet> tweetList) {
        int curSize = adapter.getItemCount();

        tweets.addAll(tweetList);

        adapter.notifyItemRangeChanged(curSize, tweetList.size());

    }

    private void clearViews() {
        tweets.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }
}
