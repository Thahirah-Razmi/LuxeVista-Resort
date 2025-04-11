package com.example.luxevistaresort;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {

    private Button buttonProfile;
    private Button buttonRoomBooking;
    private Button buttonManageBookings;
    private Button buttonServices;
    private Button buttonManageReservations;
    private Button buttonLocalAttractions;
    private Button buttonOffers;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        buttonProfile = findViewById(R.id.buttonProfile);
        buttonRoomBooking = findViewById(R.id.buttonRoomBooking);
        buttonManageBookings = findViewById(R.id.buttonManageBookings);
        buttonServices = findViewById(R.id.buttonServices);
        buttonManageReservations = findViewById(R.id.buttonManageReservations);
        buttonLocalAttractions = findViewById(R.id.buttonLocalAttractions);
        buttonOffers = findViewById(R.id.buttonOffers);
        buttonLogout = findViewById(R.id.buttonLogout);

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, ProfileActivity.class);
                intent.putExtra("guestId", -1);
                startActivity(intent);
            }
        });

        buttonRoomBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, BrowseRoomsActivity.class);
                startActivity(intent);
            }
        });

        buttonManageBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, ManageBookingsActivity.class);
                startActivity(intent);
            }
        });

        buttonServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, ServicesActivity.class);
                startActivity(intent);
            }
        });

        buttonManageReservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, ManageReservationsActivity.class);
                startActivity(intent);
            }
        });

        buttonLocalAttractions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, LocalAttractionsActivity.class);
                startActivity(intent);
            }
        });

        buttonOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, OffersActivity.class);
                startActivity(intent);
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
