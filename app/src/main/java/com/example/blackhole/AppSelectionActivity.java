package com.example.blackhole;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AppSelectionActivity extends AppCompatActivity {

    private AppSelectionViewModel viewModel;
    private AppAdapter appAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        // Запуск слушателя уведомлений
        Intent notificationListenerIntent = new Intent(this, NotificationListener.class);
        startService(notificationListenerIntent); // Запуск слушателя уведомлений при запуске активити

        viewModel = new ViewModelProvider(this).get(AppSelectionViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        Button saveButton = findViewById(R.id.save_button);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        progressBar = findViewById(R.id.progress_bar);

        appAdapter = new AppAdapter(new ArrayList<>(), viewModel);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4)); // 4 столбца в сетке
        recyclerView.setAdapter(appAdapter);

        progressBar.setVisibility(View.VISIBLE); // Показываем индикатор загрузки в начале

        // Анимация для ProgressBar
        ObjectAnimator animator = ObjectAnimator.ofFloat(progressBar, "rotation", 0f, 360f);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();

        viewModel.getInstalledApps().observe(this, appInfos -> {
            appAdapter.updateData(appInfos);
            progressBar.setVisibility(View.GONE); // Скрываем индикатор после загрузки данных
            animator.cancel();

            // Восстановление состояния выбранных приложений
            viewModel.restoreSelectedApps();
        });

        viewModel.loadInstalledApps(getPackageManager());

        saveButton.setOnClickListener(v -> {
            viewModel.saveSelectedApps();
            List<AppInfo> selectedApps = viewModel.getSelectedApps().getValue();
            ArrayList<String> selectedAppPackageNames = new ArrayList<>();
            if (selectedApps != null) {
                for (AppInfo app : selectedApps) {
                    selectedAppPackageNames.add(app.getAppPackageName());
                }
            }

            Intent intent = new Intent(AppSelectionActivity.this, AppInputsActivity.class);
            intent.putStringArrayListExtra("SELECTED_APPS", selectedAppPackageNames);
            startActivity(intent);
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_push) {
                startActivity(new Intent(AppSelectionActivity.this, AppSelectionActivity.class));
                return true;
            } else if (itemId == R.id.navigation_sms) {
                startActivity(new Intent(AppSelectionActivity.this, RecipientSms.class));
                return true;
            } else if (itemId == R.id.navigation_dlq) {
                startActivity(new Intent(AppSelectionActivity.this, DLQActivity.class));
                return true;
            } else if (itemId == R.id.navigation_journal) {
                startActivity(new Intent(AppSelectionActivity.this, JournalActivity.class));
                return true;
            } else if (itemId == R.id.navigation_options) {
                startActivity(new Intent(AppSelectionActivity.this, OptionsActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }
}
