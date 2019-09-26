package com.rabbitt.jikook.ChatAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.jikook.R;

import java.util.List;

public class ToggleAdapter extends RecyclerView.Adapter<ToggleAdapter.holder> {

    private static final int SENT = 0;
    private static final int RECEIVED = 1;

    private static final String LOG_TAG = "ToggleAdapter";
    private List<ChatMessage> dataModelArrayList;
    private Context context;
    private ToggleAdapter.OnRecycleItemListener mOnRecycleItemListener;
    private ChatMessage dataModel;

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
        View view;
        if (i == SENT) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.send_bubble, null);
            view.setForegroundGravity(Gravity.END);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.receive_bubble, null);
        }
        return new ToggleAdapter.holder(view, mOnRecycleItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ToggleAdapter.holder holder, int i) {
        dataModel = dataModelArrayList.get(i);
        Log.i(LOG_TAG, "" + i);
        Log.i(LOG_TAG, dataModel.getMessage());

        //Load text
        holder.item_name.setText(dataModel.getMessage());
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataModelArrayList.get(position).getIsMine() == 0) {
            return SENT;
        } else {
            return RECEIVED;
        }
    }

    public interface OnRecycleItemListener {
        void OnItemClick(int position);
    }

    class holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView item_name;//, item_id;
        ToggleAdapter.OnRecycleItemListener onRecycleItemListener;
        holder(@NonNull View itemView, OnRecycleItemListener mOnRecycleItemListener) {
            super(itemView);
            this.onRecycleItemListener = mOnRecycleItemListener;
            item_name = itemView.findViewById(R.id.singleMessage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecycleItemListener.OnItemClick(getAdapterPosition());
        }
    }
}