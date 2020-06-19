package com.example.fitnessapp.homeFragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fitnessapp.Introduction.IntroActivity;
import com.example.fitnessapp.R;
import com.example.fitnessapp.profileFragmentTabs.DarkModePrefManager;
import com.example.fitnessapp.profileFragmentTabs.changePassword.change_pass_authenticate;
import com.example.fitnessapp.profileFragmentTabs.editProfile;
import com.example.fitnessapp.profileFragmentTabs.logoutDialog;
import com.google.firebase.auth.FirebaseAuth;

public class profile_fragment extends Fragment implements View.OnClickListener {
    private final static String TAG = "profileFragment";
    private View view;
    private FirebaseAuth mAuth;
    private TextView logout, editprofilebtn, helpclick, changePassbtn, notification;
    DarkModePrefManager darkModePrefManager;
    private Switch darkModeSwitch;

    public profile_fragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        darkModePrefManager = new DarkModePrefManager(getActivity());
        if (darkModePrefManager.loadDarkModeState())
        {
            getActivity().setTheme(R.style.darktheme);
        }
        else
            getActivity().setTheme(R.style.AppTheme);

        Log.d(TAG, "view refresh");
        view = inflater.inflate(R.layout.profile_fragment,container,false);
        mAuth = FirebaseAuth.getInstance();

        darkModeSwitch = (Switch) view.findViewById(R.id.darkModeSwitch);
        logout = (TextView)view.findViewById(R.id.log_out);
        editprofilebtn = (TextView)view.findViewById(R.id.edit_profile);
        helpclick= (TextView)view.findViewById(R.id.help);
        notification= (TextView)view.findViewById(R.id.notifications);
        changePassbtn = (TextView)view.findViewById(R.id.change_password);

        editprofilebtn.setOnClickListener(this);
        logout.setOnClickListener(this);
        helpclick.setOnClickListener(this);
        notification.setOnClickListener(this);
        changePassbtn.setOnClickListener(this);


        if(darkModePrefManager.loadDarkModeState())
        {
            darkModeSwitch.setChecked(true);
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Log.d(TAG, "Is Dark");
        }
        else
        {
            darkModeSwitch.setChecked(false);
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Log.d(TAG, "Is Not Dark");
        }
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Log.d(TAG, "Is Checked");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    darkModePrefManager.setDarkMode(true);
                    //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.viewpager, new profile_fragment()).commit();


                }
                else
                {
                    Log.d(TAG, "Not checked");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    darkModePrefManager.setDarkMode(false);
                    Log.d(TAG, "MODE_NIGHT_NO Called");
                    //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.viewpager, new profile_fragment()).commit();

                }
            }
        });

        //setDarkModeSwitch();
        return view;
    }

    private void setDarkModeSwitch()
    {
        if(darkModePrefManager.loadDarkModeState())
        {
            darkModeSwitch.setChecked(true);
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Log.d(TAG, "Is Dark");
        }
        else
        {
            darkModeSwitch.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Log.d(TAG, "Is Not Dark");
        }
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    darkModePrefManager.setDarkMode(true);
                    //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.viewpager, new profile_fragment()).commit();


                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    darkModePrefManager.setDarkMode(false);
                    Log.d(TAG, "MODE_NIGHT_NO Called");
                    //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.viewpager, new profile_fragment()).commit();

                }
            }
        });
    }


    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.edit_profile:
            {
                Log.d(TAG, "Clicked edit profile");
                Intent editProfileMenuIntent = new Intent(getActivity(), editProfile.class);
                startActivity(editProfileMenuIntent);
                break;
            }
            case R.id.change_password:
            {
                Intent changePassIntent = new Intent(getActivity(), change_pass_authenticate.class);
                startActivity(changePassIntent);
                break;
            }
            case R.id.log_out:
            {
                /*
                    Because getActivity of fragment returns the activity hosting the fragment (Home.java), we have Home.java implement
                    the logoutDialog.java method to log the user out, rather than this fragment.
                    getActivity() returns Home.java, so need to implement the method from there
                 */
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                logoutDialog logDialog = new logoutDialog();
                logDialog.show(fragmentManager, "Logout");
                /*
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                getActivity().finish();
                Toast.makeText(getActivity(), "User logged out", Toast.LENGTH_SHORT).show();
                Intent returnLogin = new Intent(getActivity(), MainActivity.class);
                startActivity(returnLogin);
                 */
                break;
            }
            case R.id.help:
            {
                Intent goToHelp = new Intent(getActivity(), IntroActivity.class);
                goToHelp.putExtra("Helper",true);
                startActivity(goToHelp);
                break;
            }
            case R.id.notifications: {
                Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());

                startActivity(settingsIntent);
            }

        }
    }
}
