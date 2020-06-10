package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
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

import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class profile_fragment extends Fragment implements View.OnClickListener {
    private View view;
    private FirebaseAuth mAuth;
    private TextView logout;
    private TextView helpclick;
    public profile_fragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment,container,false);
        mAuth = FirebaseAuth.getInstance();
        logout = (TextView)view.findViewById(R.id.log_out);
        helpclick= (TextView)view.findViewById(R.id.help);

        logout.setOnClickListener(this);
        helpclick.setOnClickListener(this);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
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
                    getFragmentManager().beginTransaction().replace(R.id.viewpager, new profile_fragment()).commit();
                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getFragmentManager().beginTransaction().replace(R.id.viewpager, new profile_fragment()).commit();
                }
            }
        });
    }


    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.log_out:
            {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                getActivity().finish();
                Toast.makeText(getActivity(), "User logged out", Toast.LENGTH_SHORT).show();
                Intent returnLogin = new Intent(getActivity(), MainActivity.class);
                startActivity(returnLogin);
                break;
            }
            case R.id.help:
            {
                Intent goToHelp = new Intent(getActivity(), IntroActivity.class);
                goToHelp.putExtra("Helper",true);
                startActivity(goToHelp);
                break;
            }
        }
    }
}
