package com.example.diplomaapp.dataClasses;


public class Devices {
    private String friendlyName = "";
    private String deviceId;
    private String type;
    private String imgPath = "";
    private String lastAcceptedData = "";
    private String MqttPrefix = "";
    private String diodeChannel = "";

    public Devices(String deviceId, String type, String imgPath) {
        this.deviceId = deviceId;
        this.type = type;
        this.imgPath = imgPath;
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

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getMqttPrefix() {
        return MqttPrefix;
    }

    public void setMqttPrefix(String mqttPrefix) {
        MqttPrefix = mqttPrefix;
    }

    public String getDiodeChannel() {
        return diodeChannel;
    }

    public void setDiodeChannel(String diodeChannel) {
        this.diodeChannel = diodeChannel;
    }
}
