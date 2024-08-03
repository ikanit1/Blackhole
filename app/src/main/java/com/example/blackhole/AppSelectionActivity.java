package com.example.blackhole;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        viewModel = new ViewModelProvider(this).get(AppSelectionViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        Button saveButton = findViewById(R.id.save_button);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        appAdapter = new AppAdapter(new ArrayList<>(), viewModel);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4)); // 4 columns in the grid
        recyclerView.setAdapter(appAdapter);

        viewModel.getInstalledApps().observe(this, appInfos -> appAdapter.updateData(appInfos));
        viewModel.loadInstalledApps(getPackageManager());

        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        Toast.makeText(this, "Phone Number: " + phoneNumber, Toast.LENGTH_SHORT).show();

        saveButton.setOnClickListener(v -> {
                List<AppInfo> selectedApps = viewModel.getSelectedApps(); // предположим, что у вас есть метод для получения выбранных приложений
            AppDataHolder.getInstance().setSelectedApps(selectedApps);

            Intent intent = new Intent(AppSelectionActivity.this, AppInputsActivity.class);
            startActivity(intent);
        });

    }
}
