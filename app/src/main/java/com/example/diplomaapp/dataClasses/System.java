package com.example.diplomaapp.dataClasses;

public class System {
    private String systemName;
    private String mqtt_url;
    private String mqtt_login;
    private String mqtt_password;

    public System(String systemName, String mqtt_url) {
        this.systemName = systemName;
        this.mqtt_url = mqtt_url;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getMqtt_url() {
        return mqtt_url;
    }

    public void setMqtt_url(String mqtt_url) {
        this.mqtt_url = mqtt_url;
    }

    public String getMqtt_login() {
        return mqtt_login;
    }

    public void setMqtt_login(String mqtt_login) {
        this.mqtt_login = mqtt_login;
    }

    public String getMqtt_password() {
        return mqtt_password;
    }

    public void setMqtt_password(String mqtt_password) {
        this.mqtt_password = mqtt_password;
    }
}
