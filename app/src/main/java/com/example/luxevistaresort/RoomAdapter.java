package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private Context context;
    private ArrayList<Room> roomsList;

    private final int[] roomImages = {
            R.drawable.room_image_1,
            R.drawable.room_image_2,
            R.drawable.room_image_3,
            R.drawable.room_image_4,
            R.drawable.room_image_5,
            R.drawable.room_image_6,
            R.drawable.room_image_7,
            R.drawable.room_image_8,
            R.drawable.room_image_9,
            R.drawable.room_image_10,
            R.drawable.room_image_11,
            R.drawable.room_image_12,
            R.drawable.room_image_13,
            R.drawable.room_image_14,
            R.drawable.room_image_15,
    };

    public RoomAdapter(Context context, ArrayList<Room> roomsList) {
        this.context = context;
        this.roomsList = roomsList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_list_item, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomsList.get(position);

        holder.textViewRoomType.setText(room.getRoomType());
        holder.textViewRoomDescription.setText(room.getDescription());
        holder.textViewRoomPrice.setText("Price Per Night: Rs. " + room.getPricePerNight());

        holder.imageViewRoom.setImageResource(roomImages[position % roomImages.length]);

        holder.buttonBookRoom.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomBookingDetailsActivity.class);
            intent.putExtra("ROOM_ID", room.getRoomId());
            intent.putExtra("ROOM_TYPE", room.getRoomType());
            intent.putExtra("ROOM_DESCRIPTION", room.getDescription());
            intent.putExtra("ROOM_PRICE", room.getPricePerNight());
            intent.putExtra("ROOM_AVAILABILITY", room.getAvailability());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewRoom;
        TextView textViewRoomType, textViewRoomDescription, textViewRoomPrice;
        Button buttonBookRoom;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewRoom = itemView.findViewById(R.id.imageViewRoom);
            textViewRoomType = itemView.findViewById(R.id.textViewRoomType);
            textViewRoomDescription = itemView.findViewById(R.id.textViewRoomDescription);
            textViewRoomPrice = itemView.findViewById(R.id.textViewRoomPrice);
            buttonBookRoom = itemView.findViewById(R.id.buttonBookRoom);
        }
    }
}
