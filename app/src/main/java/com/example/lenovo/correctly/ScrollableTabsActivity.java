package com.example.lenovo.correctly;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.lenovo.correctly.adapter.ViewPagerAdapter;
import com.example.lenovo.correctly.fragments.EightFragment;
import com.example.lenovo.correctly.fragments.FiveFragment;
import com.example.lenovo.correctly.fragments.FourFragment;
import com.example.lenovo.correctly.fragments.NineFragment;
import com.example.lenovo.correctly.fragments.OneFragment;
import com.example.lenovo.correctly.fragments.SevenFragment;
import com.example.lenovo.correctly.fragments.SixFragment;
import com.example.lenovo.correctly.fragments.TenFragment;
import com.example.lenovo.correctly.fragments.ThreeFragment;
import com.example.lenovo.correctly.fragments.TwoFragment;

public class ScrollableTabsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollable_tabs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "ONE");
        adapter.addFragment(new TwoFragment(), "TWO");
        adapter.addFragment(new ThreeFragment(), "THREE");
        adapter.addFragment(new FourFragment(), "FOUR");
        adapter.addFragment(new FiveFragment(), "FIVE");
        adapter.addFragment(new SixFragment(), "SIX");
        adapter.addFragment(new SevenFragment(), "SEVEN");
        adapter.addFragment(new EightFragment(), "EIGHT");
        adapter.addFragment(new NineFragment(), "NINE");
        adapter.addFragment(new TenFragment(), "TEN");
        viewPager.setAdapter(adapter);
    }
}
