package com.example.blackhole;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SelectedContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_contacts);

        LinearLayout selectedContactsContainer = findViewById(R.id.selected_contacts_container);
        TextView editLink = findViewById(R.id.edit_link);

        // Get the selected recipients from the intent
        ArrayList<Recipient> selectedRecipients = getIntent().getParcelableArrayListExtra("selected_recipients");

        // Check if the list is not empty and populate the layout
        if (selectedRecipients != null && !selectedRecipients.isEmpty()) {
            for (Recipient recipient : selectedRecipients) {
                TextView contactTextView = new TextView(this);
                contactTextView.setText(recipient.getName());
                contactTextView.setTextSize(18);
                selectedContactsContainer.addView(contactTextView);
            }
        }

        // Handle edit link click
        editLink.setOnClickListener(v -> {
            Intent intent = new Intent(SelectedContactsActivity.this, RecipientSms.class);
            intent.putParcelableArrayListExtra("selected_recipients", selectedRecipients);
            startActivity(intent);
            finish(); // Close the current activity
        });
    }
}
