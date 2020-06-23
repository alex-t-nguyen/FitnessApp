package com.rest.fitnessapp.workoutCategory;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.rest.fitnessapp.R;

import java.util.ArrayList;

public class workoutTemplate_listAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private ItemTouchHelper itemTouchHelper;

    private ArrayList<Exercise> mExercises;
    // ArrayList<String> mDescriptions = new ArrayList<>();
    private Context mContext;

    private static int TYPE_NAME = 1;
    private static int TYPE_REST = 2;


    public workoutTemplate_listAdapter(ArrayList<Exercise> mExercises, /*ArrayList<String> mDescriptions,*/ Context mContext) {
        this.mExercises = mExercises;
        //this.mDescriptions = mDescriptions;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_NAME) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_template_list_item, parent, false);
            return new viewHolderName(view);
        }
        else // TYPE_REST
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_template_list_item_rest, parent, false);
            return new viewHolderRest(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if(getItemViewType(position) == TYPE_NAME)
        {
            ((viewHolderName)holder).setNameDetails(mExercises.get(position));
            ((viewHolderName)holder).parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, mExercises.get(position).getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
            ((viewHolderRest)holder).setRestDetails(mExercises.get(position));
    }

    public void setList (ArrayList<Exercise> exerciseList)
    {
        this.mExercises = exerciseList;
        notifyDataSetChanged();
    }

    /*
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.exercise.setText(mExercises.get(position).getName());
        String description = mExercises.get(position).getSets() + " x " + mExercises.get(position).getReps();
        Log.d(TAG, "description: " + mExercises.get(position).getSets() + " sets.");
        holder.description.setText(description);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, mExercises.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    */

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(TextUtils.isEmpty(mExercises.get(position).getRest()))
            return TYPE_NAME;
        else
            return TYPE_REST;
    }

    public class viewHolderName extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView exercise;
        TextView description;
        RelativeLayout parentLayout;

        public viewHolderName(View itemView) {
            super(itemView);
            exercise = itemView.findViewById(R.id.exercise_name);
            description = itemView.findViewById(R.id.description);
            parentLayout = itemView.findViewById(R.id.list_item_layout);


            itemView.setOnClickListener(this);
        }
        private void setNameDetails(Exercise e)
        {
            exercise.setText(e.getName());
            String desc = e.getReps() + " x " + e.getWeight() + "lb";
            description.setText(desc);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class viewHolderRest extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView exercise;
        TextView description;
        RelativeLayout parentLayout;

        public viewHolderRest(View itemView) {
            super(itemView);
            exercise = itemView.findViewById(R.id.rest_title);
            description = itemView.findViewById(R.id.description_rest);
            parentLayout = itemView.findViewById(R.id.list_item_layout);

            itemView.setOnClickListener(this);
        }
        private void setRestDetails(Exercise e)
        {
            exercise.setText("REST");
            int restTime = Integer.parseInt(e.getRest());
            int seconds = restTime;
            int minutes = 0;
            String timeFormatted;
            if(restTime > 60) {
                seconds = restTime % 60;
                minutes = restTime / 60;
            }
            if (minutes < 10 && seconds < 10)   // If min and sec are both single digit
                timeFormatted = "0" + minutes + ":" + "0" + seconds;
            else if (minutes < 10 && seconds > 10)  // If only min is single digit
                timeFormatted = "0" + minutes + ":" + seconds;
            else if (minutes > 10 && seconds < 10)  // If only sec is single digit
                timeFormatted = minutes + ":" + "0" + seconds;
            else    // If neither min or sec are single digit
                timeFormatted = minutes + ":" + seconds;
            description.setText(timeFormatted);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
