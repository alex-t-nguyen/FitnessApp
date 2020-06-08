package com.example.fitnessapp.menuCards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessapp.R;

import java.util.List;

public class menu_adapter extends RecyclerView.Adapter<menu_adapter.menuViewHolder> {

    Context mContext;
    List<menu_item> mData;
    private onMenuCardListener mOnMenuCardListener;

    public menu_adapter (Context mContext, List<menu_item> mData, onMenuCardListener onMenuCardListener)
    {
        this.mContext = mContext;
        this.mData = mData;
        this.mOnMenuCardListener = onMenuCardListener;
    }
    @NonNull
    @Override
    public menuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.menu_card_item, parent, false);
        return new menuViewHolder(v, mOnMenuCardListener);
    }

    @Override
    public void onBindViewHolder (@NonNull menuViewHolder holder, int position) {

        holder.backgroundImg.setImageResource(mData.get(position).getBackground());
        //holder.cardIcon.setImageResource(mData.get(position).getCard_icon());
        holder.title1.setText(mData.get(position).getTitle1());
        holder.title2.setText(mData.get(position).getTitle2());
        holder.mainTitle.setText(mData.get(position).getMainTitle());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class menuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //ImageView cardIcon;
        ImageView backgroundImg;
        TextView title1, title2, mainTitle;
        onMenuCardListener onMenuCardListener;

        public menuViewHolder(@NonNull View itemView, onMenuCardListener onMenuCardListener) {
            super(itemView);
            //cardIcon = itemView.findViewById(R.id.card_icon_img);
            backgroundImg = itemView.findViewById(R.id.card_background);
            title1 = itemView.findViewById(R.id.card_title1);
            title2 = itemView.findViewById(R.id.card_title2);
            mainTitle = itemView.findViewById(R.id.card_main_title);
            this.onMenuCardListener = onMenuCardListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMenuCardListener.onMenuCardClick(getAdapterPosition());
        }
    }

    public interface onMenuCardListener {
        void onMenuCardClick(int position);
    }
}
