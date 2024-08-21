package com.example.blackhole;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DLQActivity extends AppCompatActivity {

    private LinearLayout logsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlq);

        logsContainer = findViewById(R.id.logs_container);

        SharedPreferences preferences = getSharedPreferences("DLQPrefs", MODE_PRIVATE);
        String logs = preferences.getString("dlq_logs", "Нет доступных логов.");

        String[] logEntries = logs.split("\n");

        for (String logEntry : logEntries) {
            if (!logEntry.isEmpty()) {
                // Извлекаем информацию из логов
                String[] logParts = logEntry.split(", ");

                if (logParts.length >= 4) {
                    String packageName = extractValue(logParts[0]);
                    String title = extractValue(logParts[1]);
                    String text = extractValue(logParts[2]);
                    String time = extractValue(logParts[3]);

                    // Загружаем иконку приложения
                    String encodedIcon = preferences.getString("icon_" + packageName, null);
                    Drawable appIcon = decodeBase64ToDrawable(encodedIcon);

                    // Создаем и добавляем элемент UI для отображения уведомления
                    addLogEntryToUI(appIcon, title, text, time);
                } else {
                    // Лог не имеет ожидаемого формата
                    Log.e("DLQActivity", "Ошибка в формате строки лога: " + logEntry);
                }
            }
        }
    }

    private String extractValue(String logPart) {
        // Извлечение значения после ": "
        return logPart.split(": ")[1];
    }

    private Drawable decodeBase64ToDrawable(String encodedImage) {
        if (encodedImage != null) {
            byte[] decodedByte = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            return new BitmapDrawable(getResources(), bitmap);
        }
        return null;
    }


    private void addLogEntryToUI(Drawable appIcon, String title, String text, String time) {
        LinearLayout logItem = new LinearLayout(this);
        logItem.setOrientation(LinearLayout.HORIZONTAL);
        logItem.setPadding(8, 8, 8, 8);

        ImageView iconView = new ImageView(this);
        iconView.setImageDrawable(appIcon);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(100, 100);
        iconParams.setMargins(8, 8, 8, 8);
        iconView.setLayoutParams(iconParams);

        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);

        TextView titleView = new TextView(this);
        titleView.setText("Title: " + title);

        TextView textView = new TextView(this);
        textView.setText("Text: " + text);

        TextView timeView = new TextView(this);
        timeView.setText("Time: " + time);

        textContainer.addView(titleView);
        textContainer.addView(textView);
        textContainer.addView(timeView);

        logItem.addView(iconView);
        logItem.addView(textContainer);

        logsContainer.addView(logItem);
    }
}
