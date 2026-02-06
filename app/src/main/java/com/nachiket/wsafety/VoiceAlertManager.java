package com.nachiket.wsafety;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;
import java.util.List;

public class VoiceAlertManager {
    private Context context;
    private ContactsDatabaseHelper dbHelper;

    public VoiceAlertManager(Context context) {
        this.context = context;
        this.dbHelper = new ContactsDatabaseHelper(context); // Database Helper for contacts
    }

    public void triggerVoiceAlert() {
        List<String> emergencyContacts = dbHelper.getAllEmergencyContacts(); // Fetch all contacts

        if (emergencyContacts.isEmpty()) {
            Toast.makeText(context, "No emergency contacts set!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (String phoneNumber : emergencyContacts) {
                String message = "🚨 EMERGENCY ALERT! 🚨 Please check immediately. Reply with 'ALARM' to trigger an emergency sound.";
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            }
            Toast.makeText(context, "Emergency alert sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to send emergency alert!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
