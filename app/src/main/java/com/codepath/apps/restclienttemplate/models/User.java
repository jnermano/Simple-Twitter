package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.codepath.apps.restclienttemplate.database.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by Ermano
 * on 3/3/2018.
 */

@Table(database = MyDatabase.class)
@org.parceler.Parcel(analyze={User.class})
public class User extends BaseModel implements Parcelable {

    @Column
    String name;

    @Column
    String profile_imageURL;



    @Column
    @PrimaryKey
    Long uid;

    @Column
    String screen_name;


    @Column
    int followers;


    @Column
    int following;

    @Column
    String location;

    @Column
    String desc;

    @Column
    String profile_background_url;

    public String getProfile_background_url() {
        return profile_background_url;
    }

    public void setProfile_background_url(String profile_background_url) {
        this.profile_background_url = profile_background_url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setProfile_imageURL(String profile_imageURL) {
        this.profile_imageURL = profile_imageURL;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public User(){

    }



    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getProfile_imageURL() {
        return profile_imageURL;
    }

    public Long getUid() {
        return uid;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public static User fromJSON(JSONObject object) throws JSONException {

        User user = new User();
        user.name = object.getString("name");
        user.profile_imageURL = object.getString("profile_image_url");
        user.screen_name  = object.getString("screen_name");
        user.uid = object.getLong("id");
        user.followers = object.getInt("followers_count");
        user.following = object.getInt("friends_count");
        user.location = object.getString("location");
        user.desc = object.getString("description");
        user.profile_background_url = object.getString("profile_background_image_url");

        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(profile_imageURL);
        parcel.writeLong(uid);
        parcel.writeString(screen_name);
        parcel.writeInt(followers);
        parcel.writeInt(following);
        parcel.writeString(location);
        parcel.writeString(desc);
        parcel.writeString(profile_background_url);
    }


    public User(Parcel in) {
        name = in.readString();
        profile_imageURL = in.readString();
        uid = in.readLong();
        screen_name = in.readString();
        followers = in.readInt();
        following = in.readInt();
        location = in.readString();
        desc = in.readString();
        profile_background_url = in.readString();
    }
}
