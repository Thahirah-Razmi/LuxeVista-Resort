package com.example.luxevistaresort;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> attractionNames;
    private final ArrayList<String> attractionDescriptions;
    private final ArrayList<Integer> imageIds;

    public CustomAdapter(Context context, ArrayList<String> names, ArrayList<String> descriptions, ArrayList<Integer> imageIds) {
        super(context, R.layout.list_local_attractions, names);
        this.context = context;
        this.attractionNames = names;
        this.attractionDescriptions = descriptions;
        this.imageIds = imageIds;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_local_attractions, parent, false);
        }

        TextView nameText = convertView.findViewById(R.id.textViewAttractionName);
        TextView descriptionText = convertView.findViewById(R.id.textViewAttractionDescription);
        ImageView imageView = convertView.findViewById(R.id.imageViewAttraction);

        nameText.setText(attractionNames.get(position));
        descriptionText.setText(attractionDescriptions.get(position));
        imageView.setImageResource(imageIds.get(position));

        return convertView;
    }
}

