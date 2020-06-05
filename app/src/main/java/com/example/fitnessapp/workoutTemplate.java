package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessapp.listanimate.CustomItemAnimator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class workoutTemplate extends AppCompatActivity {

    private static final String test = "test";
    private ArrayList<String> mExercises = new ArrayList<>();
    private ArrayList<String> mDescriptions = new ArrayList<>();

    private Dialog popAddExercise;
    private ImageView popupPostImage, popupAddBtn;

    TextView popupTitle, popupSets, popupReps;
    ProgressBar popupClickProgress;

    private workoutTemplate_listAdapter adapter;

    // Firebase variables
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_template);
        initRecyclerView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Exercise 1");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inipopup();

        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        Log.i(test, key);
    }

    private void initRecyclerView() {

        mExercises.add("Bench Press");
        mDescriptions.add("4" + " x " + "6-8");

        mExercises.add("Cable Fly");
        mDescriptions.add("4" + " x " + "12-15");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        adapter = new workoutTemplate_listAdapter(mExercises, mDescriptions, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup custom item animator
        recyclerView.setItemAnimator(new CustomItemAnimator());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.add_exercise:
                Toast.makeText(this, "Add Exercise", Toast.LENGTH_SHORT).show();
                popAddExercise.show();
                break;
            case R.id.edit:
                Toast.makeText(this, "Edit Exercises", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void inipopup() {
        popAddExercise = new Dialog(this);
        popAddExercise.setContentView(R.layout.popup_add_exercise);
        popAddExercise.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddExercise.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddExercise.getWindow().getAttributes().gravity = Gravity.TOP;

        // inipopup widgets
        popupPostImage = popAddExercise.findViewById(R.id.popup_img);
        popupTitle = popAddExercise.findViewById(R.id.popupTitle);
        popupSets = popAddExercise.findViewById(R.id.popupSets);
        popupReps = popAddExercise.findViewById(R.id.popupReps);
        popupAddBtn = popAddExercise.findViewById(R.id.popup_add);
        popupClickProgress = popAddExercise.findViewById(R.id.popup_progressBar);

        database = FirebaseDatabase.getInstance();
        // myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child()

        // Add exercise click listener
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);
                addExercise(popupTitle.getText().toString(), popupSets.getText().toString(), popupReps.getText().toString());

                popAddExercise.dismiss();
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addExercise(String name, String sets, String reps) {
        mExercises.add(name);
        mDescriptions.add(sets + " x " + reps);
        adapter.notifyItemInserted(mExercises.size() - 1);
    }


}
