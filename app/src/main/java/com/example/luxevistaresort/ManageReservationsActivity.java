package com.example.luxevistaresort;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ManageReservationsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewReservations;
    private DatabaseHelper dbHelper;
    private ArrayList<Reservation> reservations;
    private ReservationAdapter reservationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reservations);

        recyclerViewReservations = findViewById(R.id.recyclerViewReservations);
        dbHelper = new DatabaseHelper(this);

        loadReservations();

        reservationAdapter = new ReservationAdapter(this, reservations);
        recyclerViewReservations.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReservations.setAdapter(reservationAdapter);
    }

    private void loadReservations() {
        reservations = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int guestId = sharedPreferences.getInt("GUEST_ID", -1);

        if (guestId == -1) {
            Toast.makeText(this, "Guest not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT R.Reservation_ID, R.Reservation_Date, R.Reservation_Time, R.Status, " +
                "S.Service_Name " +
                "FROM Reservations R " +
                "JOIN Services S ON R.Service_ID = S.Service_ID " +
                "WHERE R.Guest_ID = ?", new String[]{String.valueOf(guestId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("Reservation_ID"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("Reservation_Date"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("Reservation_Time"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("Status"));
                String serviceName = cursor.getString(cursor.getColumnIndexOrThrow("Service_Name"));

                reservations.add(new Reservation(id, serviceName, date, time, status));
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No reservations found", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }
}
