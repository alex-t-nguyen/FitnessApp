package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class menu_fragment extends Fragment implements View.OnClickListener{
    private View view;
    private FloatingActionButton fab_plus, fab_add_menu, fab_clear_menu;
    private Animation fabOpen, fabClose, fabRotateClockwise, fabRotateCounterClockwise;
    private boolean isOpen = false;
    private static final String TAG = "Menu";
    private CardView workoutCard, dietCard, clockCard;
    private ListView mListView;
    public menu_fragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.menu_fragment,container,false);
        fab_plus = (FloatingActionButton)view.findViewById(R.id.fab_plus);
        fab_add_menu = (FloatingActionButton)view.findViewById(R.id.fab_add);
        fab_clear_menu = (FloatingActionButton)view.findViewById(R.id.fab_clear);
        fabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        fabRotateClockwise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_clockwise);
        fabRotateCounterClockwise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_counter_clockwise);

        // Cards in menu
        workoutCard = (CardView)view.findViewById(R.id.workout_card);
        dietCard = (CardView)view.findViewById(R.id.diet_card);
        clockCard = (CardView)view.findViewById(R.id.timerstopwatch_card);

        // FAB button
        fab_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen)  // To close
                {
                    fab_add_menu.startAnimation(fabClose);
                    fab_clear_menu.startAnimation(fabClose);
                    fab_plus.startAnimation(fabRotateCounterClockwise);
                    fab_add_menu.setClickable(false);
                    fab_clear_menu.setClickable(false);
                    fab_add_menu.setVisibility(View.INVISIBLE);
                    fab_clear_menu.setVisibility(View.INVISIBLE);
                    isOpen = false;
                }
                else    // To open
                {
                    fab_add_menu.startAnimation(fabOpen);
                    fab_clear_menu.startAnimation(fabOpen);
                    fab_plus.startAnimation(fabRotateClockwise);
                    fab_add_menu.setClickable(true);
                    fab_clear_menu.setClickable(true);
                    fab_add_menu.setVisibility(View.VISIBLE);
                    fab_clear_menu.setVisibility(View.VISIBLE);
                    isOpen = true;
                }
            }
        });

        workoutCard.setOnClickListener(this);
        dietCard.setOnClickListener(this);
        clockCard.setOnClickListener(this);

        return view;
    }

    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.workout_card:
            {
                Log.i(TAG, "Clicking workout card");
                //getActivity().finish();
                Intent workoutIntent = new Intent(getActivity(), workouts.class);
                startActivity(workoutIntent);
                break;
            }
            case R.id.diet_card:
            {
                Log.i(TAG, "Clicking Diet card");
                //getActivity().finish();
                //Intent dietIntent = new Intent(getActivity(), diet.class);
                //startActivity(dietIntent);
                break;
            }
            case R.id.timerstopwatch_card:
            {
                //getActivity().finish();
                Log.i(TAG, "Clicking timer card");
                Intent timerStopwatchIntent = new Intent(getActivity(), timer_stopwatch.class);
                startActivity(timerStopwatchIntent);
                break;
            }
            default:
                Log.i(TAG, "Clicking default case");
        }
    }
}
