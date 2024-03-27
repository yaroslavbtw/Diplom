package com.example.diplomaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private DBHelper dbHelper;
    private List<System> systems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Systems List");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainlayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            setListeners();
            setListItems();
            return insets;
        });
    }

    public void setListeners(){
        FloatingActionButton addSystemButton = (FloatingActionButton) findViewById(R.id.addSystem);
        addSystemButton.setOnClickListener(v -> {
            Intent intent = new Intent(".SecondActivity");
            startActivity(intent);
        });
    }

    public void setListItems(){
        listView = findViewById(R.id.ListOfSystems);

        dbHelper = new DBHelper(this);

        systems = dbHelper.getAllSystems();
        List<String> systemNames = new ArrayList<>();

        for (System system : systems) {
            systemNames.add(system.getSystemName());
        }
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item_layout, R.id.textViewTitle, systemNames) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textViewSubtitle = view.findViewById(R.id.textViewSubtitle);
                textViewSubtitle.setText(systems.get(position).getMqtt_url());

                return view;
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            System selectedSystem = systems.get(position);

            String systemName = selectedSystem.getSystemName();
            String mqttUrl = selectedSystem.getMqtt_url();

            Intent intent = new Intent(".DevicesActivity");
            intent.putExtra("systemName", systemName);
            intent.putExtra("mqttUrl", mqttUrl);
            Log.i("Hello!", "Name: " + systemName + " , url: " + mqttUrl);
            startActivity(intent);
        });
    }

}