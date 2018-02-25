package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.MyUtils;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);

        holder.tv_user_name.setText(
                MyUtils.fromHtml(
                        String.format(Locale.US, "<b>%s</b>\n@%s", tweet.getUser_name(), tweet.getUser_screen_name())
                )
        );

        holder.tv_create_at.setText(ParseRelativeDate.getRelativeTimeAgo(tweet.getCreated_at()));

        holder.tv_text.setText(tweet.getText());

        Glide.with(getContext())
                .load(tweet.getUser_profile_image_url_https())
                .placeholder(R.drawable.tw__ic_tweet_photo_error_light)
                .error(R.drawable.tw__ic_tweet_photo_error_light)
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 30, 10))
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(holder.img_user);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
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


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
