package com.example.luxevistaresort;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextEmail;
    private EditText editTextPhoneNumber;
    private Button buttonRegister;
    private TextView textViewError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbHelper = new DatabaseHelper(this);

        editTextName = findViewById(R.id.editTextName);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhoneNumber = findViewById(R.id.editTextPhone_Number);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewError = findViewById(R.id.textViewError);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUsername.getText().toString();
                String name = editTextName.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                String email = editTextEmail.getText().toString();
                String phoneNumber = editTextPhoneNumber.getText().toString();

                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
                    textViewError.setText("Please fill in all fields.");
                } else if (!password.equals(confirmPassword)) {
                    textViewError.setText("Passwords do not match.");
                } else if (!isValidPassword(password)) {
                    textViewError.setText("Password must be at least 8 characters long and include uppercase, lowercase, number, and special character.");
                } else if (!isValidEmail(email)) {
                    textViewError.setText("Invalid email address.");
                } else if (!isValidPhoneNumber(phoneNumber)) {
                    textViewError.setText("Invalid phone number.");
                } else {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("Name", name);
                    values.put("Username", username);
                    values.put("Password", password);
                    values.put("Email", email);
                    values.put("Phone_Number", phoneNumber);

                    long newRowId = db.insert("Guests", null, values);
                    db.close();

                    if (newRowId != -1) {
                        Toast.makeText(RegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        textViewError.setText("Registration failed.");
                    }
                }
            }
        });
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return Pattern.matches(passwordPattern, password);
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+$";
        return Pattern.matches(emailPattern, email);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phonePattern = "^[0-9]{10,15}$";
        return Pattern.matches(phonePattern, phoneNumber);
    }
}
