package com.example.diplomaapp.dataClasses;

import androidx.annotation.Nullable;

public class Devices {
    private String deviceId;
    private String type;
    private String lastAcceptedData;

    public Devices(String deviceId, String type) {
        this.deviceId = deviceId;
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLastAcceptedData() {
        return lastAcceptedData;
    }

    public void setLastAcceptedData(String lastAcceptedData) {
        this.lastAcceptedData = lastAcceptedData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
