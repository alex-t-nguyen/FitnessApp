package com.rest.fitnessapp.listanimate;

import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.rest.fitnessapp.R;


public class CustomItemAnimator extends DefaultItemAnimator {
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return super.animateRemove(holder);
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {

        // This method is called when a new item will be added to the list
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.viewholder_anim
        ));
        return super.animateAdd(holder);
    }
}
