package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ServiceAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> serviceNames;
    private final ArrayList<String> serviceDescriptions;
    private final ArrayList<String> servicePrices;
    private final ArrayList<Integer> imageIds;
    private final ArrayList<Integer> serviceIds;

    public ServiceAdapter(Context context, ArrayList<String> names, ArrayList<String> descriptions,
                          ArrayList<String> prices, ArrayList<Integer> images, ArrayList<Integer> ids) {
        super(context, R.layout.service_item, names);
        this.context = context;
        this.serviceNames = names;
        this.serviceDescriptions = descriptions;
        this.servicePrices = prices;
        this.imageIds = images;
        this.serviceIds = ids;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.service_item, parent, false);
        }

        TextView nameText = convertView.findViewById(R.id.textViewServiceName);
        TextView descriptionText = convertView.findViewById(R.id.textViewServiceDescription);
        TextView priceText = convertView.findViewById(R.id.textViewServicePrice);
        ImageView imageView = convertView.findViewById(R.id.imageViewService);
        Button reserveButton = convertView.findViewById(R.id.buttonReserve);

        nameText.setText(serviceNames.get(position));
        descriptionText.setText(serviceDescriptions.get(position));
        priceText.setText(servicePrices.get(position));
        imageView.setImageResource(imageIds.get(position));

        reserveButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceReservationActivity.class);
            intent.putExtra("SERVICE_NAME", serviceNames.get(position));
            intent.putExtra("SERVICE_DESCRIPTION", serviceDescriptions.get(position));
            intent.putExtra("SERVICE_PRICE", servicePrices.get(position));
            intent.putExtra("SERVICE_ID", serviceIds.get(position));
            context.startActivity(intent);
        });

        return convertView;
    }
}
