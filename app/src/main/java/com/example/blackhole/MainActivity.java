package com.example.blackhole;

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

        continueButton.setOnClickListener(v -> {
            String countryCode = countryCodeSpinner.getSelectedItem().toString();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(MainActivity.this, "Введите номер телефонаjouiop", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the phone number submission
                Toast.makeText(MainActivity.this, "Phone Number: " + countryCode + phoneNumber, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
