package com.example.fitnessapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class profile_fragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView logout;
    private Dialog popup_logout;

    public profile_fragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment,container,false);
        logout = (TextView)view.findViewById(R.id.log_out);

        logout.setOnClickListener(this);

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
                    //container.setBackgroundColor(getResources().getColor(R.color.black));
                    //container.setBackgroundResource(R.drawable.card_bg_dark);

                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.viewpager, new profile_fragment()).commit();
                    //container.setBackgroundColor(getResources().getColor(R.color.white));
                    //container.setBackgroundResource(R.drawable.card_bg);
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
        }
    }
}
