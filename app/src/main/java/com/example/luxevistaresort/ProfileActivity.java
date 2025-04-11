package com.example.luxevistaresort;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private int guestId;
    private EditText nameEditText, usernameEditText, emailEditText, phoneNumberEditText;
    private EditText travelDatesTextView, preferencesTextView;
    private Button saveProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        nameEditText = findViewById(R.id.nameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        travelDatesTextView = findViewById(R.id.travelDatesTextView);
        preferencesTextView = findViewById(R.id.preferencesTextView);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        guestId = sharedPreferences.getInt("GUEST_ID", -1);

        if (guestId != -1) {
            loadProfileData();
        } else {
            Toast.makeText(this, "Guest ID is invalid", Toast.LENGTH_SHORT).show();
        }

        saveProfileButton.setOnClickListener(v -> saveProfileData());
    }

    private void loadProfileData() {
        Cursor cursor = dbHelper.getGuestDetails(guestId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("Name"));
            String username = cursor.getString(cursor.getColumnIndex("Username"));
            String email = cursor.getString(cursor.getColumnIndex("Email"));
            String phoneNumber = cursor.getString(cursor.getColumnIndex("Phone_Number"));
            String travelDates = cursor.getString(cursor.getColumnIndex("Travel_Dates"));
            String preferences = cursor.getString(cursor.getColumnIndex("Preferences"));

            nameEditText.setText(name);
            usernameEditText.setText(username);
            emailEditText.setText(email);
            phoneNumberEditText.setText(phoneNumber);
            travelDatesTextView.setText(travelDates);
            preferencesTextView.setText(preferences);

            fetchAndDisplayPreferences(preferences);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void saveProfileData() {
        String name = nameEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String travelDates = travelDatesTextView.getText().toString();
        String preferences = preferencesTextView.getText().toString();

        boolean isValid = true;

        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            isValid = false;
        }

        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
            isValid = false;
        }

        if (email.isEmpty() || !isValidEmail(email)) {
            emailEditText.setError("Valid email is required");
            isValid = false;
        }

        if (phoneNumber.isEmpty() || !isValidPhoneNumber(phoneNumber)) {
            phoneNumberEditText.setError("Valid phone number is required");
            isValid = false;
        }

        if (travelDates.isEmpty()) {
            travelDatesTextView.setError("Travel dates are required");
            isValid = false;
        }

        if (preferences.isEmpty()) {
            preferencesTextView.setError("Preferences are required");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        boolean success = dbHelper.addOrUpdateGuest(guestId, name, username, email, phoneNumber, travelDates, preferences);
        if (success) {
            fetchAndDisplayPreferences(preferences);
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
        saveProfileButton.setVisibility(View.VISIBLE);
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+$";
        return Pattern.matches(emailPattern, email);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phonePattern = "^[0-9]{10,15}$";
        return Pattern.matches(phonePattern, phoneNumber);
    }

    private void fetchAndDisplayPreferences(String preferences) {
        Cursor servicesCursor = dbHelper.getServicesByPreference(preferences);
        Cursor attractionsCursor = dbHelper.getAttractionsByPreference(preferences);
        Cursor roomsCursor = dbHelper.getRoomsByPreference(preferences);

        StringBuilder result = new StringBuilder();
        result.append("Matching Services:\n");
        if (servicesCursor != null) {
            while (servicesCursor.moveToNext()) {
                result.append(servicesCursor.getString(servicesCursor.getColumnIndex("Service_Name"))).append("\n");
            }
            servicesCursor.close();
        }

        result.append("\nMatching Attractions:\n");
        if (attractionsCursor != null) {
            while (attractionsCursor.moveToNext()) {
                result.append(attractionsCursor.getString(attractionsCursor.getColumnIndex("Attraction_Name"))).append("\n");
            }
            attractionsCursor.close();
        }

        result.append("\nMatching Rooms:\n");
        if (roomsCursor != null) {
            while (roomsCursor.moveToNext()) {
                result.append(roomsCursor.getString(roomsCursor.getColumnIndex("Room_Type"))).append("\n");
            }
            roomsCursor.close();
        }

        TextView preferencesResultsTextView = findViewById(R.id.preferencesResultsTextView);
        preferencesResultsTextView.setText(result.toString());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("SAVE_BUTTON_VISIBLE", saveProfileButton.getVisibility() == View.VISIBLE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isVisible = savedInstanceState.getBoolean("SAVE_BUTTON_VISIBLE", true);
            saveProfileButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }
}
