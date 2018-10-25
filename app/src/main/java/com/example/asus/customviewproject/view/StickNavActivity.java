package com.example.asus.customviewproject.view;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.example.asus.customviewproject.R;

public class StickNavActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private ViewPagerFragment[] mViewPagerFragments;
    String[] mTitles = {"简介", "相关", "推荐"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sticknav);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        mViewPagerFragments = new ViewPagerFragment[mTitles.length];
        for(int i = 0; i < mTitles.length; i++){
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitles[i]));
            mViewPagerFragments[i] = new ViewPagerFragment();
        }
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mViewPagerFragments[position];
            }

            @Override
            public int getCount() {
                return mViewPagerFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        };
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
