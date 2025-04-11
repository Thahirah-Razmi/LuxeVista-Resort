package com.example.luxevistaresort;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ServiceReservationActivity extends AppCompatActivity {

    private TextView textViewServiceName;
    private TextView textViewServiceDescription;
    private TextView textViewServicePrice;
    private CalendarView calendarView;
    private TimePicker timePicker;
    private Button buttonReserve;
    private DatabaseHelper dbHelper;

    private long selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_reservation);

        textViewServiceName = findViewById(R.id.textViewServiceName);
        textViewServiceDescription = findViewById(R.id.textViewServiceDescription);
        textViewServicePrice = findViewById(R.id.textViewServicePrice);
        calendarView = findViewById(R.id.calendarView);
        timePicker = findViewById(R.id.timePicker);
        buttonReserve = findViewById(R.id.buttonReserve);
        dbHelper = new DatabaseHelper(this);

        selectedDate = calendarView.getDate();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = new java.util.GregorianCalendar(year, month, dayOfMonth).getTimeInMillis();
        });

        String serviceName = getIntent().getStringExtra("SERVICE_NAME");
        String serviceDescription = getIntent().getStringExtra("SERVICE_DESCRIPTION");
        String servicePrice = getIntent().getStringExtra("SERVICE_PRICE");
        int serviceId = getIntent().getIntExtra("SERVICE_ID", -1);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int guestId = sharedPreferences.getInt("GUEST_ID", -1);

        if (serviceId == -1 || guestId == -1 || serviceName == null || serviceDescription == null || servicePrice == null) {
            Toast.makeText(this, "Invalid service or guest information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textViewServiceName.setText(serviceName);
        textViewServiceDescription.setText(serviceDescription);
        textViewServicePrice.setText(servicePrice);

        buttonReserve.setOnClickListener(v -> saveReservation(serviceId, guestId));
    }

    private void saveReservation(int serviceId, int guestId) {
        String reservationDate = android.text.format.DateFormat.format("yyyy-MM-dd", selectedDate).toString();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String reservationTime = String.format("%02d:%02d", hour, minute);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String insertReservationQuery = "INSERT INTO Reservations (Service_ID, Guest_ID, Reservation_Date, Reservation_Time, Status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            db.execSQL(insertReservationQuery, new Object[]{serviceId, guestId, reservationDate, reservationTime, "Confirmed"});
            Toast.makeText(this, "Reservation successful!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to reserve service", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }
}
