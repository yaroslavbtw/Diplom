package com.example.diplomaapp;

public class Devices {
    private String deviceId;
    private String channel;

    public Devices(String deviceId, String channel) {
        this.deviceId = deviceId;
        this.channel = channel;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
