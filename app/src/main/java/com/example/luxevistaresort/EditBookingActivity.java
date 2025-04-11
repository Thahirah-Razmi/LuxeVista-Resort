package com.example.luxevistaresort;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditBookingActivity extends AppCompatActivity {

    private EditText editTextCheckInDate, editTextCheckOutDate;
    private Button buttonSave;
    private DatabaseHelper dbHelper;
    private Booking selectedBooking;
    private Calendar calendarCheckIn, calendarCheckOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking);

        editTextCheckInDate = findViewById(R.id.editTextCheckInDate);
        editTextCheckOutDate = findViewById(R.id.editTextCheckOutDate);
        buttonSave = findViewById(R.id.buttonSave);

        dbHelper = new DatabaseHelper(this);

        calendarCheckIn = Calendar.getInstance();
        calendarCheckOut = Calendar.getInstance();

        selectedBooking = getIntent().getParcelableExtra("selectedBooking");
        if (selectedBooking != null) {
            populateFields();
        } else {
            Toast.makeText(this, "Error loading booking details", Toast.LENGTH_SHORT).show();
            finish();
        }

        editTextCheckInDate.setOnClickListener(v -> showDatePicker(calendarCheckIn, editTextCheckInDate));
        editTextCheckOutDate.setOnClickListener(v -> showDatePicker(calendarCheckOut, editTextCheckOutDate));

        buttonSave.setOnClickListener(v -> saveBooking());
    }

    private void populateFields() {
        editTextCheckInDate.setText(selectedBooking.getCheckInDate());
        editTextCheckOutDate.setText(selectedBooking.getCheckOutDate());
    }

    private void showDatePicker(Calendar calendar, EditText targetEditText) {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            targetEditText.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveBooking() {
        String updatedCheckInDate = editTextCheckInDate.getText().toString().trim();
        String updatedCheckOutDate = editTextCheckOutDate.getText().toString().trim();

        if (updatedCheckInDate.isEmpty()) {
            Toast.makeText(this, "Please enter a Check-In Date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (updatedCheckOutDate.isEmpty()) {
            Toast.makeText(this, "Please enter a Check-Out Date", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            if (dateFormat.parse(updatedCheckInDate).after(dateFormat.parse(updatedCheckOutDate))) {
                Toast.makeText(this, "Check-Out date must be after Check-In date", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("Check_In_Date", updatedCheckInDate);
        values.put("Check_Out_Date", updatedCheckOutDate);

        boolean success = dbHelper.updateBooking(selectedBooking.getBookingId(), values);
        if (success) {
            selectedBooking.setCheckInDate(updatedCheckInDate);
            selectedBooking.setCheckOutDate(updatedCheckOutDate);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedBooking", selectedBooking);
            setResult(RESULT_OK, resultIntent);

            Toast.makeText(this, "Booking updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update booking", Toast.LENGTH_SHORT).show();
        }
    }
}
