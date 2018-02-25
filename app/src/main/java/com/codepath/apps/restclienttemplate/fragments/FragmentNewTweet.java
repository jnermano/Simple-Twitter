package com.codepath.apps.restclienttemplate.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.Tweet_Table;
import com.codepath.apps.restclienttemplate.rest.RestApplication;
import com.codepath.apps.restclienttemplate.rest.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Ermano
 * on 2/24/2018.
 */

public class FragmentNewTweet extends DialogFragment implements View.OnClickListener, TextWatcher {

    Tweet tweet;

    public interface FragmentNewTweetListener {
        void onSubmitTweet(String tweet);
    }

    @BindView(R.id.frag_new_tweet_btn_cancel)
    Button btn_cancel;

    @BindView(R.id.frag_new_tweet_btn_tweet)
    Button btn_tweet;

    @BindView(R.id.frag_new_tweet_edt_tweet)
    EditText edt_tweet;

    @BindView(R.id.frag_new_tweet_img_user)
    ImageView img_user;

    @BindView(R.id.frag_new_tweet_tv_name)
    TextView tv_name;

    @BindView(R.id.frag_new_tweet_tv_screen_name)
    TextView tv_screen_name;

    @BindView(R.id.frag_new_tweet_tv_remains_char)
    TextView tv_remains_char;

    private static final int TWEET_LENGTH = 140;
    private static final String TITLE = "TITLE", MSG = "MSG";

    public FragmentNewTweet() {
    }

    public static FragmentNewTweet newInstance(String title, String msg) {
        FragmentNewTweet fragmentNewTweet = new FragmentNewTweet();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(MSG, msg);
        fragmentNewTweet.setArguments(bundle);
        return fragmentNewTweet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        btn_cancel.setOnClickListener(this);
        btn_tweet.setOnClickListener(this);
        edt_tweet.addTextChangedListener(this);

        tv_remains_char.setText(String.format(Locale.US, "%d", (TWEET_LENGTH)));



        RestClient client = RestApplication.getRestClient();
        client.getAuthUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", "user: " + response.toString());

                try {

                    tv_name.setText(response.getString("name"));
                    tv_screen_name.setText(String.format(Locale.US, "@%s", response.getString("screen_name")));
                    Glide.with(getContext())
                            .load(response.getString("profile_image_url_https"))
                            .placeholder(R.drawable.tw__ic_tweet_photo_error_light)
                            .error(R.drawable.tw__ic_tweet_photo_error_light)
                            .override(200, 200)
                            .into(img_user);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        List<Tweet> tweets = SQLite.select()
                .from(Tweet.class)
                .where(Tweet_Table.type_tweet.eq(1))
                .queryList();

        if (tweets.size() > 0){
            tweet = tweets.get(0);
            edt_tweet.setText(tweet.getText());
        }


        getDialog().setTitle(getArguments().getString(TITLE)); //getString(R.string.new_tweet)
        String msg = getArguments().getString(MSG);
        if (msg != null)
            edt_tweet.setText(msg);


    }

    private void setValues() {
        if (edt_tweet.getText().toString().length() < 5) {
            edt_tweet.setError(getString(R.string.tweet_length));
            return;
        }

        FragmentNewTweetListener listener = (FragmentNewTweetListener) getActivity();
        listener.onSubmitTweet(edt_tweet.getText().toString());

        if (tweet != null)
            tweet.delete();

        dismiss();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.frag_new_tweet_btn_cancel:
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Confirmation");
                alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
                alertDialog.setMessage("Do you want to save your tweet for later?");
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, (CharSequence) "Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (tweet != null)
                            tweet.delete();
                        dismiss();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, (CharSequence) "Save for later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (edt_tweet.getText().toString().length() >= 5){
                            if (tweet == null){
                                tweet = new Tweet();
                                tweet.setId(11L);
                                tweet.setType_tweet(1);
                            }
                            tweet.setText(edt_tweet.getText().toString());
                            tweet.save();
                            dismiss();
                        }else {
                            edt_tweet.setError(getString(R.string.tweet_length));
                        }

                    }
                });
                alertDialog.show();
                break;

            case R.id.frag_new_tweet_btn_tweet:
                setValues();
                break;

        }
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        int remain_char = TWEET_LENGTH - editable.length();

        tv_remains_char.setTextColor(Color.GRAY);

        if (remain_char < 10)
            tv_remains_char.setTextColor(Color.RED);

        tv_remains_char.setText(String.format(Locale.US, "%d", remain_char));
    }

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.97), WindowManager.LayoutParams.WRAP_CONTENT); //WindowManager.LayoutParams.WRAP_CONTENT
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }


}
