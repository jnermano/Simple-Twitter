package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.codepath.apps.restclienttemplate.database.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Ermano
 * on 3/3/2018.
 */


@Table(database = MyDatabase.class)
@org.parceler.Parcel(analyze={Media.class})
public class Media extends BaseModel implements Parcelable {

    @Column
    String media_url;
    @Column
    @PrimaryKey
    String media_type;

    @Column
    String video_url;


    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }





    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }



    public Media() {
    }

    protected Media(Parcel in) {
        media_url = in.readString();
        media_type = in.readString();
        video_url = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public static Media fromJSON(JSONArray object) throws JSONException {
        Media media = new Media();

        Log.i("Media Model","Media exists");

        media.media_url = object.getJSONObject(0).getString("media_url");
        media.media_type = object.getJSONObject(0).getString("type");

        if(media.media_type.equals("video")){
            media.video_url = object.getJSONObject(0).getJSONObject("video_info").getJSONArray("variants").getJSONObject(0).getString("url");
            Log.i("Media Model","Video exists");

        }
        else{
            media.video_url = null;
        }

        return media;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(media_url);
        parcel.writeString(media_type);
        parcel.writeString(video_url);
    }
}
