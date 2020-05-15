package com.example.fitnessapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitnessapp.R;

import java.util.Locale;

import static androidx.core.content.ContextCompat.getSystemService;

public class timerFragment extends Fragment {

    private View view;

    private TextView countDown;
    private EditText editTextInput;
    private Button buttonStartPause;
    private Button buttonReset;
    private Button buttonSet;
    private Button buttonAdd;

    private Spinner unitSelector;
    private String chosenUnit;
    private boolean unitSelected;

    private CountDownTimer CountDownTimer;

    private boolean timerRunning;
    private long startTimeInMillis = 0;
    private long timeLeftInMillis;
    private long endTime;

    public timerFragment() {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.timer_fragment, container, false);

        countDown = view.findViewById(R.id.text_view_countdown);
        editTextInput = view.findViewById(R.id.edit_text_input);

        unitSelector = view.findViewById(R.id.spinner_selectUnit);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.timer_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSelector.setAdapter(adapter);

        buttonStartPause = view.findViewById(R.id.button_startPause);
        buttonReset = view.findViewById(R.id.button_reset);
        buttonSet = view.findViewById(R.id.button_set);
        buttonAdd = view.findViewById(R.id.button_add);

        unitSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenUnit = parent.getItemAtPosition(position).toString();
                unitSelected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                unitSelected = false;
            }
        });

        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editTextInput.getText().toString();
                if(input.length() == 0)
                {
                    Toast.makeText(getActivity(), "Field cannot be empty", Toast.LENGTH_SHORT);
                    return;
                }

                long millisInput;
                if(chosenUnit.equals("Hours"))
                    millisInput = Long.parseLong(input) * 3600000;
                else if(chosenUnit.equals("Minutes"))
                    millisInput = Long.parseLong(input) * 60000;
                else
                    millisInput = Long.parseLong(input) * 1000;

                if (millisInput == 0) {
                    Toast.makeText(getActivity(), "Input must be a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }

                setTime(millisInput);
                editTextInput.setText("");
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editTextInput.getText().toString();
                if(input.length() == 0)
                {
                    Toast.makeText(getActivity(), "Field cannot be empty", Toast.LENGTH_SHORT);
                    return;
                }

                long millisInput;
                if(chosenUnit.equals("Hours"))
                    millisInput = Long.parseLong(input) * 3600000;
                else if(chosenUnit.equals("Minutes"))
                    millisInput = Long.parseLong(input) * 60000;
                else
                    millisInput = Long.parseLong(input) * 1000;

                if (millisInput == 0) {
                    Toast.makeText(getActivity(), "Input must be a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }

                addTime(millisInput);
                editTextInput.setText("");
            }
        });

        buttonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timerRunning)
                    pauseTimer();
                else
                    startTimer();
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        return view;
    }

    private void setTime(long milliseconds) {
        startTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }

    private void addTime(long milliseconds) {
        startTimeInMillis += milliseconds;
        resetTimer();
        closeKeyboard();
    }
    private void startTimer() {
        endTime = System.currentTimeMillis() + timeLeftInMillis;

        CountDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                updateWatchInterface();
            }
        }.start();

        timerRunning = true;
        updateWatchInterface();
    }

    private void pauseTimer()
    {
        CountDownTimer.cancel();
        timerRunning = false;
        updateWatchInterface();
    }

    private void resetTimer()
    {
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
        updateWatchInterface();
    }

    private void updateCountDownText()
    {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = "";

        if(hours > 0)
        {
            timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }
        else {
            timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }
        countDown.setText(timeLeftFormatted);
    }

    private void updateWatchInterface()
    {
        if(timerRunning)
        {
            editTextInput.setVisibility(View.INVISIBLE);
            buttonSet.setVisibility(View.INVISIBLE);
            buttonAdd.setVisibility(View.INVISIBLE);
            buttonReset.setVisibility(View.INVISIBLE);
            unitSelector.setVisibility(View.INVISIBLE);
            buttonStartPause.setText("Pause");
            buttonStartPause.setTextColor(Color.parseColor("#CC0000"));
        }
        else
        {
            editTextInput.setVisibility(View.VISIBLE);
            buttonSet.setVisibility(View.VISIBLE);
            buttonAdd.setVisibility(View.VISIBLE);
            unitSelector.setVisibility(View.VISIBLE);
            buttonStartPause.setText("Start");
            buttonStartPause.setTextColor(Color.parseColor("#4CAF50"));

            if(timeLeftInMillis < 1000)
                buttonStartPause.setVisibility(View.INVISIBLE);
            else
                buttonStartPause.setVisibility(View.VISIBLE);

            if(timeLeftInMillis < startTimeInMillis)
                buttonReset.setVisibility(View.VISIBLE);
            else
                buttonReset.setVisibility(View.INVISIBLE);
        }
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if(view != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTimeInMillis", startTimeInMillis);
        editor.putLong("millisLeft", timeLeftInMillis);
        editor.putBoolean("timerRunning", timerRunning);
        editor.putLong("endTime", endTime);
        editor.apply();
        if (CountDownTimer != null) {
            CountDownTimer.cancel();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", getActivity().MODE_PRIVATE);
        startTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        timeLeftInMillis = prefs.getLong("millisLeft", startTimeInMillis);
        timerRunning = prefs.getBoolean("timerRunning", false);
        updateCountDownText();
        updateWatchInterface();
        if (timerRunning) {
            endTime = prefs.getLong("endTime", 0);
            timeLeftInMillis = endTime - System.currentTimeMillis();
            if (timeLeftInMillis < 0) {
                timeLeftInMillis = 0;
                timerRunning = false;
                updateCountDownText();
                updateWatchInterface();
            } else {
                startTimer();
            }
        }
    }
}
