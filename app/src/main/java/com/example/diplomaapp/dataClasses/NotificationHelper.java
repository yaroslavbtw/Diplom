package com.example.diplomaapp.dataClasses;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.diplomaapp.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "my_app_notifications";
    private static final String CHANNEL_NAME = "My App Notifications";

    public static void showNotification(Context context, String title, String message, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Проверяем версию Android для создания канала уведомлений только для Android 8.0 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }

        // Создаем уведомление
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.safe) // Устанавливаем иконку уведомления
                .setContentTitle(title) // Устанавливаем заголовок уведомления
                .setContentText(message) // Устанавливаем текст уведомления
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // Устанавливаем приоритет уведомления

        // Отображаем уведомление
        notificationManager.notify(notificationId, builder.build());
    }

    // Метод для создания канала уведомлений
    private static void createNotificationChannel(NotificationManager notificationManager) {
        // Создаем канал уведомлений только для Android 8.0 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

