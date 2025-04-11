package com.example.luxevistaresort;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ManageBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBookings;
    private BookingAdapter bookingAdapter;
    private DatabaseHelper dbHelper;
    private int guestId;

    private static final int EDIT_BOOKING_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bookings);

        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);

        guestId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("GUEST_ID", -1);

        if (guestId != -1) {
            loadBookings();
        } else {
            Toast.makeText(this, "Guest ID is invalid", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBookings() {
        ArrayList<Booking> bookingsList = dbHelper.getGuestBookings(guestId);
        if (bookingsList.isEmpty()) {
            Toast.makeText(this, "No bookings found.", Toast.LENGTH_SHORT).show();
        } else {
            bookingAdapter = new BookingAdapter(this, bookingsList);
            recyclerViewBookings.setAdapter(bookingAdapter);

            bookingAdapter.setOnItemClickListener((position, booking) -> showOptionsDialog(booking));
        }
    }

    private void showOptionsDialog(Booking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Booking")
                .setMessage("Choose an action for Booking ID: " + booking.getBookingId())
                .setPositiveButton("Edit", (dialog, which) -> {
                    Intent intent = new Intent(this, EditBookingActivity.class);
                    intent.putExtra("selectedBooking", booking);
                    startActivityForResult(intent, EDIT_BOOKING_REQUEST);
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    confirmDeleteBooking(booking);
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void confirmDeleteBooking(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this booking?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteBooking(booking.getBookingId());
                    if (deleted) {
                        Toast.makeText(this, "Booking deleted successfully.", Toast.LENGTH_SHORT).show();
                        loadBookings();
                    } else {
                        Toast.makeText(this, "Failed to delete booking.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_BOOKING_REQUEST && resultCode == RESULT_OK) {
            loadBookings();
        }
    }
}
