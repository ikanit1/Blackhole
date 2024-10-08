package com.example.blackhole;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class SelectedContactsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_contacts);

        LinearLayout selectedContactsContainer = findViewById(R.id.selected_contacts_container);
        TextView editLink = findViewById(R.id.edit_link);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Получаем выбранные контакты из Intent
        ArrayList<Recipient> selectedRecipients = getIntent().getParcelableArrayListExtra("selected_recipients");

        // Проверяем, что список не пуст и заполняем макет
        if (selectedRecipients != null && !selectedRecipients.isEmpty()) {
            for (Recipient recipient : selectedRecipients) {
                TextView contactTextView = new TextView(this);
                contactTextView.setText(recipient.getName());
                contactTextView.setTextSize(18);
                contactTextView.setPadding(16, 8, 16, 8);
                contactTextView.setBackgroundResource(R.drawable.selected_contact_button_background);
                contactTextView.setTextColor(Color.parseColor("#303030")); // Цвет текста
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0); // Отступы
                contactTextView.setLayoutParams(params);
                selectedContactsContainer.addView(contactTextView);
            }
        } else {
            Toast.makeText(this, "No contacts selected.", Toast.LENGTH_SHORT).show();
        }

        // Обработка нажатия на ссылку редактирования
        editLink.setOnClickListener(v -> {
            Intent intent = new Intent(SelectedContactsActivity.this, RecipientSms.class);
            intent.putParcelableArrayListExtra("selected_recipients", selectedRecipients);
            startActivity(intent);
            finish(); // Закрываем текущее активити
        });

        // Обработка выбора пунктов в BottomNavigationView с использованием else if
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_push) {
                startActivity(new Intent(SelectedContactsActivity.this, AppSelectionActivity.class));
                return true;
            } else if (itemId == R.id.navigation_sms) {
                return true; // Остаемся на текущем экране
            } else if (itemId == R.id.navigation_dlq) {
                startActivity(new Intent(SelectedContactsActivity.this, DLQActivity.class));
                return true;
            } else if (itemId == R.id.navigation_journal) {
                startActivity(new Intent(SelectedContactsActivity.this, JournalActivity.class));
                return true;
            } else if (itemId == R.id.navigation_options) {
                startActivity(new Intent(SelectedContactsActivity.this, OptionsActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }
}
