package com.rest.fitnessapp.profileFragmentTabs;

import android.content.Context;
import android.content.SharedPreferences;

public class DarkModePrefManager {

    SharedPreferences pref;

    public DarkModePrefManager(Context c)
    {
        pref = c.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    // This method saves the dark mode state : True or False
    public void setDarkMode(Boolean state)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("NightMode", state);
        editor.apply();
    }

    // This method will load the dark mode state
    public Boolean loadDarkModeState()
    {
        return pref.getBoolean("NightMode", false);
    }

}
