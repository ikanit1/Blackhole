package com.example.blackhole;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OptionsActivity extends AppCompatActivity {

    private Spinner countryCodeSpinner;
    private EditText phoneNumberEditText;
    private Button saveButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        countryCodeSpinner = findViewById(R.id.countryCodeSpinner);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        saveButton = findViewById(R.id.saveButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Загружаем сохраненные данные
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String savedPhoneNumber = preferences.getString("phone_number", "");
        String savedCountryCode = preferences.getString("country_code", "");

        phoneNumberEditText.setText(savedPhoneNumber);

        // Находим индекс сохраненного кода страны
        String[] countryCodes = getResources().getStringArray(R.array.country_codes);
        int selectedIndex = 0;
        for (int i = 0; i < countryCodes.length; i++) {
            if (countryCodes[i].equals(savedCountryCode)) {
                selectedIndex = i;
                break;
            }
        }

        int[] flags = {
                R.drawable.flag_us,
                R.drawable.flag_ru,
                R.drawable.flag_fr,
                R.drawable.flag_de,
                R.drawable.flag_jp,
                R.drawable.flag_in,
                R.drawable.flag_uk,
                R.drawable.flag_kz
        };

        CountryAdapter adapter = new CountryAdapter(this, countryCodes, flags);
        countryCodeSpinner.setAdapter(adapter);
        countryCodeSpinner.setSelection(selectedIndex); // Устанавливаем выбранный код страны

        saveButton.setOnClickListener(v -> {
            String countryCode = countryCodeSpinner.getSelectedItem().toString();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(OptionsActivity.this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
            } else if (isValidPhoneNumber(countryCode + phoneNumber)) {
                // Сохраняем номер телефона и код страны
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("phone_number", phoneNumber);
                editor.putString("country_code", countryCode);
                editor.apply();

                Toast.makeText(OptionsActivity.this, "Номер телефона сохранен: " + countryCode + phoneNumber, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(OptionsActivity.this, "Некорректный номер телефона", Toast.LENGTH_SHORT).show();
            }
        });

        // Обработка навигации
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_push) {
                startActivity(new Intent(OptionsActivity.this, AppSelectionActivity.class));
                return true;
            } else if (itemId == R.id.navigation_sms) {
                startActivity(new Intent(OptionsActivity.this, RecipientSms.class));
                return true;
            } else if (itemId == R.id.navigation_dlq) {
                startActivity(new Intent(OptionsActivity.this, DLQActivity.class));
                return true;
            } else if (itemId == R.id.navigation_journal) {
                startActivity(new Intent(OptionsActivity.this, JournalActivity.class));
                return true;
            } else if (itemId == R.id.navigation_options) {
                return true; // Остаемся на текущей активности
            } else {
                return false;
            }
        });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Регулярное выражение для валидации номера телефона
        String regex = "^\\+\\d{1,3}\\d{4,14}(?:x.+)?$";
        return phoneNumber.matches(regex);
    }
}
