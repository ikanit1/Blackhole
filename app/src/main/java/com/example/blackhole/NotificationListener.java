package com.example.blackhole;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
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
            saveLogToDLQ(packageName, title, text, postTime);
        }
    }

    private void saveLogToDLQ(String packageName, String title, String text, long postTime) {
        String log = "Package: " + packageName + ", Title: " + title + ", Text: " + text + ", Time: " + postTime;
        SharedPreferences preferences = getSharedPreferences("DLQPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Сохранение информации о приложении и уведомлении
        String existingLogs = preferences.getString("dlq_logs", "");
        String updatedLogs = existingLogs + log + "\n";

        editor.putString("dlq_logs", updatedLogs);

        // Дополнительно сохраняем иконку приложения
        PackageManager pm = getPackageManager();
        try {
            Drawable appIcon = pm.getApplicationIcon(packageName);
            // Сохранение иконки в виде Base64 строки
            String encodedIcon = encodeDrawableToBase64(appIcon);
            editor.putString("icon_" + packageName, encodedIcon);
        } catch (PackageManager.NameNotFoundException ex) {
            Log.e("NotificationListener", "App icon not found", ex);
        }

        editor.apply();
    }

    private String encodeDrawableToBase64(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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
