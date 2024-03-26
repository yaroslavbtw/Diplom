package com.example.diplomaapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "iotDb";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS systems " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, system_name TEXT NOT NULL, " +
                "mqtt_url TEXT NOT NULL, mqtt_login TEXT, mqtt_password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Метод вызывается при обновлении версии базы данных
        // Можно провести миграцию данных при необходимости
    }

    public void addSystem(String name, String mqtt_url) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("system_name", name);
        values.put("mqtt_url", mqtt_url);
        db.insert("systems", null, values);
        db.close();
    }

    public List<System> getAllSystems() {
        List<System> systems = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT system_name, mqtt_url FROM systems", null);
        if (cursor.moveToFirst()) {
            do {
                String systemName = cursor.getString(0);
                String mqtt_url = cursor.getString(1);
                systems.add(new System(systemName, mqtt_url));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return systems;
    }

    public void deleteAllSystems() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("systems", null, null);
        db.close();
    }

}
