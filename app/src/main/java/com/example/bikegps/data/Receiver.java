package com.example.bikegps.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bikegps.R;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       Log.d("BroadcastReceiver",intent.getStringExtra(context.getResources().getString(R.string.intent_notification_extra))+"");
    }
}
