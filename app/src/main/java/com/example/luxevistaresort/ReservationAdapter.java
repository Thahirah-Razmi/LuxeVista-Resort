package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private final Context context;
    private final ArrayList<Reservation> reservations;
    private final DatabaseHelper dbHelper;

    public ReservationAdapter(Context context, ArrayList<Reservation> reservations) {
        this.context = context;
        this.reservations = reservations;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reservation_item, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        holder.textViewReservationId.setText("Reservation ID: " + reservation.getId());

        holder.serviceName.setText(reservation.getServiceName() != null ? reservation.getServiceName() : "No service name");
        holder.details.setText(String.format("%s at %s\nStatus: %s",
                reservation.getDate() != null ? reservation.getDate() : "No date",
                reservation.getTime() != null ? reservation.getTime() : "No time",
                reservation.getStatus() != null ? reservation.getStatus() : "No status"));

        holder.buttonCancelReservation.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Cancel Reservation")
                    .setMessage("Are you sure you want to cancel this reservation?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.execSQL("UPDATE Reservations SET Status = ? WHERE Reservation_ID = ?",
                                new Object[]{"Cancelled", reservation.getId()});
                        Toast.makeText(context, "Reservation cancelled", Toast.LENGTH_SHORT).show();

                        reservations.remove(position);
                        notifyItemRemoved(position);
                        db.close();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        });

        holder.buttonModifyReservation.setOnClickListener(v -> {
            Intent modifyIntent = new Intent(context, ModifyReservationActivity.class);
            modifyIntent.putExtra("RESERVATION_ID", reservation.getId());
            modifyIntent.putExtra("SERVICE_NAME", reservation.getServiceName());
            modifyIntent.putExtra("RESERVATION_DATE", reservation.getDate());
            modifyIntent.putExtra("RESERVATION_TIME", reservation.getTime());
            context.startActivity(modifyIntent);
        });
    }


    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {

        TextView textViewReservationId;
        TextView serviceName;
        TextView details;
        Button buttonModifyReservation;
        Button buttonCancelReservation;

        public ReservationViewHolder(View itemView) {
            super(itemView);
            textViewReservationId = itemView.findViewById(R.id.textViewReservationId);
            serviceName = itemView.findViewById(R.id.textViewReservationService);
            details = itemView.findViewById(R.id.textViewReservationDetails);
            buttonModifyReservation = itemView.findViewById(R.id.buttonModifyReservation);
            buttonCancelReservation = itemView.findViewById(R.id.buttonCancelReservation);
        }
    }
}
