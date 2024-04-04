package com.example.diplomaapp.listeners;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface MqttMessageArrivedListener {
    void onMessageArrived(String topic, MqttMessage message);
}
