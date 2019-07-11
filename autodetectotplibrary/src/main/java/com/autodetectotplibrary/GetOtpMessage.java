package com.autodetectotplibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class GetOtpMessage implements OnReceiverListener {
    private Context context;

    private String otp = "";

    public GetOtpMessage(Context context) {
        this.context = context;
    }

    private BroadcastReceiver receiver;

    public void startReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter("otp"));
    }

    public void stopReceiver() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    public String getOtp() {
        return otp;
    }

    @Override
    public void onReceived() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase("otp")) {
                    otp = intent.getStringExtra("message");
                }
            }
        };
    }
}
