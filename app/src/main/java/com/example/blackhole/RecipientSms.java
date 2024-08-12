package com.example.blackhole;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipientSms extends AppCompatActivity {

    private static final int REQUEST_READ_SMS = 100;
    private RecipientAdapter recipientAdapter;
    private List<Recipient> recipients;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_senders);

        RecyclerView recipientsRecyclerView = findViewById(R.id.recipientsRecyclerView);
        Button saveButton = findViewById(R.id.saveButton);
        Button selectAllButton = findViewById(R.id.selectAllButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_READ_SMS);
        } else {
            loadSmsRecipients();
        }

        recipientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipientsRecyclerView.setAdapter(recipientAdapter);

        selectAllButton.setOnClickListener(v -> {
            for (Recipient recipient : recipients) {
                recipient.setSelected(true);
            }
            recipientAdapter.notifyDataSetChanged();
        });

        saveButton.setOnClickListener(v -> {
            List<Recipient> selectedRecipients = new ArrayList<>();
            for (Recipient recipient : recipients) {
                if (recipient.isSelected()) {
                    selectedRecipients.add(recipient);
                }
            }
            // Сохранение selectedRecipients или другие действия с ними
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_push) {
                startActivity(new Intent(RecipientSms.this, AppSelectionActivity.class));
                return true;
            } else if (itemId == R.id.navigation_sms) {
                return true; // Stay on the current activity
            } else if (itemId == R.id.navigation_dlq) {
                startActivity(new Intent(RecipientSms.this, DLQActivity.class));
                return true;
            } else if (itemId == R.id.navigation_journal) {
                startActivity(new Intent(RecipientSms.this, JournalActivity.class));
                return true;
            } else if (itemId == R.id.navigation_options) {
                startActivity(new Intent(RecipientSms.this, OptionsActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }

    private void loadSmsRecipients() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsCursor = contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI,
                new String[]{Telephony.Sms.ADDRESS, Telephony.Sms.BODY},
                null, null, null);

        if (smsCursor != null) {
            Set<String> recipientSet = new HashSet<>();
            recipients = new ArrayList<>();
            while (smsCursor.moveToNext()) {
                String address = smsCursor.getString(smsCursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = smsCursor.getString(smsCursor.getColumnIndexOrThrow(Telephony.Sms.BODY));

                if (!recipientSet.contains(address)) {
                    recipientSet.add(address);
                    recipients.add(new Recipient(address, body, R.drawable.succes, false));
                }
            }
            smsCursor.close();

            recipientAdapter = new RecipientAdapter(this, recipients);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSmsRecipients();
            } else {
                Toast.makeText(this, "Permission denied to read SMS", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
