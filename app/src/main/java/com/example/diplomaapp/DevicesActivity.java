package com.example.diplomaapp;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomaapp.databinding.ActivityDevicesBinding;
import com.google.android.material.snackbar.Snackbar;

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

        connectToMqtt();

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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(this, position -> {
            Log.i("recycler", "swap");
            setSnackBar();
        }));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void createDataForRecycler() {
        Devices devices1 = new Devices("0x02145243255322B", "Temperature: 27^C\nPressure: 980 Pa\nLight: 832 Lum");
        devices.add(devices1);
        devices1 = new Devices("Led", "l2");
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

//        loadingSpinner.setVisibility(View.VISIBLE);
//        constraintLayout.setVisibility(View.GONE);

        if(isInternetConnection()) {
            mqttHelper = new MqttHelper(getApplicationContext(), "tcp://192.168.1.105:1883",
                    "Yaroslav", "26112002");
            loadingSpinner.setVisibility(View.INVISIBLE);
            constraintLayout.setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public void setSnackBar(){
        ConstraintLayout lay = findViewById(R.id.mainlayout);
        Snackbar snackbar = Snackbar.make(this, lay,"Item was removed from the list.", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("SnackBar", "Clicked");

            }
        });

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }
}