package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.fragments.FragmentMentions;
import com.codepath.apps.restclienttemplate.fragments.FragmentNewTweet;
import com.codepath.apps.restclienttemplate.fragments.FragmentTimeline;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.rest.RestApplication;
import com.codepath.apps.restclienttemplate.rest.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FragmentNewTweet.FragmentNewTweetListener {

    @BindView(R.id.fab)
    FloatingActionButton btn_tweet;

    @BindView(R.id.main_viewpager)
    ViewPager viewPager;

    @BindView(R.id.main_sliding_tabs)
    TabLayout tabLayout;

    FragmentTimeline fragmentTimeline;
    FragmentMentions fragmentMentions;

    User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getCurrentUser();

        // Init Bind of views
        ButterKnife.bind(this);

        btn_tweet.setOnClickListener(this);

        fragmentTimeline = new FragmentTimeline();
        fragmentMentions = new FragmentMentions();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);


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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                showDialogNewTweet(getString(R.string.new_tweet), null);
                break;
        }
    }

    /**
     * get current connected user
     */
    private void  getCurrentUser(){
        RestClient client = RestApplication.getRestClient();
        client.getAuthUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "user: " + response.toString());

                try {

                    current_user = User.fromJSON(response);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * submit tweet
     * @param tweet
     */

    @Override
    public void onSubmitTweet(final String tweet) {
        RestClient client = RestApplication.getRestClient();
        client.postTweet(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "tweet: " + response.toString());

                try {

                    final Tweet mTweet = Tweet.fromJSON(response);

                    mTweet.setType_tweet(0);
                    mTweet.save();

                    fragmentTimeline.putTweet(mTweet);

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
        } else if(id == R.id.main_action_profile){
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("USER", Parcels.wrap(current_user));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Created by Ermano
     * on 3/3/2018.
     */

    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        Context context;


        public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return fragmentTimeline;
                case 1:
                    return fragmentMentions;
            }
            return fragmentTimeline;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return getResources().getStringArray(R.array.tab_text)[position];
        }

        /*@Override
        public CharSequence getPageTitle(int position) {

            Integer[] imageResId = new Integer[]{
                    R.drawable.ic_action_home,
                    R.drawable.ic_mention
            };

            // Generate title based on item position
            Drawable image = context.getResources().getDrawable(imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            // Replace blank spaces with image icon
            SpannableString sb = new SpannableString("   " + getResources().getStringArray(R.array.tab_text)[position]);
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }*/

    }


}
