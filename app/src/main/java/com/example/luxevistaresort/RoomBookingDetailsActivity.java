package com.example.luxevistaresort;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import android.content.Context;
import androidx.core.app.NotificationCompat;

public class RoomBookingDetailsActivity extends AppCompatActivity {

    private TextView textViewRoomType, textViewRoomDescription, textViewRoomPrice, textViewRoomAvailability;
    private TextView textViewCheckInDate, textViewCheckOutDate;
    private Button buttonPickCheckInDate, buttonPickCheckOutDate, buttonConfirmBooking;
    private String roomType, roomDescription, roomAvailability;
    private int roomId, roomPricePerNight;
    private String checkInDate, checkOutDate;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking_details);

        createNotificationChannel();

        textViewRoomType = findViewById(R.id.textViewRoomType);
        textViewRoomDescription = findViewById(R.id.textViewRoomDescription);
        textViewRoomPrice = findViewById(R.id.textViewRoomPrice);
        textViewRoomAvailability = findViewById(R.id.textViewRoomAvailability);
        textViewCheckInDate = findViewById(R.id.textViewCheckInDate);
        textViewCheckOutDate = findViewById(R.id.textViewCheckOutDate);
        buttonPickCheckInDate = findViewById(R.id.buttonPickCheckInDate);
        buttonPickCheckOutDate = findViewById(R.id.buttonPickCheckOutDate);
        buttonConfirmBooking = findViewById(R.id.buttonConfirmBooking);

        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        roomId = intent.getIntExtra("ROOM_ID", -1);
        roomType = intent.getStringExtra("ROOM_TYPE");
        roomDescription = intent.getStringExtra("ROOM_DESCRIPTION");
        roomPricePerNight = intent.getIntExtra("ROOM_PRICE", 0);
        roomAvailability = intent.getStringExtra("ROOM_AVAILABILITY");

        if (roomId == -1 || roomType == null || roomDescription == null || roomAvailability == null) {
            Toast.makeText(this, "Room details not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        textViewRoomType.setText(roomType);
        textViewRoomDescription.setText(roomDescription);
        textViewRoomPrice.setText("Price Per Night: Rs. " + roomPricePerNight);
        textViewRoomAvailability.setText("Availability: " + roomAvailability);

        buttonPickCheckInDate.setOnClickListener(v -> showDatePickerDialog(date -> {
            checkInDate = date;
            textViewCheckInDate.setText("Check-in: " + checkInDate);
        }));

        buttonPickCheckOutDate.setOnClickListener(v -> showDatePickerDialog(date -> {
            checkOutDate = date;
            textViewCheckOutDate.setText("Check-out: " + checkOutDate);
        }));

        buttonConfirmBooking.setOnClickListener(v -> {
            if (checkInDate == null || checkOutDate == null) {
                Toast.makeText(this, "Please select check-in and check-out dates", Toast.LENGTH_SHORT).show();
            } else if (!isCheckOutDateValid()) {
                Toast.makeText(this, "Check-out date must be after check-in date", Toast.LENGTH_SHORT).show();
            } else {
                int totalPrice = calculateTotalPrice(checkInDate, checkOutDate, roomPricePerNight);

                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                int guestId = sharedPreferences.getInt("GUEST_ID", -1);

                boolean success = dbHelper.addBooking(guestId, roomId, checkInDate, checkOutDate, totalPrice, roomDescription);

                if (success) {
                    Toast.makeText(this, "Booking confirmed for " + roomType, Toast.LENGTH_SHORT).show();

                    sendBookingNotification(roomType, checkInDate, checkOutDate);

                } else {
                    Toast.makeText(this, "Failed to confirm booking", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int calculateTotalPrice(String checkInDate, String checkOutDate, int pricePerNight) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date checkIn = sdf.parse(checkInDate);
            Date checkOut = sdf.parse(checkOutDate);
            if (checkIn != null && checkOut != null) {
                long diff = checkOut.getTime() - checkIn.getTime();
                long nights = diff / (1000 * 60 * 60 * 24);
                return (int) (nights * pricePerNight);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean isCheckOutDateValid() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date checkIn = sdf.parse(checkInDate);
            Date checkOut = sdf.parse(checkOutDate);
            return checkOut != null && checkOut.after(checkIn);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showDatePickerDialog(DatePickedCallback callback) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    callback.onDatePicked(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    interface DatePickedCallback {
        void onDatePicked(String date);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "booking_notifications";
            String channelName = "Booking Notifications";
            String channelDescription = "Notifications for confirmed bookings";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void sendBookingNotification(String roomType, String checkInDate, String checkOutDate) {
        String channelId = "booking_notifications";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Booking Confirmed")
                .setContentText("Your booking for " + roomType + " from " + checkInDate + " to " + checkOutDate + " is confirmed.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}
