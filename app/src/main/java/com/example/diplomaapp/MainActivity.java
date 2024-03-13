package com.example.diplomaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Systems List");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            addNewSystemInList();
//            setListItems();
            return insets;
        });
    }

    public void addNewSystemInList(){
        FloatingActionButton addSystemButton = (FloatingActionButton) findViewById(R.id.addSystem);
        addSystemButton.setOnClickListener(v -> {
            Intent intent = new Intent(".SecondActivity");
            startActivity(intent);
        });
    }

    public void setupRecylerView(){
        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.ListOfSystems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация списка данных
        List<String> dataList = new ArrayList<>();
        dataList.add("Item 1");
        dataList.add("Item 2");
        dataList.add("Item 3");

        // Инициализация адаптера
        adapter = new RecyclerAdapter(this, dataList);
        recyclerView.setAdapter(adapter);

        // Присоединение SwipeToDeleteCallback к RecyclerView
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

//    public void setListItems(){
//        listView = findViewById(R.id.ListOfSystems);
//
//        // Создаем массив с элементами и подзаголовками
//        String[] titles = {"Garage", "Title 2", "Title 3", "Title 4", "Title 5", "Title 2", "Title 3", "Title 4", "Title 5", "Title 5"};
//        String[] subtitles = {"Led, Iron, Car, Cattle", "Subtitle 2", "Subtitle 3", "Subtitle 4", "Subtitle 5", "Subtitle 1", "Subtitle 2", "Subtitle 3", "Subtitle 4", "Subtitle 5"};
//
//        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item_layout, R.id.textViewTitle, titles) {
//            @NonNull
//            @Override
//            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//
//                TextView textViewSubtitle = view.findViewById(R.id.textViewSubtitle);
//                textViewSubtitle.setText(subtitles[position]);
//
//                return view;
//            }
//        };
//
//        listView.setAdapter(adapter);
//    }

}