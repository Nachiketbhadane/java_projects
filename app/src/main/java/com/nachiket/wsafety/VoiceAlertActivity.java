package com.nachiket.wsafety;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.List;

public class VoiceAlertActivity extends AppCompatActivity {

    private ContactsDatabaseHelper dbHelper;
    private MediaPlayer mediaPlayer;
    private static final int SMS_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_msg_ringer);

        dbHelper = new ContactsDatabaseHelper(this);

        Button triggerButton = findViewById(R.id.btnTriggerVoiceAlert);
        Button stopButton = findViewById(R.id.btnStopAlarm);

        triggerButton.setOnClickListener(view -> sendAlarmSMS());
        stopButton.setOnClickListener(view -> stopAlarm());

        // If activity started from an "ALARM" SMS, trigger alarm
        if (getIntent() != null && getIntent().getBooleanExtra("from_sms", false)) {
            playAlarm();
        }
    }

    private void sendAlarmSMS() {
        List<String> emergencyContacts = dbHelper.getAllEmergencyContacts();

        if (emergencyContacts.isEmpty()) {
            Toast.makeText(this, "No Emergency Contacts Set!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST);
        } else {
            sendSMSToAllContacts(emergencyContacts);
        }
    }

    private void sendSMSToAllContacts(List<String> contacts) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (String phoneNumber : contacts) {
                smsManager.sendTextMessage(phoneNumber, null, "ALARM", null, null);
            }
            Toast.makeText(this, "ALARM Messages Sent!", Toast.LENGTH_SHORT).show();

            // Play alarm after sending messages
            playAlarm();

        } catch (Exception e) {
            Toast.makeText(this, "Failed to send ALARM message!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendAlarmSMS();
            } else {
                Toast.makeText(this, "SMS Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playAlarm() {
        stopAlarm(); // Stop any existing alarm before playing a new one

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    private void stopAlarm() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }
}