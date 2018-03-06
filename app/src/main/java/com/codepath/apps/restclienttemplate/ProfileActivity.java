package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.fragments.FragmentFavorites;
import com.codepath.apps.restclienttemplate.fragments.FragmentPhotos;
import com.codepath.apps.restclienttemplate.fragments.FragmentTweets;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.MyUtils;

import org.parceler.Parcels;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProfileActivity extends AppCompatActivity {

    FragmentTweets fragmentTweets;
    FragmentPhotos fragmentPhotos;
    FragmentFavorites fragmentFavorites;

    @BindView(R.id.profile_viewpager)
    ViewPager viewPager;

    @BindView(R.id.profile_sliding_tabs)
    TabLayout tabLayout;

    @BindView(R.id.profile_img_bg)
    ImageView img_bg;

    @BindView(R.id.profile_img_profile)
    ImageView img_profile;

    @BindView(R.id.profile_tv_name)
    TextView tv_name;

    @BindView(R.id.profile_tv_screen_name)
    TextView tv_screen_name;

    @BindView(R.id.profile_btn_follow)
    Button btn_follow;

    @BindView(R.id.profile_tv_body)
    TextView tv_desc;

    @BindView(R.id.profile_tv_followers)
    TextView tv_followers;

    @BindView(R.id.profile_tv_following)
    TextView tv_following;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pofile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Init Bind of views
        ButterKnife.bind(this);

        Intent intent = getIntent();
        user = (User) Parcels.unwrap(intent.getParcelableExtra("USER"));

        if (user == null)
            finish();

        setTitle(user.getName());

        fragmentTweets = FragmentTweets.newInstance(user.getScreen_name());
        fragmentPhotos = new FragmentPhotos();
        fragmentFavorites = FragmentFavorites.newInstance(user.getScreen_name());

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                this));

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        fillUserProfile();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    /**
     * Filling user profile with data
     */

    private void fillUserProfile() {

        tv_name.setText(user.getName());
        tv_screen_name.setText(String.format(Locale.US, "@%s", user.getScreen_name()));
        tv_desc.setText(user.getDesc());

        tv_following.setText(MyUtils.fromHtml(String.format(Locale.US, "<b>%d</b> FOLLOWING", user.getFollowing())));
        tv_followers.setText(MyUtils.fromHtml(String.format(Locale.US, "<b>%d</b> FOLLOWERS", user.getFollowers())));

        Glide.with(this)
                .load(user.getProfile_imageURL())
                .placeholder(R.drawable.tw__ic_tweet_photo_error_light)
                .error(R.drawable.tw__ic_tweet_photo_error_light)
                .fitCenter()
                .into(img_profile);

        Glide.with(this)
                .load(user.getProfile_background_url())
                .fitCenter()
                .into(img_bg);

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
                    return fragmentTweets;
                case 1:
                    return fragmentPhotos;
                case 2:
                    return fragmentFavorites;
            }
            return fragmentTweets;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return getResources().getStringArray(R.array.tab_profile)[position];
        }


    }


}
