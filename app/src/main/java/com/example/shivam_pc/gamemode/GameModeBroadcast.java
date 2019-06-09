package com.example.shivam_pc.gamemode;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class GameModeBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        SharedPreferences SavedSettings = context.getSharedPreferences("SavedSettings",Context.MODE_PRIVATE);

        NotificationManager mNotificationManager  = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
            // Permission is not granted
            Toast.makeText(context, "Please allow Game Mode do not disturb access", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(context, "Stopping Game Mode", Toast.LENGTH_SHORT);
            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            SavedSettings.edit().putBoolean("BoostStatus",false).apply();
            mNotificationManager.cancelAll();
        }
    }
}
