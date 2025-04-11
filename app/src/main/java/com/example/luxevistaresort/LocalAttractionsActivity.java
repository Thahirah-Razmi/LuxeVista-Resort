package com.example.luxevistaresort;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LocalAttractionsActivity extends AppCompatActivity {

    private ListView listViewLocalAttractions;
    private TextView textViewAttractions;
    private DatabaseHelper dbHelper;
    private ArrayList<String> attractionNames;
    private ArrayList<String> attractionDescriptions;
    private ArrayList<Integer> imageIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_attractions);

        listViewLocalAttractions = findViewById(R.id.listViewLocalAttractions);
        textViewAttractions = findViewById(R.id.textViewAttractions);
        dbHelper = new DatabaseHelper(this);

        loadAttractionsFromDatabase();

        CustomAdapter adapter = new CustomAdapter(this, attractionNames, attractionDescriptions, imageIds);
        listViewLocalAttractions.setAdapter(adapter);
    }

    private final int[] attractionImages = {
            R.drawable.attraction1,
            R.drawable.attraction2,
            R.drawable.attraction3,
            R.drawable.attraction4,
            R.drawable.attraction5,
            R.drawable.attraction6,
            R.drawable.attraction7,
            R.drawable.attraction8,
            R.drawable.attraction9,
            R.drawable.attraction10,
            R.drawable.attraction11,
            R.drawable.attraction12,
            R.drawable.attraction13,
            R.drawable.attraction14,
            R.drawable.attraction15,
    };

    private void loadAttractionsFromDatabase() {
        attractionNames = new ArrayList<>();
        attractionDescriptions = new ArrayList<>();
        imageIds = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Attraction_Name, Description FROM Attractions", null);

        int imageIndex = 0;
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("Attraction_Name"));
                String description = cursor.getString(cursor.getColumnIndex("Description"));

                attractionNames.add(name);
                attractionDescriptions.add(description);

                imageIds.add(attractionImages[imageIndex]);

                imageIndex = (imageIndex + 1) % attractionImages.length;

            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No attractions found", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }
}
