package com.example.fitnessapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.facebook.appevents.suggestedevents.ViewOnClickListener;

import java.util.ArrayList;

public class workoutTemplate_listAdapter extends Adapter<workoutTemplate_listAdapter.viewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mExercises = new ArrayList<>();
    private Context mContext;


    public workoutTemplate_listAdapter(ArrayList<String> mExercises, Context mContext) {
        this.mExercises = mExercises;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_template_list_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        holder.exercise.setText(mExercises.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, mExercises.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView exercise;
        RelativeLayout parentLayout;

        public viewHolder(View itemView) {
            super(itemView);
            exercise = itemView.findViewById(R.id.exercise_name);
            parentLayout = itemView.findViewById(R.id.list_item_layout);
        }
    }
}
