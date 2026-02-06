package com.nachiket.wsafety;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {

            Bundle bundle = intent.getExtras();

            if (bundle != null) {

                Object[] pdus = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format");

                if (pdus != null) {
                    for (Object pdu : pdus) {

                        SmsMessage msg =
                                SmsMessage.createFromPdu((byte[]) pdu, format);

                        if ("ALARM".equalsIgnoreCase(msg.getMessageBody())) {

                            MediaPlayer mp =
                                    MediaPlayer.create(context, R.raw.alarm_sound);

                            mp.setLooping(true);
                            mp.start();
                        }
                    }
                }
            }
        }
    }
}
