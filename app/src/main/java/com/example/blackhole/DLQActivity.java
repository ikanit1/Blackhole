package com.example.blackhole;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DLQActivity extends AppCompatActivity {

    private LinearLayout logsContainer;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlq);

        logsContainer = findViewById(R.id.logs_container);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load logs from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("DLQPrefs", MODE_PRIVATE);
        String logs = preferences.getString("dlq_logs", "Нет доступных логов.");

        // Split logs into individual entries
        String[] logEntries = logs.split("\n");

        // Process each log entry
        for (String logEntry : logEntries) {
            if (!logEntry.isEmpty()) {
                try {
                    // Extract information from logs
                    String[] logParts = logEntry.split(", ");

                    if (logParts.length >= 3) {
                        // Assuming log format is title, text, time
                        String title = extractValue(logParts[0]);
                        String text = extractValue(logParts[1]);
                        String time = extractValue(logParts[2]);

                        // Use default icon for all notifications
                        Drawable appIcon = ContextCompat.getDrawable(this, R.drawable.img); // Default icon

                        // Add log entry to the UI
                        addLogEntryToUI(appIcon, title, text, time);
                    }
                } catch (Exception e) {
                    // Log error to console but do not display it to the user
                    Log.e("DLQActivity", "Error processing log entry: " + logEntry, e);
                }
            }
        }

        // Set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_push) {
                startActivity(new Intent(DLQActivity.this, AppSelectionActivity.class));
                return true;
            } else if (itemId == R.id.navigation_sms) {
                startActivity(new Intent(DLQActivity.this, RecipientSms.class));
                return true;
            } else if (itemId == R.id.navigation_dlq) {
                return true; // Stay in the current activity
            } else if (itemId == R.id.navigation_journal) {
                startActivity(new Intent(DLQActivity.this, JournalActivity.class));
                return true;
            } else if (itemId == R.id.navigation_options) {
                startActivity(new Intent(DLQActivity.this, OptionsActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }

    // Extract the value from a log part
    private String extractValue(String logPart) {
        int colonIndex = logPart.indexOf(": ");
        if (colonIndex != -1) {
            return logPart.substring(colonIndex + 2).trim();
        } else {
            return logPart.trim();
        }
    }

    // Add a log entry to the UI
    private void addLogEntryToUI(Drawable appIcon, String title, String text, String time) {
        // Create a container for each log entry
        LinearLayout logItem = new LinearLayout(this);
        logItem.setOrientation(LinearLayout.HORIZONTAL);
        logItem.setPadding(16, 16, 16, 16);
        logItem.setGravity(Gravity.CENTER_VERTICAL);

        // Add app icon
        ImageView iconView = new ImageView(this);
        iconView.setImageDrawable(appIcon);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(120, 120);
        iconParams.setMargins(16, 16, 16, 16);
        iconView.setLayoutParams(iconParams);

        // Create a container for text content
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setPadding(16, 0, 0, 0);

        // Title
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(16);
        titleView.setTextColor(ContextCompat.getColor(this, R.color.black));
        titleView.setTypeface(titleView.getTypeface(), android.graphics.Typeface.BOLD);

        // Log text
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setTextColor(ContextCompat.getColor(this, R.color.black));

        // Time
        TextView timeView = new TextView(this);
        timeView.setText(time);
        timeView.setTextSize(12);
        timeView.setTextColor(ContextCompat.getColor(this, R.color.black));

        // Add views to the text container
        textContainer.addView(titleView);
        textContainer.addView(textView);
        textContainer.addView(timeView);

        // Add icon and text container to the log item
        logItem.addView(iconView);
        logItem.addView(textContainer);

        // Optionally set background for each log item
        logItem.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_background));
        logItem.setClickable(true);

        // Add the log item to the logs container
        logsContainer.addView(logItem);
    }
}
