package com.autodetectotplibrary;

public class Worker {
    OnReceiverListener onReceiverListener;

    void onEvent(){
        onReceiverListener.onReceived();
    }
}
