import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.nachiket.wsafety.R;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the action is the right one (SMS received)
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                // Iterate through all received SMS
                for (Object pdu : pdus) {
                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
                    String msg = message.getMessageBody();

                    // Check if the message is the trigger word "ALARM_TRIGGER"
                    if (msg.equalsIgnoreCase("ALARM_TRIGGER")) {
                        // Trigger the alarm sound
                        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound);
                        mediaPlayer.start();

                        // Optional: Add a listener to stop the alarm when it's done playing
                        mediaPlayer.setOnCompletionListener(mp -> mp.release());
                    }
                }
            }
        }
    }
}
