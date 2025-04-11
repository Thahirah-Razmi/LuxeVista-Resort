package com.example.luxevistaresort;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ServicesActivity extends AppCompatActivity {

    private ListView listViewServices;
    private DatabaseHelper dbHelper;
    private ArrayList<String> serviceNames;
    private ArrayList<String> serviceDescriptions;
    private ArrayList<String> servicePrices;
    private ArrayList<Integer> imageIds;
    private ArrayList<Integer> serviceIds;

    private final int[] serviceImages = {
            R.drawable.service_image_1,
            R.drawable.service_image_2,
            R.drawable.service_image_3,
            R.drawable.service_image_4,
            R.drawable.service_image_5,
            R.drawable.service_image_6,
            R.drawable.service_image_7,
            R.drawable.service_image_8,
            R.drawable.service_image_9,
            R.drawable.service_image_10,
            R.drawable.service_image_11,
            R.drawable.service_image_12,
            R.drawable.service_image_13,
            R.drawable.service_image_14,
            R.drawable.service_image_15,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        listViewServices = findViewById(R.id.listViewServices);
        dbHelper = new DatabaseHelper(this);

        loadServicesFromDatabase();

        ServiceAdapter adapter = new ServiceAdapter(this, serviceNames, serviceDescriptions, servicePrices, imageIds, serviceIds);
        listViewServices.setAdapter(adapter);
    }

    private void loadServicesFromDatabase() {
        serviceNames = new ArrayList<>();
        serviceDescriptions = new ArrayList<>();
        servicePrices = new ArrayList<>();
        imageIds = new ArrayList<>();
        serviceIds = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Service_ID, Service_Name, Description, Price FROM Services", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("Service_ID"));
                String name = cursor.getString(cursor.getColumnIndex("Service_Name"));
                String description = cursor.getString(cursor.getColumnIndex("Description"));
                String price = cursor.getString(cursor.getColumnIndex("Price"));

                serviceIds.add(id);
                serviceNames.add(name);
                serviceDescriptions.add(description);
                servicePrices.add("Rs." + price);

                imageIds.add(serviceImages[serviceNames.size() - 1 % serviceImages.length]);

            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No services available", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }
}
