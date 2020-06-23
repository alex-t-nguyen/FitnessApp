package com.rest.fitnessapp.Clock;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rest.fitnessapp.R;
import com.rest.fitnessapp.ViewPageAdapter;
import com.rest.fitnessapp.profileFragmentTabs.DarkModePrefManager;

public class timer_stopwatch extends AppCompatActivity {

    private DarkModePrefManager darkModePrefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        darkModePrefManager = new DarkModePrefManager(this);
        if (darkModePrefManager.loadDarkModeState())
        {
            setTheme(R.style.darkthemeProfile);
        }
        else
            setTheme(R.style.lightThemeProfile);

        setContentView(R.layout.activity_timer_stopwatch);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
                //onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}