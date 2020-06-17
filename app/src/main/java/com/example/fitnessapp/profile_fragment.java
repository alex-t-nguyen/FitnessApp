package com.example.fitnessapp;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentActivity;

import com.example.fitnessapp.profileFragmentTabs.editProfile;
import com.example.fitnessapp.profileFragmentTabs.logoutDialog;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class profile_fragment extends Fragment implements View.OnClickListener {
    private final static String TAG = "profileFragment";
    private View view;
    private FirebaseAuth mAuth;
    private TextView logout, editprofilebtn,notification;
    private TextView helpclick;
    private Dialog popup_logout;
    public profile_fragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment,container,false);
        mAuth = FirebaseAuth.getInstance();
        logout = (TextView)view.findViewById(R.id.log_out);
        editprofilebtn = (TextView)view.findViewById(R.id.edit_profile);
        helpclick= (TextView)view.findViewById(R.id.help);
        notification= (TextView)view.findViewById(R.id.notifications);

        editprofilebtn.setOnClickListener(this);
        logout.setOnClickListener(this);
        helpclick.setOnClickListener(this);
        notification.setOnClickListener(this);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            getActivity().setTheme(R.style.darktheme);
        }
        else
        {
            getActivity().setTheme(R.style.AppTheme);
        }
        setDarkModeSwitch();
        return view;
    }

    private void setDarkModeSwitch()
    {
        Switch darkModeSwitch = (Switch) view.findViewById(R.id.darkModeSwitch);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        {
            darkModeSwitch.setChecked(true);
        }
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.viewpager, new profile_fragment()).commit();

                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.viewpager, new profile_fragment()).commit();
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
