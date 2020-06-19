package com.example.fitnessapp.calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapp.R;

import java.util.List;

public class calendar_list_item_Adapter extends RecyclerView.Adapter<calendar_list_item_Adapter.calendarViewHolder> {

    private Context mContext;
    private List<calendarItem> mData;
    private onCalItemListener onCalItemListener;

    public calendar_list_item_Adapter(Context mContext, List<calendarItem> mData, onCalItemListener onCalItemListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.onCalItemListener = onCalItemListener;
    }

    @NonNull
    @Override
    public calendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layout;
        layout = LayoutInflater.from(mContext).inflate(R.layout.calendar_list_item, parent, false);
        return new calendarViewHolder(layout, onCalItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull calendarViewHolder holder, int position) {

        // Animation of calendar items
        holder.itemContainer.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));

        holder.title.setText(mData.get(position).getTitle());
        holder.description.setText(mData.get(position).getDescription());
        holder.date.setText(mData.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class calendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String TAG = "calendarViewHolder";
        private TextView title, description, date;
        private onCalItemListener onCalItemListener;
        private RelativeLayout itemContainer;

        public calendarViewHolder(@NonNull View itemView, onCalItemListener onCalItemListener)
        {
            super(itemView);

            itemContainer = itemView.findViewById(R.id.calendar_container);
            title = itemView.findViewById(R.id.calendarTitle);
            description = itemView.findViewById(R.id.calendarDescription);
            date = itemView.findViewById(R.id.calendarItemDate);
            this.onCalItemListener = onCalItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: " + "calendarViewHolder onClick");
            onCalItemListener.onCalendarItemClick(getAdapterPosition());
        }
    }
    public interface onCalItemListener {
        void onCalendarItemClick(int position);
    }
}
