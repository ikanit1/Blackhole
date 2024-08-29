package com.example.blackhole;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppInputsActivity extends AppCompatActivity {

    private LinearLayout selectedAppsContainer;
    private EditText ipAddress;
    private EditText port;
    private Button saveButton;
    private BottomNavigationView bottomNavigationView;
    private TextView editLink;

    private List<String> selectedAppPackageNames;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_inputs);

        selectedAppsContainer = findViewById(R.id.selected_apps_container);
        ipAddress = findViewById(R.id.ip_address);
        port = findViewById(R.id.port);
        saveButton = findViewById(R.id.save_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        editLink = findViewById(R.id.edit_link);

        // Initialize Retrofit
        String baseUrl = "http://91.234.199.200:5000/";
        apiService = RetrofitClient.getClient(baseUrl).create(ApiService.class);

        // Load saved data
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String savedIp = preferences.getString("server_ip", "");
        String savedPort = preferences.getString("server_port", "3000");

        ipAddress.setText(savedIp);
        port.setText(savedPort);

        // Get selected app package names from the intent
        selectedAppPackageNames = getIntent().getStringArrayListExtra("SELECTED_APPS");
        if (selectedAppPackageNames != null) {
            displaySelectedApps(selectedAppPackageNames);
        }

        // Handle the "Edit" button click
        editLink.setOnClickListener(v -> {
            Intent intent = new Intent(AppInputsActivity.this, AppSelectionActivity.class);
            intent.putStringArrayListExtra("SELECTED_APPS", new ArrayList<>(selectedAppPackageNames));
            startActivityForResult(intent, 1);
        });

        saveButton.setOnClickListener(v -> {
            String ip = ipAddress.getText().toString();
            String portValue = port.getText().toString();

            if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(portValue)) {
                Toast.makeText(this, "IP and port must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate database connection via API
            validateDatabaseConnection(ip, portValue);

            // After validation, send the message to the server
            sendMessageToServer();
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_push) {
                startActivity(new Intent(AppInputsActivity.this, AppSelectionActivity.class));
                return true;
            } else if (itemId == R.id.navigation_sms) {
                startActivity(new Intent(AppInputsActivity.this, RecipientSms.class));
                return true;
            } else if (itemId == R.id.navigation_dlq) {
                startActivity(new Intent(AppInputsActivity.this, DLQActivity.class));
                return true;
            } else if (itemId == R.id.navigation_journal) {
                startActivity(new Intent(AppInputsActivity.this, JournalActivity.class));
                return true;
            } else if (itemId == R.id.navigation_options) {
                startActivity(new Intent(AppInputsActivity.this, OptionsActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }

    private void saveServerConfig(String ip, String port) {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("server_ip", ip);
        editor.putString("server_port", port);
        editor.apply();
    }

    private void validateDatabaseConnection(String ip, String port) {
        apiService.validateConnection(ip, port, null, null).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    saveServerConfig(ip, port);
                    startNotificationListenerService();
                    showSuccessDialog();
                } else {
                    saveErrorLog("Connection error: Failed to connect to the database. Check the input data.");
                    Toast.makeText(AppInputsActivity.this, "Failed to connect to the database. Check the input data and try again.", Toast.LENGTH_LONG).show();
                }
                saveSelectedAppsToDLQ();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                saveErrorLog("Connection error: " + t.getMessage());
                Toast.makeText(AppInputsActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                saveSelectedAppsToDLQ();
            }
        });
    }

    private void sendMessageToServer() {
        String datetime = "2023-10-01T12:00:00";
        String phoneNumber = "+79266666666";
        String recipient = "user";
        String text = "llol";
        String status = "1";

        MessageData messageData = new MessageData(datetime, phoneNumber, recipient, text, status);

        apiService.sendMessage(messageData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AppInputsActivity.this, "Message sent successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppInputsActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AppInputsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSelectedAppsToDLQ() {
        SharedPreferences preferences = getSharedPreferences("DLQPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        for (String packageName : selectedAppPackageNames) {
            try {
                PackageManager pm = getPackageManager();
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                String appName = pm.getApplicationLabel(appInfo).toString();

                Drawable appIcon = pm.getApplicationIcon(appInfo);
                String encodedIcon = encodeDrawableToBase64(appIcon);

                long currentTimeMillis = System.currentTimeMillis();
                String time = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date(currentTimeMillis)).toString();

                String logEntry = "App: " + packageName + ", Name: " + appName + ", Time: " + time + "\n";

                String currentLogs = preferences.getString("dlq_logs", "");
                String updatedLogs = currentLogs + logEntry;
                editor.putString("dlq_logs", updatedLogs);
                editor.putString("icon_" + packageName, encodedIcon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        editor.apply();
    }

    private String encodeDrawableToBase64(Drawable drawable) {
        if (drawable != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        return null;
    }

    private void saveErrorLog(String errorMessage) {
        SharedPreferences preferences = getSharedPreferences("DLQPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        long currentTimeMillis = System.currentTimeMillis();
        String time = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date(currentTimeMillis)).toString();

        String logEntry = "Error: " + errorMessage + ", Time: " + time + "\n";

        String currentLogs = preferences.getString("dlq_logs", "");
        String updatedLogs = currentLogs + logEntry;

        editor.putString("dlq_logs", updatedLogs);
        editor.apply();
    }

    private void startNotificationListenerService() {
        Intent serviceIntent = new Intent(this, NotificationListener.class);
        serviceIntent.putStringArrayListExtra("SELECTED_APPS", new ArrayList<>(selectedAppPackageNames));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void displaySelectedApps(List<String> selectedAppPackageNames) {
        // Очищаем контейнер перед добавлением новых иконок
        selectedAppsContainer.removeAllViews();

        // Получаем менеджер пакетов
        PackageManager pm = getPackageManager();

        for (String packageName : selectedAppPackageNames) {
            try {
                // Получаем информацию о приложении
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);

                // Получаем иконку приложения
                Drawable appIcon = pm.getApplicationIcon(appInfo);

                // Создаем ImageView для отображения иконки
                ImageView appIconView = new ImageView(this);

                // Настраиваем параметры отображения иконки
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
                params.setMargins(8, 8, 8, 8); // Устанавливаем отступы
                appIconView.setLayoutParams(params);

                // Устанавливаем иконку в ImageView
                appIconView.setImageDrawable(appIcon);

                // Добавляем ImageView в контейнер
                selectedAppsContainer.addView(appIconView);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage("Data has been saved successfully.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
