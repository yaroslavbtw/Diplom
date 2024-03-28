package com.example.diplomaapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Devices> mDataset;
    private Context context;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textNameCard, textDataCard;
        public Switch switchButton;
        public View view;

        public ViewHolder(View v) {
            super(v);
            textNameCard = v.findViewById(R.id.textNameCard);
            textDataCard = v.findViewById(R.id.textDataCard);
            switchButton = v.findViewById(R.id.switchBtn);
        }

        public void SetDetails(Devices device){
            textNameCard.setText(device.getDeviceId());
            textDataCard.setText(device.getChannel());
            switchButton.setChecked(true);
        }

        public View getView() {
            return view;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, ArrayList<Devices> myDataset) {
        this.mDataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Devices device = mDataset.get(position);
        holder.SetDetails(device);

        holder.switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Log.i("button", "On " + holder.getAdapterPosition());
            } else {
                Log.i("button", "Off " + holder.getAdapterPosition());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
