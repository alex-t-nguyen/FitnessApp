package com.example.fitnessapp.homeFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.fitnessapp.R;
import com.example.fitnessapp.calendar.calendarBottomSheet;
import com.example.fitnessapp.profileFragmentTabs.DarkModePrefManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class calendar_fragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    View view;

    private static final String TAG = "calendarFragment";

    private Button dateBtn1, dateBtn2, dateBtn3, dateBtn4, dateBtn5, dateBtn6, dateBtn7;
    private LinearLayout llbtn1, llbtn2, llbtn3, llbtn4, llbtn5, llbtn6, llbtn7;

    private DarkModePrefManager darkModePrefManager;

    // Bar Chart variables
    private BarChart barChart;
    private ArrayList<String> dates;
    private BarEntry[] barEntries;
    private ArrayList<BarEntry> barEntries2;
    private Entry selectedEntry;
    private Highlight selectedHighlight;

    private int colorBarDark, colorBarLight, colorLabelDark, colorLabelLight;

    // Firebase variables
    FirebaseDatabase database;
    DatabaseReference myRef;

    public calendar_fragment()
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
        else {
            getActivity().setTheme(R.style.AppTheme);
        }

        view = inflater.inflate(R.layout.calendar_fragment,container,false);

        // Colors
        colorBarDark = ContextCompat.getColor(getActivity(), R.color.colorAccent2);
        colorBarLight = ContextCompat.getColor(getActivity(), R.color.colorAccent);
        colorLabelDark = ContextCompat.getColor(getActivity(), R.color.white);
        colorLabelLight = ContextCompat.getColor(getActivity(), R.color.black);

        // Instantiate widgets
        dateBtn1 = (Button)view.findViewById(R.id.dateBtn1);
        dateBtn2 = (Button)view.findViewById(R.id.dateBtn2);
        dateBtn3 = (Button)view.findViewById(R.id.dateBtn3);
        dateBtn4 = (Button)view.findViewById(R.id.dateBtn4);
        dateBtn5 = (Button)view.findViewById(R.id.dateBtn5);
        dateBtn6 = (Button)view.findViewById(R.id.dateBtn6);
        dateBtn7 = (Button)view.findViewById(R.id.dateBtn7);

        llbtn1 = (LinearLayout)view.findViewById(R.id.linearlayoutbtn1);
        llbtn2 = (LinearLayout)view.findViewById(R.id.linearlayoutbtn2);
        llbtn3 = (LinearLayout)view.findViewById(R.id.linearlayoutbtn3);
        llbtn4 = (LinearLayout)view.findViewById(R.id.linearlayoutbtn4);
        llbtn5 = (LinearLayout)view.findViewById(R.id.linearlayoutbtn5);
        llbtn6 = (LinearLayout)view.findViewById(R.id.linearlayoutbtn6);
        llbtn7 = (LinearLayout)view.findViewById(R.id.linearlayoutbtn7);

        // Create bar graph
        Date refDate = Calendar.getInstance().getTime();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_WEEK, 6);
        Date refDate2 = c.getTime();
        Log.d(TAG, "Ref date 1: " + refDate.toString());
        Log.d(TAG, "Ref date 2: " + refDate2.toString());
        barChart = (BarChart)view.findViewById(R.id.bargraph);

        final ArrayList<String> xLabel = new ArrayList<>();
        xLabel.add("S");
        xLabel.add("M");
        xLabel.add("T");
        xLabel.add("W");
        xLabel.add("T");
        xLabel.add("F");
        xLabel.add("S");

        XAxis xaxis = barChart.getXAxis();
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setDrawGridLines(false);
        xaxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xLabel.get((int)value);
            }
        });

        barChart.setOnChartValueSelectedListener(this);
        barChart.setDescription(null);
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.setDrawBorders(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        barChart.setHighlightPerDragEnabled(false);
        barChart.setScaleEnabled(false);

        createBarGraph();

        barChart.getLegend().setEnabled(false);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                barChart.notifyDataSetChanged();
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Date[] days = getDaysOfWeek(refDate, Calendar.getInstance().getFirstDayOfWeek());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Calendar").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        SimpleDateFormat ft = new SimpleDateFormat("d");
        for (Date day : days) {
            Log.d(TAG, ft.format(day));
        }

        Button[] dateBtnList = {dateBtn1, dateBtn2, dateBtn3, dateBtn4, dateBtn5, dateBtn6, dateBtn7};

        // Assign all button numbers matching current days of the week
        for (int btnNum = 0; btnNum < 7; btnNum++)
        {
            dateBtnList[btnNum].setText(ft.format(days[btnNum]));
        }


        dateBtn1.setOnClickListener(this);
        dateBtn2.setOnClickListener(this);
        dateBtn3.setOnClickListener(this);
        dateBtn4.setOnClickListener(this);
        dateBtn5.setOnClickListener(this);
        dateBtn6.setOnClickListener(this);
        dateBtn7.setOnClickListener(this);


        llbtn1.setOnClickListener(this);
        llbtn2.setOnClickListener(this);
        llbtn3.setOnClickListener(this);
        llbtn4.setOnClickListener(this);
        llbtn5.setOnClickListener(this);
        llbtn6.setOnClickListener(this);
        llbtn7.setOnClickListener(this);

        return view;
    }

    private static Date[] getDaysOfWeek(Date refDate, int firstDayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        Date[] daysOfWeek = new Date[7];
        for (int i = 0; i < 7; i++) {
            daysOfWeek[i] = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return daysOfWeek;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.dateBtn1:
            case R.id.linearlayoutbtn1:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();

                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Sunday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
            case R.id.dateBtn2:
            case R.id.linearlayoutbtn2:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Monday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
            case R.id.dateBtn3:
            case R.id.linearlayoutbtn3:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Tuesday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
            case R.id.dateBtn4:
            case R.id.linearlayoutbtn4:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Wednesday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
            case R.id.dateBtn5:
            case R.id.linearlayoutbtn5:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Thursday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
            case R.id.dateBtn6:
            case R.id.linearlayoutbtn6:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Friday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
            case R.id.dateBtn7:
            case R.id.linearlayoutbtn7:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Saturday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
        }
    }

    public void createBarGraph()
    {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Calendar").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Fetch data from database to reload calendar items on graph
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                barEntries = new BarEntry[7];
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    float numCalendarItems = snapshot.getChildrenCount();
                    Log.d(TAG, "snapshot: " + snapshot.getKey());
                    switch (snapshot.getKey())
                    {
                        case "Sunday":
                        {
                            barEntries[0] = new BarEntry(0, numCalendarItems);
                            break;
                        }
                        case "Monday":
                        {
                            barEntries[1] = new BarEntry(1, numCalendarItems);
                            break;
                        }
                        case "Tuesday":
                        {
                            barEntries[2] = new BarEntry(2, numCalendarItems);
                            break;
                        }
                        case "Wednesday":
                        {
                            barEntries[3] = new BarEntry(3, numCalendarItems);
                            break;
                        }
                        case "Thursday":
                        {
                            barEntries[4] = new BarEntry(4, numCalendarItems);
                            break;
                        }
                        case "Friday":
                        {
                            barEntries[5] = new BarEntry(5, numCalendarItems);
                            break;
                        }
                        case "Saturday":
                        {
                            barEntries[6] = new BarEntry(6, numCalendarItems);
                            break;
                        }
                    }
                    Log.d(TAG, "numCalendarItems: " + numCalendarItems + "");
                }

                for(int j = 0; j < 7; j++)
                {
                    if (barEntries[j] == null)
                        barEntries[j] = new BarEntry(j, 0);
                }
                barEntries2 = new ArrayList<>();
                barEntries2.addAll(Arrays.asList(barEntries));

                Log.d(TAG, "barEntriesList: " + Arrays.toString(barEntries));
                BarDataSet barDataSet = new BarDataSet(barEntries2, "Number of Notes");
                BarData barData = new BarData(barDataSet);
                barData.setDrawValues(false);
                barData.setBarWidth(.5f);

                if(barChart == null)
                    Log.d(TAG, "barData is null");
                barChart.setData(barData);

                if (darkModePrefManager.loadDarkModeState())
                {
                    barDataSet.setColor(colorBarDark);
                    barChart.getXAxis().setTextColor(colorLabelDark);
                }
                else {
                    barDataSet.setColor(colorBarLight);
                    barChart.getXAxis().setTextColor(colorLabelLight);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
        switch ((int)e.getX())
        {
            case 0: // Sunday
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();

                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Sunday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
            case 1:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Monday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
            case 2:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Tuesday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
            case 3:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Wednesday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
            case 4:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Thursday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
            case 5:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Friday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
            case 6:
            {
                calendarBottomSheet bottomSheet = new calendarBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", "Saturday");

                bottomSheet.setArguments(bundle);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "calendarBottomSheet");
                break;
            }
        }
        selectedEntry = e;
        selectedHighlight = h;
        barChart.highlightValues(null);
    }

    @Override
    public void onNothingSelected() {
        onValueSelected(selectedEntry, selectedHighlight);
    }
}
