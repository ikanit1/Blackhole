package com.example.blackhole;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AppInputsActivity extends AppCompatActivity {

    private LinearLayout selectedAppsContainer;
    private EditText ipAddress;
    private Button saveButton;
    private BottomNavigationView bottomNavigationView;
    private TextView editLink;

    private List<String> selectedAppPackageNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_inputs);

        selectedAppsContainer = findViewById(R.id.selected_apps_container);
        ipAddress = findViewById(R.id.ip_address);
        saveButton = findViewById(R.id.save_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        editLink = findViewById(R.id.edit_link);

        selectedAppPackageNames = getIntent().getStringArrayListExtra("SELECTED_APPS");
        if (selectedAppPackageNames != null) {
            displaySelectedApps(selectedAppPackageNames);
        }

        saveButton.setOnClickListener(v -> {
            String ip = ipAddress.getText().toString();
            if (ip.isEmpty()) {
                Toast.makeText(this, "IP сервера не может быть пустым", Toast.LENGTH_SHORT).show();
                return;
            }
            // Handle saving logic here
            showSuccessDialog();
        });

        editLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppSelectionActivity.class);
            intent.putStringArrayListExtra("SELECTED_APPS", new ArrayList<>(selectedAppPackageNames));
            startActivityForResult(intent, 1);
        });

        // Set up BottomNavigationView
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
        // Inflate the custom dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);

        // Find the TextView and apply the custom font
        TextView messageTextView = dialogView.findViewById(R.id.success_message);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.onest_font_family);
        messageTextView.setTypeface(typeface);

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
