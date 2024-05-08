package com.example.diplomaapp.dataClasses;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomaapp.DevicesActivity;
import com.example.diplomaapp.R;
import java.util.ArrayList;

public class SystemAdapter extends RecyclerView.Adapter<SystemAdapter.ViewHolder> {
    private static DBHelper dbHelper;
    private ArrayList<System> mDataset;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textNameSystem, textDevicesSystem;
        public View view;

        public ViewHolder(View v) {
            super(v);
            textNameSystem = v.findViewById(R.id.textNameSystem);
            textDevicesSystem = v.findViewById(R.id.textDevicesSystem);
        }

        public void SetDetails(System system){
            textNameSystem.setText(system.getSystemName());
            ArrayList<Devices> devices = dbHelper.getAllDevices(system);
            StringBuilder devStr = new StringBuilder();
            devices.forEach((x)->devStr.append(x.getFriendlyName()).append(" "));
            if(!devStr.toString().isEmpty())
                textDevicesSystem.setText(devStr);
        }
        public View getView() {
            return view;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SystemAdapter(Context context, ArrayList<System> myDataset) {
        this.mDataset = myDataset;
        this.context = context;
        dbHelper = new DBHelper(context.getApplicationContext());
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public SystemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.system_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        System system = mDataset.get(position);
        holder.SetDetails(system);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    System clickedSystem = mDataset.get(position);

                    Intent intent = getIntent(clickedSystem);
                    Log.i("MQTT main", clickedSystem.getMqtt_url() + clickedSystem.getMqtt_login() + clickedSystem.getMqtt_password());
                    context.startActivity(intent);
                }
            }
        });
    }

    @NonNull
    private Intent getIntent(System clickedSystem) {
        Intent intent = new Intent(context, DevicesActivity.class);
        intent.putExtra("systemName", clickedSystem.getSystemName());
        intent.putExtra("mqttUrl", clickedSystem.getMqtt_url());

        if(clickedSystem.getMqtt_login() != null && clickedSystem.getMqtt_password() != null){
            intent.putExtra("mqtt_login", clickedSystem.getMqtt_login());
            intent.putExtra("mqtt_password", clickedSystem.getMqtt_password());
        }
        return intent;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

