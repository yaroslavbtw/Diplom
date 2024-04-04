package com.example.diplomaapp.listeners;

public interface MqttConnectionLostListener {
    void onConnectionLost(Throwable cause);
}
