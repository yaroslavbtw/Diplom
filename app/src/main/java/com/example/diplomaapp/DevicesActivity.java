package com.example.diplomaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomaapp.dataClasses.DBHelper;
import com.example.diplomaapp.dataClasses.DeleteConfirmationDialog;
import com.example.diplomaapp.dataClasses.Devices;
import com.example.diplomaapp.dataClasses.MqttHelper;
import com.example.diplomaapp.dataClasses.MyAdapter;
import com.example.diplomaapp.dataClasses.System;
import com.example.diplomaapp.databinding.ActivityDevicesBinding;
import com.example.diplomaapp.listeners.MqttConnectListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DevicesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyAdapter adapter;
    private ArrayList<Devices> devices;
    private ActivityDevicesBinding binding;
    private MqttHelper mqttHelper;
    private SwipeToDeleteCallback callback;
    private System system;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_devices);

        Intent intent = getIntent();

        String systemName = intent.getStringExtra("systemName");
        String mqttUrl = intent.getStringExtra("mqttUrl");

        system = new System(systemName, mqttUrl);

        connectToMqtt();

        setListeners();

        ImageButton imageButton = findViewById(R.id.imageButton2);
        imageButton.setBackground(null);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDevicesList();
                Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainlayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initCardView(MqttHelper mqttHelper) {
        setDevicesList();
        callback = new SwipeToDeleteCallback(this, position -> {

            DeleteConfirmationDialog.show(this, "Are you sure you want to remove this item?", () -> {
                Devices dev = devices.get(position);
                dbHelper.deleteDevice(dev);
                devices.remove(position);
                adapter.notifyDataSetChanged();
            }, () -> {
                adapter.notifyDataSetChanged();
            });
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateDataForRecycler() {
        TextView textViewNoDevices = findViewById(R.id.textViewNoDevices);
        if(!devices.isEmpty())
            textViewNoDevices.setVisibility(View.GONE);
        else
            textViewNoDevices.setVisibility(View.VISIBLE);

        adapter.notifyDataSetChanged();
    }

    private boolean isInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            for (Network net : connectivityManager.getAllNetworks()) {
                NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(net);
                if (nc != null && nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
                    return true;
            }
        }
        return false;
    }

    private void connectToMqtt(){
        ProgressBar loadingSpinner = findViewById(R.id.progressBar);
        ConstraintLayout constraintLayout = findViewById(R.id.conLayout);
        FloatingActionButton addDevice = findViewById(R.id.addDevice);
        TextView textViewNoDevices = findViewById(R.id.textViewNoDevices);

        textViewNoDevices.setVisibility(View.GONE);
        loadingSpinner.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.GONE);
        addDevice.setVisibility(View.INVISIBLE);

        if(isInternetConnection()) {
            try {

            mqttHelper = new MqttHelper(getApplicationContext(), "tcp://192.168.1.108:1883",
                    "Yaroslav", "26112002", new MqttConnectListener() {
                @Override
                public void onSuccess() {

                    loadingSpinner.setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    addDevice.setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(), "Mqtt connection", Toast.LENGTH_LONG).show();
                    initCardView(mqttHelper);
                }

                @Override
                public void onFailure(Throwable exception) {
                    Toast.makeText(getApplicationContext(), "No mqtt connection", Toast.LENGTH_LONG).show();
                }
            });
            } catch (Exception e) {
                Log.i("mqtt", e.getMessage());
            }
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public void setListeners(){
        FloatingActionButton addDeviceButton = findViewById(R.id.addDevice);
        addDeviceButton.setOnClickListener(v -> {
            Intent intent = new Intent(".AddDevice");
            intent.putExtra("systemName", system.getSystemName());
            intent.putExtra("mqttUrl", system.getMqtt_url());
            startActivity(intent);
        });
    }

    private void setDevicesList(){
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager((this)));

        dbHelper = new DBHelper(this);
        devices = dbHelper.getAllDevices(system);
        adapter = new MyAdapter(this, devices, mqttHelper);
        mRecyclerView.setAdapter(adapter);
        updateDataForRecycler();
    }
}
