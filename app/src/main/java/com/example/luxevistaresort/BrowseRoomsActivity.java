package com.example.luxevistaresort;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BrowseRoomsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRooms;
    private RoomAdapter roomAdapter;
    private DatabaseHelper dbHelper;
    private Spinner spinnerRoomType;
    private SeekBar seekBarPrice;
    private TextView textViewPriceDisplay;
    private CheckBox checkBoxAvailability;
    private Button buttonApplyFilters;

    private static final int DEFAULT_PRICE = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_rooms);

        recyclerViewRooms = findViewById(R.id.recyclerViewRooms);
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(this));

        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        seekBarPrice = findViewById(R.id.seekBarPrice);
        textViewPriceDisplay = findViewById(R.id.textViewPriceDisplay);
        checkBoxAvailability = findViewById(R.id.checkBoxAvailability);
        buttonApplyFilters = findViewById(R.id.buttonApplyFilters);

        dbHelper = new DatabaseHelper(this);

        seekBarPrice.setMax(150000);
        seekBarPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewPriceDisplay.setText("Max Price: Rs. " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        ArrayList<String> roomTypes = new ArrayList<>();
        roomTypes.add("Single");
        roomTypes.add("Double");
        roomTypes.add("Suite");
        roomTypes.add("Penthouse");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoomType.setAdapter(adapter);

        loadRooms(null, DEFAULT_PRICE, false);

        buttonApplyFilters.setOnClickListener(v -> applyFilters());
    }

    private void loadRooms(String roomType, int maxPrice, boolean availability) {
        ArrayList<Room> roomsList = dbHelper.getFilteredRooms(roomType, maxPrice, availability);
        if (roomsList.isEmpty()) {
            Toast.makeText(this, "No available rooms based on your criteria.", Toast.LENGTH_SHORT).show();
        } else {
            roomAdapter = new RoomAdapter(this, roomsList);
            recyclerViewRooms.setAdapter(roomAdapter);
        }
    }

    private void applyFilters() {
        String roomType = spinnerRoomType.getSelectedItem().toString();
        int maxPrice = seekBarPrice.getProgress();
        boolean availability = checkBoxAvailability.isChecked();

        loadRooms(roomType, maxPrice, availability);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
