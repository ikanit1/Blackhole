package com.example.blackhole;

import android.content.Intent;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotificationListener extends NotificationListenerService {

    private Set<String> selectedAppPackageNames;
    private String serverIp;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        serverIp = preferences.getString("server_ip", "");

        // Загружаем выбранные пакеты приложений
        selectedAppPackageNames = new HashSet<>(preferences.getStringSet("selected_apps", new HashSet<>()));
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if (selectedAppPackageNames.contains(packageName)) {
            String notificationTitle = sbn.getNotification().extras.getString("android.title");
            String notificationText = sbn.getNotification().extras.getString("android.text");
            long postTime = sbn.getPostTime();

            // Сохранить уведомление в базу данных
            saveNotificationToDatabase(packageName, notificationTitle, notificationText, postTime);
        }
    }

    private void saveNotificationToDatabase(String packageName, String title, String text, long postTime) {
        // Подключение к базе данных MySQL
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://" + serverIp + ":3306/your_database", "username", "password")) {
            String query = "INSERT INTO notifications (package_name, title, text, post_time) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, packageName);
                statement.setString(2, title);
                statement.setString(3, text);
                statement.setLong(4, postTime);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Log.e("NotificationListener", "Error saving notification to database", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        List<String> packages = intent.getStringArrayListExtra("SELECTED_APPS");
        if (packages != null) {
            selectedAppPackageNames = new HashSet<>(packages);

            // Сохраняем выбранные приложения для использования позже
            SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet("selected_apps", selectedAppPackageNames);
            editor.apply();
        }
        return START_STICKY;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Может быть использовано для дополнительных действий при удалении уведомления
    }
}
