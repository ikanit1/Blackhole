package com.example.blackhole;

import android.content.Intent;
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
                R.drawable.flag_kz // Kazakhstan
        };

        CountryAdapter adapter = new CountryAdapter(this, countryCodes, flags);
        countryCodeSpinner.setAdapter(adapter);

        continueButton.setOnClickListener(v -> {
            String countryCode = countryCodeSpinner.getSelectedItem().toString();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(MainActivity.this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the phone number submission
                Toast.makeText(MainActivity.this, "Phone Number: " + countryCode + phoneNumber, Toast.LENGTH_SHORT).show();

                // Intent to start AppSelectionActivity
                Intent intent = new Intent(MainActivity.this, AppSelectionActivity.class);
                intent.putExtra("PHONE_NUMBER", countryCode + phoneNumber);
                startActivity(intent);
            }
        });
    }
}
