package com.example.luxevistaresort;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class OffersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        TextView textViewOfferTitle = findViewById(R.id.textViewOfferTitle);
        textViewOfferTitle.setText("Exclusive Offers at LuxeVista!");

        NotificationUtils.createNotificationChannel(this);

        Button btnSendPromotionNotification = findViewById(R.id.btnSendPromotionNotification);
        btnSendPromotionNotification.setOnClickListener(v -> sendPromotionNotification());

    }

    private void sendPromotionNotification() {
        Intent intent = new Intent(this, OffersActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText("Get 20% off on spa services, complimentary breakfast, and more! Visit LuxeVista Resort today.")
                .setBigContentTitle("Special Promotion!")
                .setSummaryText("Exclusive Offers");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationUtils.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Special Promotion!")
                .setContentText("Check out our latest offers, discounts and promotions at LuxeVista Resort!")
                .setStyle(bigTextStyle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}
