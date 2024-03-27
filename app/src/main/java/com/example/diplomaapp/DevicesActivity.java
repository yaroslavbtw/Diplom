package com.example.diplomaapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomaapp.databinding.ActivityDevicesBinding;

import java.util.ArrayList;

public class DevicesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyAdapter adapter;
    private ArrayList<Devices> devices;
    private ActivityDevicesBinding binding;
    private MqttHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_devices);

//        connectToMqtt();

        initCardView();
        setListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainlayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setListeners(){
        Switch mySwitch = findViewById(R.id.switchBtn);
//        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                mySwitch.setText("On");
//            } else {
//                mySwitch.setText("Off");
//            }
//        });
    }

    private void initCardView() {
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager((this)));
        devices = new ArrayList<>();
        adapter = new MyAdapter(this, devices);
        mRecyclerView.setAdapter(adapter);
        createDataForRecycler();
    }

    private void createDataForRecycler() {
        Devices devices1 = new Devices("Led1", "l1");
        devices.add(devices1);
        devices1 = new Devices("Led2", "l2");
        devices.add(devices1);
        devices1 = new Devices("Led3", "l3");
        devices.add(devices1);
        devices1 = new Devices("Led4", "l4");
        devices.add(devices1);
        devices1 = new Devices("Led5", "l5");
        devices.add(devices1);
        devices1 = new Devices("Led6", "l6");
        devices.add(devices1);
        devices1 = new Devices("Led7", "l7");
        devices.add(devices1);
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

        if(isInternetConnection()) {

            mqttHelper = new MqttHelper(getApplicationContext(), "mqtt://172.20.10.10:1883",
                    "Yaroslav", "26112002");
            if(mqttHelper.isConnected()){

                loadingSpinner.setVisibility(View.INVISIBLE);
                constraintLayout.setAlpha(0f);

                Toast.makeText(getApplicationContext(), "mqtt +", Toast.LENGTH_LONG).show();
//                mqttHelper.subscribeToTopic("zigbee2mqtt/0x00124B00281A9824/l1/set");
//                mqttHelper.publishMessage("172.20.10.10/check", "Off");
            }
            else Toast.makeText(getApplicationContext(), "mqtt -", Toast.LENGTH_LONG).show();
        }else {
            loadingSpinner.setVisibility(View.VISIBLE);
            constraintLayout.setAlpha(0.4f);
            Toast.makeText(getApplicationContext(), "wifi -", Toast.LENGTH_LONG).show();
        }
    }
}