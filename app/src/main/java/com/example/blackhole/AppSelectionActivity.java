package com.example.blackhole;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

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

        viewModel = new ViewModelProvider(this).get(AppSelectionViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        Button saveButton = findViewById(R.id.save_button);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        progressBar = findViewById(R.id.progress_bar);

        appAdapter = new AppAdapter(new ArrayList<>(), viewModel);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4)); // 4 columns in the grid
        recyclerView.setAdapter(appAdapter);

        progressBar.setVisibility(View.VISIBLE); // Показать ProgressBar при старте загрузки

        // Анимация ProgressBar
        ObjectAnimator animator = ObjectAnimator.ofFloat(progressBar, "rotation", 0f, 360f);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();

        viewModel.getInstalledApps().observe(this, appInfos -> {
            appAdapter.updateData(appInfos);
            progressBar.setVisibility(View.GONE); // Скрыть ProgressBar после загрузки данных
            animator.cancel();
        });

        viewModel.loadInstalledApps(getPackageManager());

        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        Toast.makeText(this, "Phone Number: " + phoneNumber, Toast.LENGTH_SHORT).show();

        saveButton.setOnClickListener(v -> {
            List<AppInfo> selectedApps = viewModel.getSelectedApps();
            ArrayList<String> selectedAppPackageNames = new ArrayList<>();
            for (AppInfo app : selectedApps) {
                selectedAppPackageNames.add(app.getPackageName());
            }

            Intent intent = new Intent(AppSelectionActivity.this, AppInputsActivity.class);
            intent.putStringArrayListExtra("SELECTED_APPS", selectedAppPackageNames);
            startActivity(intent);
        });
    }
}
