package com.example.blackhole;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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

        appAdapter = new AppAdapter(new ArrayList<>(), viewModel);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4)); // 4 столбца в сетке
        recyclerView.setAdapter(appAdapter);

        viewModel.getInstalledApps().observe(this, appInfos -> appAdapter.updateData(appInfos));
        viewModel.loadInstalledApps(getPackageManager());

        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        Toast.makeText(this, "Phone Number: " + phoneNumber, Toast.LENGTH_SHORT).show();

        saveButton.setOnClickListener(v -> {
            // Обработка сохранения выбранных приложений
        });
    }
}
