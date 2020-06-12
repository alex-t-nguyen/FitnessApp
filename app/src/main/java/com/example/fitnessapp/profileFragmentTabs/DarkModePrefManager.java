package com.example.fitnessapp.profileFragmentTabs;

import android.content.Context;
import android.content.SharedPreferences;

public class DarkModePrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    private static final int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "dark-mode";
    private static final String IS_NIGHT_MODE = "isNightMode";

    public DarkModePrefManager(Context c)
    {
        context = c;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setDarkMode(boolean isFirstTime)
    {
        editor.putBoolean(IS_NIGHT_MODE, isFirstTime);
        editor.commit();
    }

    public boolean isNightMode()
    {
        return pref.getBoolean(IS_NIGHT_MODE, true);
    }

}
