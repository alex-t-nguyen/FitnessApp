package com.rest.fitnessapp.homeFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rest.fitnessapp.Clock.timer_stopwatch;
import com.rest.fitnessapp.R;
import com.rest.fitnessapp.profileFragmentTabs.DarkModePrefManager;
import com.rest.fitnessapp.workoutCategory.workouts;

public class menu_fragment extends Fragment implements View.OnClickListener {
    private View view;
    private FloatingActionButton fab_plus, fab_add_menu, fab_clear_menu;
    private Animation fabOpen, fabClose, fabRotateClockwise, fabRotateCounterClockwise;
    private boolean isOpen = false;
    private static final String TAG = "Menu";
    private CardView workoutCard, dietCard, clockCard;
    private DarkModePrefManager darkModePrefManager;

    public menu_fragment()
    {

    }
    /*
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cardList = new ArrayList<>();
        cardList.add(new menu_item(R.drawable.muscle_icon, "Save you workouts", "WORKOUTS"));
        cardList.add(new menu_item(R.drawable.maintenance_icon, "Track your time","TIMER / STOPWATCH"));
    }
    */

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

        view = inflater.inflate(R.layout.menu_fragment,container,false);

        /*
        // FAB button ids
        fab_plus = (FloatingActionButton)view.findViewById(R.id.fab_plus);
        fab_add_menu = (FloatingActionButton)view.findViewById(R.id.fab_add);
        fab_clear_menu = (FloatingActionButton)view.findViewById(R.id.fab_clear);
        fabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        fabRotateClockwise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_clockwise);
        fabRotateCounterClockwise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_counter_clockwise);
        */


        // Cards in menu
        workoutCard = view.findViewById(R.id.workout_card);
        //dietCard = (CardView)view.findViewById(R.id.diet_card);
        clockCard = view.findViewById(R.id.timerstopwatch_card);


        /*
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
        */

        /*
        Window window = getActivity().getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        recyclerView = view.findViewById(R.id.menu_frag_recyclerview);
        menu_adapter adapter = new menu_adapter(getContext(), cardList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        */

        workoutCard.setOnClickListener(this);
//        dietCard.setOnClickListener(this);
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


    /*
    @Override
    public void onMenuCardClick(int position) {
        menu_item card = cardList.get(position);
        switch (card.getMainTitle())
        {
            case "Workouts":
            {
                Log.i(TAG, "Clicking workout card");
                Intent workoutIntent = new Intent(getActivity(), workouts.class);
                startActivity(workoutIntent);
                break;
            }
            case "Timer/Stopwatch":
            {
                Log.i(TAG, "Clicking timer card");
                Intent timerStopwatchIntent = new Intent(getActivity(), timer_stopwatch.class);
                startActivity(timerStopwatchIntent);
                break;
            }
            default:
                Log.d(TAG, "Clicking default case");
        }
    }
    */
}
