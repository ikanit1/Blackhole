package com.example.blackhole;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionsActivity extends AppCompatActivity {

    private Spinner countryCodeSpinner;
    private EditText phoneNumberEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        countryCodeSpinner = findViewById(R.id.countryCodeSpinner);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        saveButton = findViewById(R.id.saveButton);

        // Здесь можно заполнить Spinner списком кодов стран и соответствующих флагов
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

        saveButton.setOnClickListener(v -> {
            String countryCode = countryCodeSpinner.getSelectedItem().toString();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(OptionsActivity.this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
            } else if (isValidPhoneNumber(countryCode + phoneNumber)) {
                // Сохранение номера телефона
                Toast.makeText(OptionsActivity.this, "Номер телефона сохранен: " + countryCode + phoneNumber, Toast.LENGTH_SHORT).show();

                // Intent на другую активность или логика сохранения данных
                // Например, переход на другую активность:
                Intent intent = new Intent(OptionsActivity.this, AppSelectionActivity.class);
                intent.putExtra("PHONE_NUMBER", countryCode + phoneNumber);
                startActivity(intent);
            } else {
                Toast.makeText(OptionsActivity.this, "Некорректный номер телефона", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Регулярное выражение для валидации номера телефона
        String regex = "^\\+\\d{1,3}\\d{4,14}(?:x.+)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
