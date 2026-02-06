package com.nachiket.wsafety;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ContactsDatabaseHelper contactDatabaseHelper;
    private MediaPlayer mediaPlayer;
    private TextView textView;

    private final ActivityResultLauncher<String[]> multiplePermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    boolean allPermissionsGranted = true;
                    for (Boolean granted : result.values()) {
                        if (!granted) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }
                    if (!allPermissionsGranted) {
                        Snackbar.make(findViewById(android.R.id.content),
                                "Some permissions are missing!",
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    protected void onResume() {
        super.onResume();
        contactDatabaseHelper = new ContactsDatabaseHelper(this);
        updateContactText();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textNum);
        contactDatabaseHelper = new ContactsDatabaseHelper(this);

        requestPermissions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        updateContactText();
    }

    private void requestPermissions() {
        multiplePermissions.launch(new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MYID", "CHANNELFOREGROUND", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    private void updateContactText() {
        List<String> contactList = contactDatabaseHelper.getAllEmergencyContacts();
        if (contactList.isEmpty()) {
            textView.setText("No Emergency Contacts Set!");
        } else {
            textView.setText("SOS Will Be Sent To:\n" + String.join(", ", contactList));
        }
    }

    public void sendVoiceAlert(View view) {
        List<String> contacts = contactDatabaseHelper.getAllEmergencyContacts();

        if (contacts.isEmpty()) {
            Snackbar.make(view, "No Emergency Contacts Set!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            multiplePermissions.launch(new String[]{Manifest.permission.SEND_SMS});
            return;
        }

        sendSOSMessages(contacts);
        playAlarmSound();
    }

    private void sendSOSMessages(List<String> contacts) {
        SmsManager smsManager = SmsManager.getDefault();
        for (String contact : contacts) {
            smsManager.sendTextMessage(contact, null, "EMERGENCY ALERT: Immediate help needed!", null, null);
        }
        Snackbar.make(findViewById(android.R.id.content), "SOS Alert Sent to All Contacts!", Snackbar.LENGTH_SHORT).show();
    }

    private void playAlarmSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void startServiceV(View view) {
        if (!hasRequiredPermissions()) {
            requestPermissions();
            return;
        }

        Intent notificationIntent = new Intent(this, ServiceMine.class);
        notificationIntent.setAction("Start");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(notificationIntent);
            Snackbar.make(findViewById(android.R.id.content), "Service Started!", Snackbar.LENGTH_LONG).show();
        }
    }

    public void stopService(View view) {
        Intent notificationIntent = new Intent(this, ServiceMine.class);
        notificationIntent.setAction("stop");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(notificationIntent);
            Snackbar.make(findViewById(android.R.id.content), "Service Stopped!", Snackbar.LENGTH_LONG).show();
        }
    }

        public void PopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.add_contact:
                    intent = new Intent(MainActivity.this, RegisterNumberActivity.class);
                    break;
                case R.id.edit_sos:
                    intent = new Intent(MainActivity.this, EditSosActivity.class);
                    break;
                case R.id.view_contacts:
                    // ✅ FIX: Launch ViewContactsActivity instead of ContactsActivity
                    intent = new Intent(MainActivity.this, ViewContactsActivity.class);
                    break;
                case R.id.voice_alert:
                    intent = new Intent(MainActivity.this, VoiceAlertActivity.class);
                    break;
                default:
                    return false;
            }
            if (intent != null) startActivity(intent);
            return true;
        });
        popupMenu.show();
    }


    private boolean hasRequiredPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
