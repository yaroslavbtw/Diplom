package com.example.diplomaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
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
import com.example.diplomaapp.dataClasses.MyAdapter;
import com.example.diplomaapp.dataClasses.System;
import com.example.diplomaapp.databinding.ActivityDevicesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class DevicesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyAdapter adapter;
    private ArrayList<Devices> devices;
    private ActivityDevicesBinding binding;
    private SwipeToDeleteCallback callback;
    private System system;
    private DBHelper dbHelper;
    private MqttAndroidClient mqttAndroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_devices);
        dbHelper = new DBHelper(this);
        Intent intent = getIntent();

        system = new System(intent.getStringExtra("systemName"), intent.getStringExtra("mqttUrl"));

        if (intent.hasExtra("mqtt_login") && intent.hasExtra("mqtt_password")) {
            system.setMqtt_login(intent.getStringExtra("mqtt_login"));
            system.setMqtt_password(intent.getStringExtra("mqtt_password"));
        }

        connectToMqtt();

        setListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainlayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initCardView() {
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
                if (nc != null && ((nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) // Добавляем проверку на мобильный интернет
                        && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)))
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
                String clientId = MqttClient.generateClientId();
                mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), system.getMqtt_url(), clientId);

                mqttAndroidClient.setCallback(new MqttCallback() {

                    @Override
                    public void connectionLost(Throwable cause) {}

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        String[] parts = topic.split("/");
                        String deviceId = parts[parts.length - 1];
                        Devices dev = new Devices(deviceId, "smth", "");
                        dbHelper.updateLastDataForDevice(dev, parseMqttMessage(message.toString()));
                        setDevicesList();
                        Log.i("MQTT", deviceId + " " + message.toString());
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });

                MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                mqttConnectOptions.setAutomaticReconnect(true);
                mqttConnectOptions.setCleanSession(false);

                if(system.getMqtt_login() != null && system.getMqtt_password() != null)
                {
                    mqttConnectOptions.setUserName(system.getMqtt_login());
                    mqttConnectOptions.setPassword(system.getMqtt_password().toCharArray());
                }

                try {
                    mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            loadingSpinner.setVisibility(View.INVISIBLE);
                            constraintLayout.setVisibility(View.VISIBLE);
                            addDevice.setVisibility(View.VISIBLE);

                            Toast.makeText(getApplicationContext(), "Mqtt connection", Toast.LENGTH_LONG).show();
                            initCardView();

                            for (Devices device : devices) {
                                String prefixMqtt = device.getMqttPrefix();
                                String deviceId = device.getDeviceId();

                                subscribeToTopic(prefixMqtt + "/" + deviceId);
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Toast.makeText(getApplicationContext(), "Failed to connect: " + exception.toString(), Toast.LENGTH_LONG).show();

                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.i("MQTT", ex.toString());
                }

            } catch (Exception e) {
                Log.i("mqtt", e.getMessage());
            }
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public void setListeners(){
        ImageButton refreshButton = findViewById(R.id.buttonRefreshDevices);
        refreshButton.setBackground(null);

        ImageButton deleteSystemButton = findViewById(R.id.buttonSystemDelete);
        deleteSystemButton.setBackground(null);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDevicesList();
                for (Devices device : devices) {
                    String prefixMqtt = device.getMqttPrefix();
                    String deviceId = device.getDeviceId();

                    subscribeToTopic(prefixMqtt + "/" + deviceId);
                }
                Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        deleteSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("mqtt", "delete system");
                dbHelper.deleteSystem(system);
                finish();
            }
        });

        FloatingActionButton addDeviceButton = findViewById(R.id.addDevice);
        addDeviceButton.setOnClickListener(v -> {
            Intent intent = new Intent(".AddDevice").setClassName(getPackageName(), "com.example.diplomaapp.AddDevice");
            intent.putExtra("systemName", system.getSystemName());
            intent.putExtra("mqttUrl", system.getMqtt_url());
            startActivity(intent);
        });
    }

    private void setDevicesList(){
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager((this)));
//        dbHelper = new DBHelper(this);
        devices = dbHelper.getAllDevices(system);
        adapter = new MyAdapter(this, devices, mqttAndroidClient);
        mRecyclerView.setAdapter(adapter);
        updateDataForRecycler();
    }

    public void subscribeToTopic(String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("MQTT", "CONNECTED TO TOPIC");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i("MQTT", "NOT CONNECTED TO TOPIC");
                }
            });
        } catch (Exception ex) {
            Log.i("MQTT", ex.toString());
            ex.printStackTrace();
        }
    }

    public void publishMessage(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());
            mqttAndroidClient.publish(topic, mqttMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String parseMqttMessage(String jsonMessage){
        StringBuilder formattedMessage = new StringBuilder();
        try {
            JSONObject jsonObject = new JSONObject(jsonMessage);

            // Получаем все ключи объекта
            Iterator<String> keys = jsonObject.keys();


            // Проходим по каждому ключу и добавляем его и соответствующее значение в форматированное сообщение
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.getString(key);

                formattedMessage.append(key).append(": ").append(value).append("\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return formattedMessage.toString();
    }

    @Override
    protected void onDestroy() {
        try {
            mqttAndroidClient.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onDestroy();
    }
}
