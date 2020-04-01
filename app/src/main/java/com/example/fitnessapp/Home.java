package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        calendarDate();
        tabLayout = (TabLayout) findViewById(R.id.navbar);
        appBarLayout = (AppBarLayout)findViewById(R.id.appBar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());
        // Adding Fragments

        adapter.addFragment(new menu_fragment(),"Menu");
        adapter.addFragment(new geolocation_fragment(),"Map");
        adapter.addFragment(new profile_fragment(),"Profile");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void calendarDate()
    {
        String date_n = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
        TextView date = findViewById(R.id.calendarDate);
        date.setText(date_n);
    }
}
