package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.fragments.FragmentNewTweet;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.Tweet_Table;
import com.codepath.apps.restclienttemplate.rest.RestApplication;
import com.codepath.apps.restclienttemplate.rest.RestClient;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FragmentNewTweet.FragmentNewTweetListener {

    @BindView(R.id.fab)
    FloatingActionButton btn_tweet;

    @BindView(R.id.rvItems)
    RecyclerView rvTweets;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;


    List<Tweet> tweets;

    TweetAdapter adapter;

    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Init Bind of views
        ButterKnife.bind(this);

        btn_tweet.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        tweets = new ArrayList<>();

        adapter = new TweetAdapter(this, tweets);

        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
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
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("TWEET", Parcels.wrap(adapter.getItem(position)));
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTweets(1);
                clearViews();
            }
        });

        loadTweets(1);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                // Make sure to check whether returned data will be null.
                String titleOfPage = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                String urlOfPage = intent.getStringExtra(Intent.EXTRA_TEXT);
                Uri imageUriOfPage = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

                showDialogNewTweet(getString(R.string.new_tweet), urlOfPage);

            }
        }

    }

    private void showDialogNewTweet(String title, String tweet) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentNewTweet fragmentNewTweet = FragmentNewTweet.newInstance(title, tweet);
        fragmentNewTweet.setCancelable(false);
        fragmentNewTweet.show(fm, "tweet fragment");
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
            client.getHomeTimeline(page, new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, final JSONArray jsonArray) {
                    Log.d("DEBUG", "page : " + page + " / timeline: " + jsonArray.toString());
                    // Load json array into model classes
                    try {

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                List<Tweet> tweetList = Tweet.fromJson(jsonArray);

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

        swipeRefreshLayout.setRefreshing(false);
    }

    private void clearViews() {
        tweets.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                showDialogNewTweet(getString(R.string.new_tweet), null);
                break;
        }
    }

    @Override
    public void onSubmitTweet(final String tweet) {
        RestClient client = RestApplication.getRestClient();
        client.postTweet(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "tweet: " + response.toString());

                try {

                    final Tweet mTweet = new Tweet(response);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            tweets.add(0, mTweet);
                            adapter.notifyItemInserted(0);
                            rvTweets.scrollToPosition(0);

                            mTweet.setType_tweet(0);
                            mTweet.save();

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Menu items management
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    /**
     * Menu item click management
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.main_action_logout) {

            RestClient client = RestApplication.getRestClient();
            client.clearAccessToken();
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check is device is connected to the internet
     *
     * @return
     */
    public boolean isOnline() {
        return true;
        /*Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;*/
    }
}
