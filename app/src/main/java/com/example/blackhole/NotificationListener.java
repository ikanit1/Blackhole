package com.example.blackhole;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Получаем пакет (имя пакета приложения)
        String packageName = sbn.getPackageName();

        // Получаем список выбранных приложений из AppDatabase
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        List<String> selectedAppPackageNames = db.selectedAppsDao().getAllSelectedAppPackageNames();

        // Проверяем, что уведомление от выбранного приложения
        if (selectedAppPackageNames.contains(packageName)) {
            // Получаем текст уведомления
            String notificationText = sbn.getNotification().extras.getString("android.text");

            // Сохраняем данные в базу
            saveNotificationToDatabase(packageName, notificationText);
        }
    }

    private void saveNotificationToDatabase(String packageName, String notificationText) {
        // Создаем объект LogEntry и сохраняем его в базу данных
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        LogEntry logEntry = new LogEntry(
                timestamp,
                packageName, // В качестве phoneNumber указываем пакет приложения
                packageName, // В качестве recipient также указываем пакет приложения
                notificationText // Текст уведомления
                // Статус обработки (по умолчанию 0)
                // Уровень лога (по умолчанию INFO)
                // Стектрейс (пустой для уведомлений)
        );

        // Выполняем вставку в базу данных
        db.logEntryDao().insert(logEntry);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Обработка удаления уведомления, если это необходимо
    }
}
