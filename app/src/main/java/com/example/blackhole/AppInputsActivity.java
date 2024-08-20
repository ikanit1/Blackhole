package com.example.blackhole;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        // Инициализируем Retrofit
        String baseUrl = "http://localhost:3000/";  // Укажите базовый URL вашего API
        apiService = RetrofitClient.getClient(baseUrl).create(ApiService.class);

        // Загружаем сохраненные данные
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String savedIp = preferences.getString("server_ip", "");
        String savedPort = preferences.getString("server_port", "3000");

        ipAddress.setText(savedIp);
        port.setText(savedPort);

        selectedAppPackageNames = getIntent().getStringArrayListExtra("SELECTED_APPS");
        if (selectedAppPackageNames != null) {
            displaySelectedApps(selectedAppPackageNames);
        }

        saveButton.setOnClickListener(v -> {
            String ip = ipAddress.getText().toString();
            String portValue = port.getText().toString();

            if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(portValue)) {
                Toast.makeText(this, "IP и порт должны быть заполнены", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверяем подключение к базе данных через API
            validateDatabaseConnection(ip, portValue);
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
        // Параметры аутентификации убраны из запроса
        apiService.validateConnection(ip, port, null, null).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    // Сохраняем данные
                    saveServerConfig(ip, port);

                    // Запускаем службу для прослушивания уведомлений
                    startNotificationListenerService();

                    // Показ диалога об успешном сохранении
                    showSuccessDialog();
                } else {
                    Toast.makeText(AppInputsActivity.this, "Не удалось подключиться к базе данных. Проверьте введенные данные и попробуйте снова.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(AppInputsActivity.this, "Ошибка подключения: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startNotificationListenerService() {
        Intent serviceIntent = new Intent(this, NotificationListener.class);
        serviceIntent.putStringArrayListExtra("SELECTED_APPS", new ArrayList<>(selectedAppPackageNames));
        startService(serviceIntent);
    }

    private void displaySelectedApps(List<String> selectedAppPackageNames) {
        selectedAppsContainer.removeAllViews();
        PackageManager pm = getPackageManager();
        for (String packageName : selectedAppPackageNames) {
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                Drawable appIcon = pm.getApplicationIcon(appInfo);
                ImageView appIconView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
                params.setMargins(8, 8, 8, 8);
                appIconView.setLayoutParams(params);
                appIconView.setImageDrawable(appIcon);
                selectedAppsContainer.addView(appIconView);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void showSuccessDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            selectedAppPackageNames = data.getStringArrayListExtra("SELECTED_APPS");
            if (selectedAppPackageNames != null) {
                displaySelectedApps(selectedAppPackageNames);
            }
        }
    }
}
