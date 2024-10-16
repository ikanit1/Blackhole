package com.example.blackhole;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.NotificationCompat;

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

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, "Your_Channel_ID")
                .setContentTitle("Мониторинг уведомлений")
                .setContentText("Слушаем уведомления в фоновом режиме")
                .setSmallIcon(R.drawable.img) // Замените на корректный ресурс
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(1, notification);

        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        Set<String> savedPackages = preferences.getStringSet("selected_apps", null);
        if (savedPackages != null) {
            selectedAppPackageNames = new HashSet<>(savedPackages);
        }

        serverIp = preferences.getString("server_ip", "");
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                "Your_Channel_ID",
                "Канал службы слушателя уведомлений",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if (selectedAppPackageNames.contains(packageName)) {
            String notificationTitle = sbn.getNotification().extras.getString("android.title");
            String notificationText = sbn.getNotification().extras.getString("android.text");
            long postTime = sbn.getPostTime();

            saveNotificationToDatabase(packageName, notificationTitle, notificationText, postTime);
        }
    }

    private void saveNotificationToDatabase(String packageName, String title, String text, long postTime) {
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
            Log.e("NotificationListener", "Ошибка при сохранении уведомления в базу данных", e);
            saveLogToDLQ(packageName, title, text, postTime);
        }
    }

    private void saveLogToDLQ(String packageName, String title, String text, long postTime) {
        String log = "Пакет: " + packageName + ", Заголовок: " + title + ", Текст: " + text + ", Время: " + postTime;
        SharedPreferences preferences = getSharedPreferences("DLQPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String existingLogs = preferences.getString("dlq_logs", "");
        String updatedLogs = existingLogs + log + "\n";

        editor.putString("dlq_logs", updatedLogs);

        PackageManager pm = getPackageManager();
        try {
            Drawable appIcon = pm.getApplicationIcon(packageName);
            String encodedIcon = encodeDrawableToBase64(appIcon);
            editor.putString("icon_" + packageName, encodedIcon);
        } catch (PackageManager.NameNotFoundException ex) {
            Log.e("NotificationListener", "Иконка приложения не найдена", ex);
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
        if (intent != null && intent.getStringArrayListExtra("SELECTED_APPS") != null) {
            List<String> packages = intent.getStringArrayListExtra("SELECTED_APPS");
            selectedAppPackageNames = new HashSet<>(packages);

            SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet("selected_apps", selectedAppPackageNames);
            editor.apply();
        }

        return START_STICKY;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Обработка удаления уведомления, если требуется
    }
}
