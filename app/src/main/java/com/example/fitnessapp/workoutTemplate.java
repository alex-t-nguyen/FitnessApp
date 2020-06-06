package com.example.fitnessapp;

import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessapp.listanimate.CustomItemAnimator;
import com.example.fitnessapp.workoutCategory.Exercise;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class workoutTemplate extends AppCompatActivity {

    private static final String TAG = "workoutTemplate: ";
    private ArrayList<Exercise> mExercises = new ArrayList<>();
    private ArrayList<String> mDescriptions = new ArrayList<>();

    private Dialog popAddExercise, popAddRest;
    private ImageView popupPostImage, popupAddBtn;

    TextView popupTitle, popupSets, popupReps, popupTime;
    ProgressBar popupClickProgress;

    private workoutTemplate_listAdapter adapter;

    // Firebase variables
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_template);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Exercise 1");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inipopupaddExercise();
        inipopupaddRest();

        Intent intent = getIntent();
        String workoutKey = intent.getStringExtra("selectedWorkoutKey");
        String workoutGroup = intent.getStringExtra("selectedWorkoutGroup");
        if (workoutKey == null)
            Log.d(TAG, "null");
        if(workoutGroup == null)
            Log.d(TAG, "null2");
        else {
            Log.d(TAG, workoutKey);
            Log.d(TAG, workoutGroup);
        }


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(workoutGroup).child(workoutKey);

        //initRecyclerView();


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        adapter = new workoutTemplate_listAdapter(mExercises, /*mDescriptions,*/getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup custom item animator
        recyclerView.setItemAnimator(new CustomItemAnimator());



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mExercises.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Exercise exercise = snapshot.getValue(Exercise.class);
                    mExercises.add(exercise);

                    Log.d(TAG, "Name: " + exercise.getName());

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listHashMap.clear();
                listHeader.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    try {
                        final String parentKey = snapshot.getKey();  // contains "Push" category
                        DatabaseReference childReference = database.getReference("Workouts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(parentKey);

                        childReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                newExpandableCategory = new ArrayList<>();
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    String childValue = snapshot1.getKey();
                                    newExpandableCategory.add(childValue);
                                }
                                // Creates item in list
                                listHeader.add(parentKey);
                                listHashMap.put(listHeader.get(listHeader.size() - 1), newExpandableCategory);
                                listAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    catch(Exception e)
                    {
                        Log.i(TAG, "Exception: " + e.toString());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */
    }

    private void initRecyclerView() {
        /*
        mExercises.add("Bench Press");
        mDescriptions.add("4" + " x " + "6-8");

        mExercises.add("Cable Fly");
        mDescriptions.add("4" + " x " + "12-15");
        */
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        adapter = new workoutTemplate_listAdapter(mExercises, /*mDescriptions,*/getApplicationContext());
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
            case R.id.add_rest:
                Toast.makeText(this, "Add Rest", Toast.LENGTH_SHORT).show();
                popAddRest.show();
            case R.id.edit:
                Toast.makeText(this, "Edit Exercises", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void inipopupaddExercise() {
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

        // Add exercise click listener
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);
                Exercise exercise = new Exercise(popupTitle.getText().toString(), popupSets.getText().toString(), popupReps.getText().toString(), "");
                addExercise(exercise);
                String exerciseKey = myRef.push().getKey();
                Log.d(TAG, "key: " + exerciseKey);
                myRef.child(exerciseKey).child("name").setValue(exercise.getName());
                myRef.child(exerciseKey).child("sets").setValue(exercise.getSets());
                myRef.child(exerciseKey).child("reps").setValue(exercise.getReps());
                myRef.child(exerciseKey).child("rest").setValue(exercise.getRest());    // Empty string

                //Log.i(TAG, popupTitle.getText().toString());

                popupClickProgress.setVisibility(View.INVISIBLE);
                popAddExercise.dismiss();
                popupAddBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addExercise(Exercise exercise) {
        mExercises.add(exercise);
        //mDescriptions.add(sets + " x " + reps);
        adapter.notifyItemInserted(mExercises.size() - 1);
    }

    private void inipopupaddRest() {
        popAddRest = new Dialog(this);
        popAddRest.setContentView(R.layout.popup_add_rest);
        popAddRest.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddRest.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddRest.getWindow().getAttributes().gravity = Gravity.TOP;

        // inipopup widgets
        popupPostImage = popAddRest.findViewById(R.id.popup_img);
        popupTime = popAddRest.findViewById(R.id.popupTime);
        //popupTime.setTransformationMethod(null);
        popupAddBtn = popAddRest.findViewById(R.id.popup_add_rest);
        popupClickProgress = popAddRest.findViewById(R.id.popup_progressBar);

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);
                Exercise exercise = new Exercise();
                exercise.setRest(popupTime.getText().toString());
                addExercise(exercise);
                String restKey = myRef.push().getKey();

                myRef.child(restKey).child("name").setValue(exercise.getName());    // Empty string
                myRef.child(restKey).child("sets").setValue(exercise.getSets());    // Empty string
                myRef.child(restKey).child("reps").setValue(exercise.getReps());    // Empty string
                myRef.child(restKey).child("rest").setValue(exercise.getRest());

                popupClickProgress.setVisibility(View.INVISIBLE);
                popAddRest.dismiss();
                popupAddBtn.setVisibility(View.VISIBLE);
            }
        });
    }
}
