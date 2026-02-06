package com.nachiket.wsafety;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class ServiceMine extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private ContactsDatabaseHelper dbHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new ContactsDatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ShakeDetector.create(this, this::getLastKnownLocationAndSendSOS);
    }

    private void getLastKnownLocationAndSendSOS() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            String loc = "Location not found";
            if (location != null) {
                loc = "http://maps.google.com/maps?q=loc:" +
                        location.getLatitude() + "," + location.getLongitude();
            }

            sendSOSMessage(loc);
        });
    }

    private void sendSOSMessage(String location) {

        List<String> contacts = dbHelper.getAllEmergencyContacts();

        if (contacts.isEmpty()) {
            Toast.makeText(this, "No emergency contacts!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        // ✅ SAME KEY AS EditSosActivity
        String sos = prefs.getString("CUSTOM_MSG", "Help! I am in danger!");

        SmsManager smsManager = SmsManager.getDefault();
        String finalMsg = sos + " " + location;

        for (String c : contacts) {
            smsManager.sendTextMessage(c, null, finalMsg, null, null);
        }

        Toast.makeText(this, "SOS Sent!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        return START_NOT_STICKY;
    }

    private void startForegroundService() {

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_IMMUTABLE);

        NotificationChannel channel =
                new NotificationChannel("MYID", "Women Safety", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.createNotificationChannel(channel);

        Notification notification = new Notification.Builder(this, "MYID")
                .setContentTitle("Women Safety")
                .setContentText("Shake phone to send SOS")
                .setSmallIcon(R.drawable.girl_vector)
                .setContentIntent(pi)
                .build();

        startForeground(101, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ShakeDetector.stop();
    }
}
