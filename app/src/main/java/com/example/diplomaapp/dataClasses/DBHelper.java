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
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, friendly_name TEXT, " +
                "device_id TEXT NOT NULL UNIQUE, " +
                "device_type TEXT NOT NULL, last_data TEXT, " +
                "system_id INTEGER NOT NULL, " +
                "img_path TEXT," +
                "mqtt_prefix Text," +
                "diode_channel Text," +
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
        if(system.getMqtt_login() != null && system.getMqtt_password() != null){
            values.put("mqtt_login", system.getMqtt_login());
            values.put("mqtt_password", system.getMqtt_password());
            Log.i("MQTT addSystem DB", system.getMqtt_url() + " " + system.getMqtt_login() + " " + system.getMqtt_password());
        }
        db.insert("systems", null, values);
        db.close();
    }

    public void updateSystem(System newSystem, System oldSystem) {
        String id = getSystemId(oldSystem);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mqtt_url", newSystem.getMqtt_url());
        values.put("system_name", newSystem.getSystemName());
        if (newSystem.getMqtt_login() != null && newSystem.getMqtt_password() != null) {
            values.put("mqtt_login", newSystem.getMqtt_login());
            values.put("mqtt_password", newSystem.getMqtt_password());
        }
        db.update("systems", values, "_id = ?", new String[]{id});
        db.close();
    }

    public List<System> getAllSystems() {
        List<System> systems = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT system_name, mqtt_url, mqtt_login, mqtt_password FROM systems", null);
        if (cursor.moveToFirst()) {
            do {
                String systemName = cursor.getString(0);
                String mqtt_url = cursor.getString(1);
                System sys = new System(systemName, mqtt_url);
                sys.setMqtt_login(cursor.getString(2));
                sys.setMqtt_password(cursor.getString(3));
                systems.add(sys);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return systems;
    }

    public System getSystem(String systemName) {
        SQLiteDatabase db = getReadableDatabase();
        System system = null;
        Cursor cursor = db.rawQuery("SELECT mqtt_url, mqtt_login, mqtt_password FROM systems WHERE system_name = ?", new String[]{systemName});
        if (cursor.moveToFirst()) {
            String mqtt_url = cursor.getString(0);
            system = new System(systemName, mqtt_url);
            system.setMqtt_login(cursor.getString(1));
            system.setMqtt_password(cursor.getString(2));
        }
        cursor.close();
        db.close();
        return system;
    }

    public void deleteAllSystems() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("systems", null, null);
        db.close();
    }

    public void addDevice(Devices device, System system){
        Log.i("sql", device.getDiodeChannel());
        String id = getSystemId(system);
        SQLiteDatabase db = getWritableDatabase();
        String img = device.getImgPath();
        String friendlyName = device.getFriendlyName();
        ContentValues values = new ContentValues();
        values.put("device_id", device.getDeviceId());
        values.put("device_type", device.getType());
        values.put("system_id", id);
        values.put("mqtt_prefix", device.getMqttPrefix());
        if(!img.isEmpty())
            values.put("img_path", img);
        if(!friendlyName.isEmpty())
            values.put("friendly_name", friendlyName);
        if(!device.getDiodeChannel().isEmpty())
            values.put("diode_channel", device.getDiodeChannel());
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
                int deviceFriendlyNameIndex = cursor.getColumnIndex("friendly_name");
                int deviceLastDataIndex = cursor.getColumnIndex("last_data");
                int deviceMqttPrefixIndex = cursor.getColumnIndex("mqtt_prefix");
                int deviceDiodeChannelIndex = cursor.getColumnIndex("diode_channel");

                String deviceId = cursor.getString(deviceIdIndex);
                String deviceType = cursor.getString(deviceTypeIndex);
                String deviceImg = cursor.getString(deviceImgIndex);
                String deviceFriendlyName = cursor.getString(deviceFriendlyNameIndex);
                String deviceLastData = cursor.getString(deviceLastDataIndex);
                String deviceMqttPrefix = cursor.getString(deviceMqttPrefixIndex);
                String deviceDiodeChannel = cursor.getString(deviceDiodeChannelIndex);

                Log.i("sql get all devices", "device id: " + deviceId +
                        ", type: " + deviceType + ", img path: " + deviceImg + ", friendlyName: " +
                        deviceFriendlyName + ", friendlyName: " + deviceMqttPrefix +
                        ", diode channel: " + deviceDiodeChannel);

                Devices device = new Devices(deviceId, deviceType, deviceImg);
                device.setFriendlyName(deviceFriendlyName);
                device.setLastAcceptedData(deviceLastData);
                device.setMqttPrefix(deviceMqttPrefix);
                device.setDiodeChannel(deviceDiodeChannel);
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
        db.delete("devices", "system_id = ?", new String[]{sysId});
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

    public int getIdByDeviceId(String deviceId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id FROM devices WHERE device_id = ?", new String[]{deviceId});
        String id = "";
        if (cursor.moveToFirst()) {
            id = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return Integer.parseInt(id);
    }

    public Devices getDeviceByDeviceId(String deviceId) {
        SQLiteDatabase db = getReadableDatabase();
        Devices device = null;
        Cursor cursor = db.rawQuery("SELECT * FROM devices WHERE device_id = ?", new String[]{deviceId});
        if (cursor.moveToFirst()) {
            // Получение данных о устройстве из курсора
            int deviceTypeIndex = cursor.getColumnIndex("device_type");
            int deviceImgIndex = cursor.getColumnIndex("img_path");
            int deviceFriendlyNameIndex = cursor.getColumnIndex("friendly_name");
            int deviceLastDataIndex = cursor.getColumnIndex("last_data");
            int deviceMqttPrefixIndex = cursor.getColumnIndex("mqtt_prefix");
            int deviceDiodeChannelIndex = cursor.getColumnIndex("diode_channel");

            String deviceType = cursor.getString(deviceTypeIndex);
            String deviceImg = cursor.getString(deviceImgIndex);
            String deviceFriendlyName = cursor.getString(deviceFriendlyNameIndex);
            String deviceLastData = cursor.getString(deviceLastDataIndex);
            String deviceMqttPrefix = cursor.getString(deviceMqttPrefixIndex);
            String deviceDiodeChannel = cursor.getString(deviceDiodeChannelIndex);

            // Создание объекта устройства и инициализация его полей
            device = new Devices(deviceId, deviceType, deviceImg);
            device.setFriendlyName(deviceFriendlyName);
            device.setLastAcceptedData(deviceLastData);
            device.setMqttPrefix(deviceMqttPrefix);
            device.setDiodeChannel(deviceDiodeChannel);
        }
        cursor.close();
        db.close();
        return device;
    }


    public void updateDevice(Devices device, Devices oldDevice) {
        String id = getDeviceId(oldDevice);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("device_type", device.getType());
        values.put("img_path", device.getImgPath());
        values.put("friendly_name", device.getFriendlyName());
        values.put("mqtt_prefix", device.getMqttPrefix());
        values.put("diode_channel", device.getDiodeChannel());
        if(!device.getImgPath().isEmpty())
            values.put("img_path", device.getImgPath());
        if(!device.getFriendlyName().isEmpty())
            values.put("friendly_name", device.getFriendlyName());
        if(!device.getDiodeChannel().isEmpty())
            values.put("diode_channel", device.getDiodeChannel());
        db.update("devices", values, "_id = ?", new String[]{id});
        db.close();
    }

    public void updateLastDataForDevice(Devices device, String newData) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("last_data", newData);
        db.update("devices", values, "device_id = ?", new String[]{device.getDeviceId()});
        db.close();
    }


}
