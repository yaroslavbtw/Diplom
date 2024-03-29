package com.example.diplomaapp.listeners;

public interface MqttConnectListener {
    void onSuccess(); // Метод для уведомления об успешном подключении
    void onFailure(Throwable exception); // Метод для уведомления о неудачном подключении
}
