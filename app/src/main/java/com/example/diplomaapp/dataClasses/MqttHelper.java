package com.example.diplomaapp.dataClasses;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.diplomaapp.listeners.MqttConnectListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttHelper {
    private MqttAndroidClient mqttAndroidClient;

    public MqttHelper(Context appContext, String serverUri, final String username, final String password, MqttConnectListener connectListener) {
        String clientId = MqttClient.generateClientId();
        mqttAndroidClient = new MqttAndroidClient(appContext, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {}

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i("MQTT", message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        if(username != null && password != null)
        {
            mqttConnectOptions.setUserName(username);
            mqttConnectOptions.setPassword(password.toCharArray());
        }

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("MQTT", "Successfully CONNECTED");
                    if (connectListener != null) {
                        connectListener.onSuccess(); // Уведомляем об успешном подключении
                    }

                    subscribeToTopic("zigbee2mqtt/0x00124B00281A9824");
//                    publishMessage("zigbee2mqtt/0x123456789/l1/state", "Off");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(appContext.getApplicationContext(), "Failed to connect: " + exception.toString(), Toast.LENGTH_LONG).show();
                    if (connectListener != null) {
                        connectListener.onFailure(new Throwable()); // Уведомляем об успешном подключении
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("MQTT", ex.toString());
        }
    }

    public void subscribeToTopic(String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("MQTT", "CONNECTED TO TOPIC");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i("MQTT", "NOT CONNECTED TO TOPIC");
                }
            });
        } catch (Exception ex) {
            Log.i("MQTT", ex.toString());
            ex.printStackTrace();
        }
    }

    public void publishMessage(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());
            mqttAndroidClient.publish(topic, mqttMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            mqttAndroidClient.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isConnected() {
        return mqttAndroidClient != null && mqttAndroidClient.isConnected();
    }
}

