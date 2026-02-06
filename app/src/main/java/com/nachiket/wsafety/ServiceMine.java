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

    private boolean isRunning = false;
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    String myLocation = "Unable to Find Location :(";
                    if (location != null) {
                        myLocation = "http://maps.google.com/maps?q=loc:" + location.getLatitude() + "," + location.getLongitude();
                    }
                    sendSOSMessage(myLocation);
                });
    }

    private void sendSOSMessage(String myLocation) {
        List<String> emergencyContacts = dbHelper.getAllEmergencyContacts();

        if (emergencyContacts.isEmpty()) {
            Toast.makeText(this, "No emergency contacts set!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String sosMessage = sharedPreferences.getString("SOS_MESSAGE", "Help! I am in danger!");

        SmsManager smsManager = SmsManager.getDefault();
        String finalMessage = sosMessage + " " + myLocation;

        for (String contact : emergencyContacts) {
            smsManager.sendTextMessage(contact, null, finalMessage, null, null);
        }

        Toast.makeText(this, "SOS message sent!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP".equalsIgnoreCase(intent.getAction())) {
            stopForeground(true);
            stopSelf();
        } else {
            startForegroundService();
        }

        return START_NOT_STICKY;
    }

    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MYID", "Women Safety", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(this, "MYID")
                    .setContentTitle("Women Safety")
                    .setContentText("Shake Device to Send SOS")
                    .setSmallIcon(R.drawable.girl_vector)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(115, notification);
            isRunning = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ShakeDetector.stop(); // Stop shake detection when service stops
    }
}