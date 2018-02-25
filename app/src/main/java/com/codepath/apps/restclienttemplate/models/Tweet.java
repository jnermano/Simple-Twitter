package com.codepath.apps.restclienttemplate.models;

/**
 * Created by Ermano
 * on 2/24/2018.
 */

import com.codepath.apps.restclienttemplate.database.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;


@Table(database = MyDatabase.class)
@Parcel(analyze={Tweet.class})
public class Tweet extends BaseModel {
    // Define database columns and associated fields
    @PrimaryKey
    @Column
    Long id;
    @Column
    String created_at;
    @Column
    String text;

    @Column
    String tweet_media_url;

    @Column
    String tweet_media_type;

    @Column
    Long user_id;
    @Column
    String user_name;
    @Column
    String user_screen_name;
    @Column
    String user_profile_image_url_https;

    @Column
    int type_tweet;

    public Tweet(){}

    // Add a constructor that creates an object from the JSON response
    public Tweet(JSONObject object){
        super();

        try {
            this.id = object.getLong("id");
            this.created_at = object.getString("created_at");
            this.text = object.getString("text");

            if (!object.isNull("extended_entities")) {
                JSONObject entitiesObject = object.getJSONObject("extended_entities");
                JSONArray mediaArray = entitiesObject.getJSONArray("media");
                if (mediaArray.length() > 0) {
                    JSONObject mediaObject = mediaArray.getJSONObject(0);
                    this.tweet_media_url = mediaObject.getString("media_url_https");
                    this.tweet_media_type = mediaObject.getString("type");
                    if (this.tweet_media_type.equals("video")) {
                        JSONObject video_info = mediaObject.getJSONObject("video_info");
                        JSONArray variantsArray = video_info.getJSONArray("variants");
                        if (variantsArray.length() > 0) {
                            this.tweet_media_url = variantsArray.getJSONObject(0).getString("url");
                        }
                    }
                }
            }



            JSONObject userJson = object.getJSONObject("user");

            this.user_id = userJson.getLong("id");
            this.user_name = userJson.getString("name");
            this.user_screen_name = userJson.getString("screen_name");
            this.user_profile_image_url_https = userJson.getString("profile_image_url_https");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = new Tweet(tweetJson);
            tweet.setType_tweet(0);
            tweet.save();
            tweets.add(tweet);
        }

        return tweets;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_screen_name() {
        return user_screen_name;
    }

    public void setUser_screen_name(String user_screen_name) {
        this.user_screen_name = user_screen_name;
    }

    public String getUser_profile_image_url_https() {
        return user_profile_image_url_https;
    }

    public void setUser_profile_image_url_https(String user_profile_image_url_https) {
        this.user_profile_image_url_https = user_profile_image_url_https;
    }

    public int getType_tweet() {
        return type_tweet;
    }

    public void setType_tweet(int type_tweet) {
        this.type_tweet = type_tweet;
    }

    public String getTweet_media_url() {
        return tweet_media_url;
    }

    public void setTweet_media_url(String tweet_media_url) {
        this.tweet_media_url = tweet_media_url;
    }

    public String getTweet_media_type() {
        return tweet_media_type;
    }

    public void setTweet_media_type(String tweet_media_type) {
        this.tweet_media_type = tweet_media_type;
    }
}
