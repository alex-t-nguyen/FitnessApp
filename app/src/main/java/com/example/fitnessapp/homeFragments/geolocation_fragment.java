package com.example.fitnessapp.homeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitnessapp.R;
import com.example.fitnessapp.profileFragmentTabs.DarkModePrefManager;

public class geolocation_fragment extends Fragment {
    View view;
    private DarkModePrefManager darkModePrefManager;

    public geolocation_fragment()
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

        view = inflater.inflate(R.layout.geolocation_fragment,container,false);
        return view;
    }
}
