package com.example.blackhole;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class AppInputsActivity extends AppCompatActivity {

    private LinearLayout selectedAppsContainer;
    private EditText ipAddress;
    private Button saveButton;
    private BottomNavigationView bottomNavigationView;
    private TextView editLink;

    private ArrayList<AppInfo> selectedApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_inputs);

        selectedAppsContainer = findViewById(R.id.selected_apps_container);
        ipAddress = findViewById(R.id.ip_address);
        saveButton = findViewById(R.id.save_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        editLink = findViewById(R.id.edit_link);

        selectedApps = getIntent().getParcelableArrayListExtra("SELECTED_APPS");
        if (selectedApps != null) {
            displaySelectedApps(selectedApps);
        }

        saveButton.setOnClickListener(v -> {
            String ip = ipAddress.getText().toString();
            if (ip.isEmpty()) {
                Toast.makeText(this, "IP сервера не может быть пустым", Toast.LENGTH_SHORT).show();
                return;
            }
            // Handle saving logic here
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
        });

        editLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppSelectionActivity.class);
            intent.putParcelableArrayListExtra("SELECTED_APPS", selectedApps);
            startActivityForResult(intent, 1);
        });
    }

    private void displaySelectedApps(ArrayList<AppInfo> selectedApps) {
        selectedAppsContainer.removeAllViews();
        for (AppInfo app : selectedApps) {
            ImageView appIcon = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
            params.setMargins(8, 8, 8, 8);
            appIcon.setLayoutParams(params);
            appIcon.setImageDrawable(app.getIcon());
            selectedAppsContainer.addView(appIcon);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            selectedApps = data.getParcelableArrayListExtra("SELECTED_APPS");
            if (selectedApps != null) {
                displaySelectedApps(selectedApps);
            }
        }
    }
}
