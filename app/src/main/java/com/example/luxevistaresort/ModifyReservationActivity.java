package com.example.luxevistaresort;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ModifyReservationActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TimePicker timePicker;
    private Button buttonSaveChanges;
    private DatabaseHelper dbHelper;
    private TextView textViewSelectedDate;
    private TextView textViewModifyServiceName;

    private long selectedDate;
    private int reservationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_reservation);

        calendarView = findViewById(R.id.calendarViewModify);
        timePicker = findViewById(R.id.timePickerModify);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        dbHelper = new DatabaseHelper(this);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        textViewModifyServiceName = findViewById(R.id.textViewModifyServiceName);

        reservationId = getIntent().getIntExtra("RESERVATION_ID", -1);
        String currentDate = getIntent().getStringExtra("RESERVATION_DATE");

        if (reservationId == -1) {
            Toast.makeText(this, "Invalid reservation", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (currentDate != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = dateFormat.parse(currentDate);
                calendarView.setDate(date != null ? date.getTime() : System.currentTimeMillis());
                selectedDate = date != null ? date.getTime() : System.currentTimeMillis();
                textViewSelectedDate.setText("Current Date: " + currentDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        loadServiceName();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = new java.util.GregorianCalendar(year, month, dayOfMonth).getTimeInMillis();
            String selectedDateString = android.text.format.DateFormat.format("yyyy-MM-dd", selectedDate).toString();
            textViewSelectedDate.setText("Current Date: " + selectedDateString);
        });

        buttonSaveChanges.setOnClickListener(v -> updateReservation());
    }

    private void loadServiceName() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT S.Service_Name " +
                "FROM Reservations R " +
                "JOIN Services S ON R.Service_ID = S.Service_ID " +
                "WHERE R.Reservation_ID = ?", new String[]{String.valueOf(reservationId)});

        if (cursor.moveToFirst()) {
            String serviceName = cursor.getString(cursor.getColumnIndexOrThrow("Service_Name"));
            textViewModifyServiceName.setText("Service Name: " + serviceName);
        } else {
            textViewModifyServiceName.setText("Service Name: Not found");
        }

        cursor.close();
        db.close();
    }

    private void updateReservation() {
        long currentTime = System.currentTimeMillis();
        if (selectedDate < currentTime) {
            Toast.makeText(this, "Cannot select a past date", Toast.LENGTH_SHORT).show();
            return;
        }

        String newDate = android.text.format.DateFormat.format("yyyy-MM-dd", selectedDate).toString();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String newTime = String.format("%02d:%02d", hour, minute);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Reservation_Date", newDate);
        values.put("Reservation_Time", newTime);

        int rows = db.update("Reservations", values, "Reservation_ID = ?", new String[]{String.valueOf(reservationId)});
        db.close();

        if (rows > 0) {
            Toast.makeText(this, "Reservation updated successfully!", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("UPDATED_DATE", newDate);
            resultIntent.putExtra("UPDATED_TIME", newTime);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Failed to update reservation", Toast.LENGTH_SHORT).show();
        }
    }
}
