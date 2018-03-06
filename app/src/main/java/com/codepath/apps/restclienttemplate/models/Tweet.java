package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.codepath.apps.restclienttemplate.database.MyDatabase;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ermano
 * on 3/3/2018.
 */

@Table(database = MyDatabase.class)
@org.parceler.Parcel(analyze={Tweet.class})
public class Tweet extends BaseModel implements Parcelable
{
    @Column
    String created_at;

    @Column
    @PrimaryKey
    Long tweet_id;

    @Column
    String body;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    User user;


    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Entities entities;

    @Column
    int reply_count;

    @Column
    int retweet_count;

    @Column
    int favorite_count;

    @Column
    boolean retweeted;

    @Column
    boolean favorited;

    @Column
    int type_tweet;

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public int getReply_count() {
        return reply_count;
    }

    public void setReply_count(int reply_count) {
        this.reply_count = reply_count;
    }

    public int getRetweet_count() {
        return retweet_count;
    }

    public void setRetweet_count(int retweet_count) {
        this.retweet_count = retweet_count;
    }

    public int getFavorite_count() {
        return favorite_count;
    }

    public void setFavorite_count(int favorite_count) {
        this.favorite_count = favorite_count;
    }


    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void settweet_id(Long uid) {
        this.tweet_id = uid;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public int getType_tweet() {
        return type_tweet;
    }

    public void setType_tweet(int type_tweet) {
        this.type_tweet = type_tweet;
    }

    public Tweet() {
    }

    public Tweet(Parcel in) {
        this.created_at = in.readString();
        this.tweet_id = in.readLong();
        this.body = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.entities = in.readParcelable(Entities.class.getClassLoader());
        this.reply_count = in.readInt();
        this.retweet_count = in.readInt();
        this.favorite_count = in.readInt();

        this.retweeted = in.readByte() != 0;
        this.favorited = in.readByte() != 0;
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    public String getCreated_at() {
        return created_at;
    }

    public Long gettweet_id() {
        return tweet_id;
    }

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    public static Tweet fromJSON(JSONObject object) throws JSONException {
        Tweet tweet = new Tweet();

       // Log.i(TimelineActivity.TAG,"Inside Tweet class "+object.getString("created_at"));
        tweet.created_at = ParseRelativeDate.getRelativeTimeAgo(object.getString("created_at"));
        tweet.tweet_id = object.getLong("id");
        tweet.body = object.getString("text");
        tweet.user =  User.fromJSON(object.getJSONObject("user"));
        if(object.has("extended_entities")) {
            JSONObject extended_entities = object.getJSONObject("extended_entities");
            Log.i("Tweet Model","Media exists "+tweet.user.getScreen_name());
            if (extended_entities != null) {
                tweet.entities = Entities.fromJSON(extended_entities, tweet.tweet_id);
            } else {
                tweet.entities = null;
            }
        }
        else{
            tweet.entities = null;
        }

        if(object.has("reply_count")) {
            tweet.reply_count = object.getInt("reply_count");
        }
        else
            tweet.reply_count = 0;
        if(object.has("retweet_count")) {
            tweet.retweet_count = object.getInt("retweet_count");
        }
        else
            tweet.retweet_count = 0;


        if(object.has("favorite_count")) {
            tweet.favorite_count = object.getInt("favorite_count");
        }
        else
            tweet.favorite_count = 0;

        tweet.retweeted = object.getBoolean("retweeted");
        tweet.favorited = object.getBoolean("favorited");

        return tweet;
    }


    public static ArrayList<Tweet> getTweets(JSONArray array){
        ArrayList<Tweet> list = new ArrayList<>();

        for(int i=0;i<array.length();i++){
            try {
                Tweet tweet = Tweet.fromJSON(array.getJSONObject(i));
                list.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(created_at);
        parcel.writeLong(tweet_id);
        parcel.writeString(body);
        parcel.writeParcelable(user,i);
        parcel.writeParcelable(entities,i);
        parcel.writeInt(reply_count);
        parcel.writeInt(retweet_count);
        parcel.writeInt(favorite_count);
        parcel.writeByte((byte) (retweeted ? 1 : 0));
        parcel.writeByte((byte) (favorited ? 1 : 0));
    }


   public static List<Tweet> loadRecentItemsfromDB(){
        return new Select().from(Tweet.class).orderBy(Tweet_Table.tweet_id,false).limit(100).queryList();
    }
}
