package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.rest.RestApplication;
import com.codepath.apps.restclienttemplate.rest.RestClient;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailsActivity extends AppCompatActivity
        implements View.OnClickListener, TextWatcher {

    @BindView(R.id.tweet_details_img_user)
    ImageView img_user;

    @BindView(R.id.tweet_details_tv_name)
    TextView tv_name;

    @BindView(R.id.tweet_details_tv_screen_name)
    TextView tv_screen_name;

    @BindView(R.id.tweet_details_tv_time)
    TextView tv_time;

    @BindView(R.id.tweet_details_tv_text)
    TextView tv_text;

    @BindView(R.id.tweet_details_video_tweet)
    VideoView video_tweet;

    @BindView(R.id.tweet_details_img_tweet)
    ImageView img_tweet;

    @BindView(R.id.tweet_details_ll_tweet)
    LinearLayout ll_tweet;

    @BindView(R.id.tweet_details_edt_tweet)
    EditText edt_tweet;

    @BindView(R.id.tweet_details_tv_remains_char)
    TextView tv_remains_char;

    @BindView(R.id.tweet_details_btn_tweet)
    Button btn_tweet;

    private static final int TWEET_LENGTH = 140;

    Tweet tweet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Init Bind of views
        ButterKnife.bind(this);

        btn_tweet.setOnClickListener(this);
        edt_tweet.addTextChangedListener(this);
        tv_remains_char.setText(String.format(Locale.US, "%d", (TWEET_LENGTH)));

        Intent intent = getIntent();
        tweet = (Tweet) Parcels.unwrap(intent.getParcelableExtra("TWEET"));

        if (tweet == null)
            finish();

        tv_name.setText(tweet.getUser_name());
        tv_screen_name.setText(String.format(Locale.US, "@%s", tweet.getUser_screen_name()));
        tv_text.setText(tweet.getText());
        tv_time.setText(ParseRelativeDate.getRelativeTimeAgo(tweet.getCreated_at()));

        edt_tweet.setText(String.format(Locale.US, "@%s", tweet.getUser_screen_name()));

        Glide.with(this)
                .load(tweet.getUser_profile_image_url_https())
                .placeholder(R.drawable.tw__ic_tweet_photo_error_light)
                .error(R.drawable.tw__ic_tweet_photo_error_light)
                .bitmapTransform(new RoundedCornersTransformation(this, 30, 10))
                .bitmapTransform(new CropCircleTransformation(this))
                .into(img_user);


        if (tweet.getTweet_media_type() != null && tweet.getTweet_media_url() != null) {

            if (tweet.getTweet_media_type().equals("video")) {

                video_tweet.setVisibility(View.VISIBLE);
                Uri vidUri = Uri.parse(tweet.getTweet_media_url());
                video_tweet.setVideoURI(vidUri);
                video_tweet.setBackgroundResource(R.drawable.tw__ic_tweet_photo_error_light);

                MediaController vidControl = new MediaController(this);
                vidControl.setAnchorView(video_tweet);
                vidControl.setVisibility(View.GONE);
                video_tweet.setMediaController(vidControl);
                video_tweet.requestFocus();
                video_tweet.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        video_tweet.start();
                    }
                });

                video_tweet.start();

            } else {
                img_tweet.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(tweet.getTweet_media_url())
                        .into(img_tweet);
            }

        }


    }

    private void setValues() {
        if (edt_tweet.getText().toString().length() < 5) {
            edt_tweet.setError(getString(R.string.tweet_length));
            return;
        }

        RestClient client = RestApplication.getRestClient();
        client.postTweet(edt_tweet.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "tweet: " + response.toString());

                try {

                    final Tweet mTweet = new Tweet(response);
                    DetailsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(ll_tweet, R.string.success_tweet, Snackbar.LENGTH_LONG)
                                    .show();
                            edt_tweet.setText(String.format(Locale.US, "@%s", tweet.getUser_screen_name()));
                        }
                    });


                    //finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Snackbar.make(ll_tweet, R.string.error_tweet, Snackbar.LENGTH_LONG)
                        .show();
            }
        });


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tweet_details_btn_tweet)
            setValues();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        ll_tweet.setVisibility(editable.length() > (tweet.getUser_screen_name().length() + 1) ? View.VISIBLE : View.GONE);

        int remain_char = TWEET_LENGTH - editable.length();

        tv_remains_char.setTextColor(Color.GRAY);

        if (remain_char < 10)
            tv_remains_char.setTextColor(Color.RED);

        tv_remains_char.setText(String.format(Locale.US, "%d", remain_char));
    }
}
