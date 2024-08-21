package com.example.blackhole;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner countryCodeSpinner;
    private EditText phoneNumberEditText;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Проверяем, был ли номер телефона уже введен
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String savedPhoneNumber = preferences.getString("phone_number", null);
        String savedCountryCode = preferences.getString("country_code", null);

        if (savedPhoneNumber != null && savedCountryCode != null) {
            // Если номер уже введен, переходим на следующую страницу
            Intent intent = new Intent(MainActivity.this, AppSelectionActivity.class);
            startActivity(intent);
            finish(); // Завершаем MainActivity, чтобы пользователь не мог вернуться назад
            return;
        }

        setContentView(R.layout.activity_main);

        countryCodeSpinner = findViewById(R.id.countryCodeSpinner);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        continueButton = findViewById(R.id.continueButton);

        String[] countryCodes = getResources().getStringArray(R.array.country_codes);

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

        continueButton.setOnClickListener(v -> {
            String countryCode = countryCodeSpinner.getSelectedItem().toString();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            String fullPhoneNumber = countryCode + phoneNumber;

            if (phoneNumber.isEmpty()) {
                Toast.makeText(MainActivity.this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
            } else if (!isValidPhoneNumber(fullPhoneNumber)) {
                Toast.makeText(MainActivity.this, "Некорректный номер телефона", Toast.LENGTH_SHORT).show();
            } else {
                // Сохраняем номер телефона и код страны
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("phone_number", phoneNumber);
                editor.putString("country_code", countryCode);
                editor.apply();

                // Переход на следующую страницу
                Intent intent = new Intent(MainActivity.this, AppSelectionActivity.class);
                intent.putExtra("PHONE_NUMBER", fullPhoneNumber);
                startActivity(intent);
            }
        });
    }

    // Метод для проверки корректности номера телефона
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Регулярное выражение для валидации номера телефона
        String regex = "^\\+\\d{1,3}\\d{4,14}(?:x.+)?$";
        return phoneNumber.matches(regex);
    }
}
