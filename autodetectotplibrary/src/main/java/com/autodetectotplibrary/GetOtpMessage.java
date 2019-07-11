package com.autodetectotplibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class GetOtpMessage {
    private Context context;

    private OnOtpReceivedListener otpReceivedListener;

    public GetOtpMessage(Context context, OnOtpReceivedListener otpReceivedListener) {
        this.context = context;
        this.otpReceivedListener = otpReceivedListener;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equalsIgnoreCase("otp")) {
                otpReceivedListener.onSuccess(intent.getStringExtra("message"));
            } else {
                otpReceivedListener.onFailure("Didn't receive OTP. Please try again after sometime");
            }
        }
    };

    public void startReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter("otp"));
    }

    public void stopReceiver() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    public interface OnOtpReceivedListener {
        void onSuccess(String otp);

        void onFailure(String message);
    }
}
