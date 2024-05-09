package com.example.diplomaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diplomaapp.dataClasses.DBHelper;
import com.example.diplomaapp.dataClasses.DeleteConfirmationDialog;
import com.example.diplomaapp.dataClasses.DeviceAdapter;
import com.example.diplomaapp.dataClasses.Devices;
import com.example.diplomaapp.dataClasses.NotificationHelper;
import com.example.diplomaapp.dataClasses.Storage;
import com.example.diplomaapp.dataClasses.SwipeToDeleteCallback;
import com.example.diplomaapp.dataClasses.System;
import com.example.diplomaapp.dataClasses.SystemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeToDeleteCallback.OnSwipeLeftListener, SwipeToDeleteCallback.OnSwipeRightListener {
    private DBHelper dbHelper;
    private RecyclerView mRecyclerView;
    private SystemAdapter adapter;
    private List<System> systems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Storage.device = null;
        dbHelper = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainlayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            setListeners();
            setListItems();

            ItemTouchHelper itemTouchHelper = getItemTouchHelper();
            itemTouchHelper.attachToRecyclerView(mRecyclerView);

            return insets;
        });
    }

    @NonNull
    private ItemTouchHelper getItemTouchHelper() {
        SwipeToDeleteCallback callback = new SwipeToDeleteCallback(this, this, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        return itemTouchHelper;
    }

    public void setListeners(){
        ImageButton refreshButton = findViewById(R.id.buttonRefreshSystems);
        refreshButton.setBackground(null);

        refreshButton.setOnClickListener(v -> {
            setListItems();
            Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();
        });

        FloatingActionButton addSystemButton = findViewById(R.id.addSystem);
        addSystemButton.setOnClickListener(v -> {
            Storage.device = null;
            Intent intent = new Intent(".SecondActivity");
            startActivity(intent);
            setListItems();
        });
    }

    public void setListItems(){
        mRecyclerView = findViewById(R.id.recyclerSystem);
        mRecyclerView.setLayoutManager(new LinearLayoutManager((this)));
        systems = dbHelper.getAllSystems();
        adapter = new SystemAdapter(this, new ArrayList<>(systems));
        mRecyclerView.setAdapter(adapter);
        updateDataForRecycler();

//        listView.setOnItemClickListener((parent, view, position, id) -> {
//            System selectedSystem = systems.get(position);
//
//            Intent intent = new Intent(".DevicesActivity");
//            intent.putExtra("systemName", selectedSystem.getSystemName());
//            intent.putExtra("mqttUrl", selectedSystem.getMqtt_url());
//            if(selectedSystem.getMqtt_login() != null && selectedSystem.getMqtt_password() != null){
//                intent.putExtra("mqtt_login", selectedSystem.getMqtt_login());
//                intent.putExtra("mqtt_password", selectedSystem.getMqtt_password());
//            }
//            Log.i("MQTT main", selectedSystem.getMqtt_url() + selectedSystem.getMqtt_login() + selectedSystem.getMqtt_password());
//            startActivity(intent);
//            setListItems();
//        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateDataForRecycler() {
        TextView textViewNoSystems = findViewById(R.id.textViewNoSystems);

        if (!systems.isEmpty())
            textViewNoSystems.setVisibility(View.GONE);
        else textViewNoSystems.setVisibility(View.VISIBLE);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSwipeLeft(int position) {
        DeleteConfirmationDialog.show(this, "Are you sure you want to remove this item?", () -> {
            System sys = systems.get(position);
            dbHelper.deleteSystem(sys);
            systems.remove(position);
            setListItems();
        }, this::setListItems);
        Toast.makeText(getApplicationContext(), "System deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSwipeRight(int position) {
        Intent intent = getIntentChangeSystem(systems.get(position));
        startActivity(intent);
        setListItems();
    }

    @NonNull
    private Intent getIntentChangeSystem(System clickedSystem) {
        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
        Storage.system = clickedSystem;
        return intent;
    }
}