package com.example.cchiv.kanema;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cchiv.kanema.fragments.SlidePageFragment;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private final static int mNbComponents = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SliderPageAdapter pageAdapter = new SliderPageAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.viewpager_slider);
        viewPager.setAdapter(pageAdapter);

        setTabLayout(pageAdapter, viewPager);
        setViewPagerPreferences(viewPager);
    }

    public void setTabLayout(SliderPageAdapter pageAdapter, ViewPager viewPager) {
        TabLayout tabLayout = findViewById(R.id.entertainment_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        for(int it = 0; it < tabLayout.getTabCount(); it++) {
            TabLayout.Tab tab = tabLayout.getTabAt(it);
            if(tab == null)
                continue;

            tab.setCustomView(pageAdapter.getTabView(it));
        }
    }

    public void setViewPagerPreferences(ViewPager mViewPager) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String entertainment_type = sharedPreferences.getString(getString(R.string.entertainment_key), getString(R.string.entertainment_movies));

        String[] labels = getResources().getStringArray(R.array.settings_item_keys);
        int index = Arrays.asList(labels).indexOf(entertainment_type);

        mViewPager.setCurrentItem(index);
    }

    public class SliderPageAdapter extends FragmentPagerAdapter {

        private List<String> components;
        private Context context;

        private SliderPageAdapter(Context context, FragmentManager fm) {
            super(fm);

            this.context = context;

            String[] data = this.context.getResources().getStringArray(R.array.settings_item_labels);
            components = Arrays.asList(data);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new SlidePageFragment();

            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.entertainment_key), this.components.get(position));

            fragment.setArguments(bundle);
            return fragment;
        }

        private View getTabView(int tabPos) {
            View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);

            ImageView tabIcon = view.findViewById(R.id.tab_icon);
            if(tabPos == 0)
                tabIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_movie));
            else if(tabPos == 1)
                tabIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_series));
            else tabIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_white));

            TextView tabText = view.findViewById(R.id.tab_text);
            tabText.setText(getPageTitle(tabPos));

            Toast.makeText(context, getPageTitle(tabPos), Toast.LENGTH_LONG);

            return view;
        }

        @Override
        public int getCount() {
            return mNbComponents;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0)
                return getResources().getString(R.string.entertainment_movies);
            else if(position == 1)
                return getResources().getString(R.string.entertainment_series);
            else return getResources().getString(R.string.entertainment_starred);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }
}
