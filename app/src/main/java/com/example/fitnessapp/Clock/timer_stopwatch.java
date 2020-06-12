package com.example.fitnessapp.Clock;

import android.os.Bundle;

import com.example.fitnessapp.R;
import com.example.fitnessapp.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

public class timer_stopwatch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_stopwatch);

        // Maybe not needed
        //SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        final ViewPager viewPager = findViewById(R.id.view_pager);    // Get viewpager from view
        TabLayout tabs = findViewById(R.id.tabs);

        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new stopwatchFragment(), "Stopwatch");
        adapter.addFragment(new timerFragment(), "Timer");

        viewPager.setAdapter(adapter); // Set adapter for viewpager
        tabs.setupWithViewPager(viewPager);

    }
}