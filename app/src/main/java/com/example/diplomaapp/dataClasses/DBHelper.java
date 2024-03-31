package com.example.diplomaapp.dataClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        db.execSQL("CREATE TABLE IF NOT EXISTS devices " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, device_id TEXT NOT NULL UNIQUE, " +
                "device_type TEXT NOT NULL, last_data TEXT, " +
                "system_id INTEGER NOT NULL, " +
                "img_path TEXT," +
                "FOREIGN KEY (system_id) REFERENCES systems(_id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Метод вызывается при обновлении версии базы данных
        // Можно провести миграцию данных при необходимости
    }

    public String getSystemId(System system){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id FROM systems WHERE system_name = ? and mqtt_url = ?",
                new String[]{system.getSystemName(), system.getMqtt_url()});
        String systemId = "";
        if (cursor.moveToFirst()) {
            systemId = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return systemId;
    }

    public void addSystem(System system) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("system_name", system.getSystemName());
        values.put("mqtt_url", system.getMqtt_url());
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

    public void addDevice(Devices device, System system){
        String id = getSystemId(system);
        SQLiteDatabase db = getWritableDatabase();
        String img = device.getImgPath();
        ContentValues values = new ContentValues();
        values.put("device_id", device.getDeviceId());
        values.put("device_type", device.getType());
        values.put("system_id", id);
        if(img != null)
            values.put("img_path", img);
        db.insert("devices", null, values);
        db.close();
    }

    public ArrayList<Devices> getAllDevices(System system) {
        String sys_id = getSystemId(system);
        ArrayList<Devices> devicesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT devices.* FROM devices " +
                "INNER JOIN systems ON devices.system_id = systems._id " +
                "WHERE systems._id = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{sys_id});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Получение данных о устройстве из курсора и добавление в список
                int deviceIdIndex = cursor.getColumnIndex("device_id");
                int deviceTypeIndex = cursor.getColumnIndex("device_type");
                int deviceImgIndex = cursor.getColumnIndex("img_path");

                String deviceId = cursor.getString(deviceIdIndex);
                String deviceType = cursor.getString(deviceTypeIndex);
                String deviceImg = cursor.getString(deviceImgIndex);
                Log.i("sql get all devices", "device id: " + deviceId +
                        ", type: " + deviceType + ", img path: " + deviceImg);
                Devices device = new Devices(deviceId, deviceType, deviceImg);
                devicesList.add(device);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return devicesList;
    }

    public void deleteSystem(System system) {
        String sysId = getSystemId(system);
        SQLiteDatabase db = getWritableDatabase();

        // Удаление всех устройств, принадлежащих данной системе
        db.delete("devices", "system_id = ?", new String[]{sysId});

        // Удаление самой системы
        db.delete("systems", "_id = ?", new String[]{sysId});

        db.close();
    }

    public void deleteDevice(Devices device) {
        String devId = getDeviceId(device);
        SQLiteDatabase db = getWritableDatabase();
        db.delete("devices", "_id = ?", new String[]{devId});
        db.close();
    }

    public String getDeviceId(Devices device) {
        SQLiteDatabase db = getReadableDatabase();
        Log.i("sql", device.getDeviceId());
        Cursor cursor = db.rawQuery("SELECT _id FROM devices WHERE device_id = ?",
                new String[]{device.getDeviceId()});
        String deviceId = "";
        if (cursor.moveToFirst()) {
            deviceId = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return deviceId;
    }

}
