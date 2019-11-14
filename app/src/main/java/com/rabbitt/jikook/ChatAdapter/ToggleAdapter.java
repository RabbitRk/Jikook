package com.rabbitt.jikook.ChatAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rabbitt.jikook.R;

import java.util.List;

public class ToggleAdapter extends RecyclerView.Adapter<ToggleAdapter.holder> {

    private static final int SENT = 0;
    private static final int RECEIVED = 1;
    private static final int IMG_SENT = 2;
    private static final int IMG_RECEIVED = 3;

    private static final String LOG_TAG = "ToggleAdapter";
    private List<ChatMessage> dataModelArrayList;
    private Context context;
    private ToggleAdapter.OnRecycleItemListener mOnRecycleItemListener;
    private ChatMessage dataModel;
    private ImageView item_image;

    public ToggleAdapter(List<ChatMessage> productAdapter, Context context, ToggleAdapter.OnRecycleItemListener onRecycleItemListener) {
        this.dataModelArrayList = productAdapter;
        this.context = context;
        this.mOnRecycleItemListener = onRecycleItemListener;
        Log.i(LOG_TAG, dataModelArrayList.toString());
    }

    @NonNull
    @Override
    public ToggleAdapter.holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        @SuppressLint("InflateParams")
        View view = null;

        switch (i)
        {
            case SENT:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.send_bubble, null);
                break;
            case RECEIVED:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.receive_bubble, null);
                break;
            case IMG_SENT:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.send_img_bubble, null);
                break;
            case IMG_RECEIVED:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.receive_img_bubble, null);
                break;

        }
        return new ToggleAdapter.holder(view, mOnRecycleItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ToggleAdapter.holder holder, int i) {

        dataModel = dataModelArrayList.get(i);
        Log.i(LOG_TAG, "" + i);
        Log.i(LOG_TAG, dataModel.getMessage());

        //Load text

        if (dataModel.getType() == 0)
        {
            holder.item_name.setText(dataModel.getMessage());
        }
        else
        {
            Glide.with(context)
                    .load(dataModel.getMessage())
                    .into(item_image);
        }
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int i = 0;

        switch(dataModelArrayList.get(position).getIsMine())
        {
            case 0:
                i = SENT;
                break;
            case 1:
                i = RECEIVED;
                break;
            case 2:
                i = IMG_SENT;
                break;
            case 3:
                i = IMG_RECEIVED;
                break;
        }

//        int i = dataModelArrayList.get(position).getIsMine();
//
//        if (i == 0)
//        {
//            return SENT;
//        } else if(i == 1)
//        {
//            return RECEIVED;
//        }
//        else if (i == 2)
//        {
//            return IMG_SENT;
//        }
//        else if (i == 3)
//        {
//            return IMG_RECEIVED;
//        }
//        return IMG_RECEIVED;

        return i;
    }

    public interface OnRecycleItemListener {
        void OnItemClick(int position);
    }

    class holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView item_name;

        ToggleAdapter.OnRecycleItemListener onRecycleItemListener;
        holder(@NonNull View itemView, OnRecycleItemListener mOnRecycleItemListener) {
            super(itemView);
            this.onRecycleItemListener = mOnRecycleItemListener;
            item_name = itemView.findViewById(R.id.singleMessage);
            item_image = itemView.findViewById(R.id.img_chat);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecycleItemListener.OnItemClick(getAdapterPosition());
        }
    }
}