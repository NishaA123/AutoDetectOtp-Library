package com.autodetectotplibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class OtpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (Object o : pdusObj) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) o);

                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody().split("OTP is")[1];
                    String message1 = message.split("for")[0];

                    message1 = message1.substring(0, message1.length() - 1);
                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message1);

                    Intent myIntent = new Intent("otp");
                    myIntent.putExtra("message", message1);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }
}
