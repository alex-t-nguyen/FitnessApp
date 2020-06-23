package com.rest.fitnessapp.homeFragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.ViewPager;

import com.facebook.login.LoginManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.rest.fitnessapp.Login.MainActivity;
import com.rest.fitnessapp.R;
import com.rest.fitnessapp.ViewPageAdapter;
import com.rest.fitnessapp.profileFragmentTabs.DarkModePrefManager;
import com.rest.fitnessapp.profileFragmentTabs.logoutDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity implements logoutDialog.Communicator {

    private static final String TAG = "HOME";
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private DarkModePrefManager darkModePrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        darkModePrefManager = new DarkModePrefManager(this);
        if (darkModePrefManager.loadDarkModeState())
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setTheme(R.style.darkthemeProfile);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(R.style.lightThemeProfile);
        }

        setContentView(R.layout.activity_home);
        calendarDate();
        tabLayout = findViewById(R.id.navbar);
        appBarLayout = findViewById(R.id.appBar);
        viewPager = findViewById(R.id.viewpager);
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());

        // Adding Fragments
        adapter.addFragment(new menu_fragment(),"Menu");
        adapter.addFragment(new calendar_fragment(),"Schedule");
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

    @Override
    public void onDialogLogout(String message) {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut(); // Logout of facebook to change button on login-screen
        this.finish();
        Toast.makeText(this, "User logged out", Toast.LENGTH_SHORT).show();
        Intent returnLogin = new Intent(this, MainActivity.class);
        startActivity(returnLogin);
    }
}
