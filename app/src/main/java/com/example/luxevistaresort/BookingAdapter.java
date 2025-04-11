package com.example.luxevistaresort;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final Context context;
    private final ArrayList<Booking> bookings;

    private OnItemClickListener listener;

    public BookingAdapter(Context context, ArrayList<Booking> bookings) {
        this.context = context;
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booking_list_item, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);

        holder.textViewBookingId.setText("Booking ID: " + booking.getBookingId());
        holder.textViewCheckInDate.setText("Check-In: " + booking.getCheckInDate());
        holder.textViewCheckOutDate.setText("Check-Out: " + booking.getCheckOutDate());
        holder.textViewTotalPrice.setText("Total: Rs. " + booking.getTotalPrice());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position, booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Booking booking);
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookingId, textViewCheckInDate, textViewCheckOutDate, textViewTotalPrice;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBookingId = itemView.findViewById(R.id.textViewBookingId);
            textViewCheckInDate = itemView.findViewById(R.id.textViewCheckInDate);
            textViewCheckOutDate = itemView.findViewById(R.id.textViewCheckOutDate);
            textViewTotalPrice = itemView.findViewById(R.id.textViewTotalPrice);
        }
    }
}
