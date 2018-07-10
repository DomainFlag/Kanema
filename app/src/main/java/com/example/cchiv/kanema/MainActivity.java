package com.example.cchiv.kanema;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static int mNbComponents = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager_slider);
        SliderPageAdapter mPagerAdapter = new SliderPageAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        setViewPagerPreferences(mViewPager);
        setSharedPreferences();
    }

    public void setViewPagerPreferences(ViewPager mViewPager) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String entertainment_type = sharedPreferences.getString(getString(R.string.entertainment_key), getString(R.string.entertainment_movies));

        String[] labels = getResources().getStringArray(R.array.settings_item_keys);
        int index = Arrays.asList(labels).indexOf(entertainment_type);

        mViewPager.setCurrentItem(index);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    public class SliderPageAdapter extends FragmentPagerAdapter {

        private List<String> components;
        private Context context;

        public SliderPageAdapter(Context context, FragmentManager fm) {
            super(fm);

            this.context = context;

            String[] data = this.context.getResources().getStringArray(R.array.settings_item_labels);
            this.components = Arrays.asList(data);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new SlidePageFragment();

            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.entertainment_key), this.components.get(position));

            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return mNbComponents;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item_popularity) {
//            fetchMovies(Constants.FILTER_BY_POPULARITY);
        } else if(item.getItemId() == R.id.menu_item_rating) {
//            fetchMovies(Constants.FILTER_BY_RATING);
        } else if(item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    public void setSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
}
