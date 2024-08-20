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

        viewModel = new ViewModelProvider(this).get(AppSelectionViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        Button saveButton = findViewById(R.id.save_button);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        progressBar = findViewById(R.id.progress_bar);

        appAdapter = new AppAdapter(new ArrayList<>(), viewModel);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4)); // 4 columns in the grid
        recyclerView.setAdapter(appAdapter);

        progressBar.setVisibility(View.VISIBLE); // Show ProgressBar at start of loading

        // ProgressBar Animation
        ObjectAnimator animator = ObjectAnimator.ofFloat(progressBar, "rotation", 0f, 360f);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();

        viewModel.getInstalledApps().observe(this, appInfos -> {
            appAdapter.updateData(appInfos);
            progressBar.setVisibility(View.GONE); // Hide ProgressBar after loading data
            animator.cancel();

            // Restore selected apps state
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
