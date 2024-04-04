package com.example.diplomaapp.listeners;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

public interface MqttDeliveryCompleteListener {
    void onDeliveryComplete(IMqttDeliveryToken token);
}
