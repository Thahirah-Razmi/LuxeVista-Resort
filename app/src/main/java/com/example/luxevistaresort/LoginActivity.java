package com.example.luxevistaresort;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewError = findViewById(R.id.textViewError);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    textViewError.setText("Please fill in all fields.");
                } else if (checkUserCredentials(username, password)) {
                    int guestId = getGuestId(username);
                    if (guestId != -1) {
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("GUEST_ID", guestId);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, HomePage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        textViewError.setText("Error retrieving user details.");
                    }
                } else {
                    textViewError.setText("Login failed. Invalid credentials.");
                }
            }
        });
    }

    private boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "Username = ? AND Password = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query("Guests", null, selection, selectionArgs, null, null, null);
        boolean credentialsMatch = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return credentialsMatch;
    }

    private int getGuestId(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"Guest_ID"};
        String selection = "Username = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query("Guests", columns, selection, selectionArgs, null, null, null);
        int guestId = -1;
        if (cursor.moveToFirst()) {
            guestId = cursor.getInt(cursor.getColumnIndex("Guest_ID"));
        }
        cursor.close();
        db.close();
        return guestId;
    }
}
