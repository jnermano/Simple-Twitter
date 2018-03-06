package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.ProfileActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.MyUtils;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;

import org.parceler.Parcels;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Ermano
 * on 2/24/2018.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    List<Tweet> tweets;
    Context mContext;

    public TweetAdapter(Context mContext, List<Tweet> tweets) {
        this.tweets = tweets;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.tweet_adapter, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);

        holder.tv_user_name.setText(
                MyUtils.fromHtml(
                        String.format(Locale.US, "<b>%s</b>\n@%s", tweet.getUser().getName(), tweet.getUser().getScreen_name())
                )
        );

        holder.tv_create_at.setText(ParseRelativeDate.getRelativeTimeAgo(tweet.getCreated_at()));

        holder.tv_text.setText(tweet.getBody());

        Glide.with(getContext())
                .load(tweet.getUser().getProfile_imageURL())
                .placeholder(R.drawable.tw__ic_tweet_photo_error_light)
                .error(R.drawable.tw__ic_tweet_photo_error_light)
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 30, 10))
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(holder.img_user);



        if (tweet.getEntities() != null && tweet.getEntities().getMedia() != null) {

            if (tweet.getEntities().getMedia().getMedia_type().equals("video")) {

                holder.img_tweet.setVisibility(View.GONE);

                holder.video_tweet.setVisibility(View.VISIBLE);
                Uri vidUri = Uri.parse(tweet.getEntities().getMedia().getVideo_url());
                holder.video_tweet.setVideoURI(vidUri);
                holder.video_tweet.setBackgroundResource(R.drawable.tw__ic_tweet_photo_error_light);

                MediaController vidControl = new MediaController(getContext());
                vidControl.setAnchorView(holder.video_tweet);
                vidControl.setVisibility(View.GONE);
                holder.video_tweet.setMediaController(vidControl);
                holder.video_tweet.requestFocus();
                holder.video_tweet.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        holder.video_tweet.start();
                    }
                });

                holder.video_tweet.start();

            }else {

                holder.video_tweet.setVisibility(View.GONE);

                holder.img_tweet.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(tweet.getEntities().getMedia().getMedia_url())
                        .into(holder.img_tweet);
            }

        }else {
            holder.img_tweet.setVisibility(View.GONE);
            holder.video_tweet.setVisibility(View.GONE);
        }

        holder.img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("USER", Parcels.wrap(tweet.getUser()));
                getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public Tweet getItem(int position){
        return tweets.get(position);
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tweet_adapter_img_user)
        ImageView img_user;

        @BindView(R.id.tweet_adapter_tv_name)
        TextView tv_user_name;

        @BindView(R.id.tweet_adapter_tv_time)
        TextView tv_create_at;

        @BindView(R.id.tweet_adapter_tv_text)
        TextView tv_text;

        @BindView(R.id.tweet_adapter_img_tweet)
        ImageView img_tweet;


        @BindView(R.id.tweet_adapter_video_tweet)
        VideoView video_tweet;




        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
