package ru.payts.weatherforecastx;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class StatesMessageReceiver extends BroadcastReceiver {
    private static final String TAG = "StatesMessageReceiver";
    private int messageId = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = true;
        String message = "";

        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_BATTERY_CHANGED:
            case Intent.ACTION_BATTERY_OKAY:
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float batteryPct = level / (float) scale;
                if (batteryPct < 0.2) {
                    message = "BATTERY < 20%";
                }
                Log.d(TAG, "Battery Level:" + Float.toString(batteryPct));
                break;
            case Intent.ACTION_BATTERY_LOW:
                message = "VERY LOW BATTERY!!!";
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                //should check null because in airplane mode it will be null
                if (netInfo != null && netInfo.isConnected()) {
                    //Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                    isConnected = true;
                } else {
                    //Toast.makeText(context, "Not Connected", Toast.LENGTH_LONG).show();
                    isConnected = false;
                    message = "Not Connected!";
                }
                break;
        }
        if (!message.equals("")) {
            // создать нотификацию
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "2")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Notification!")
                    .setContentText(message);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(messageId++, builder.build());
        }
    }


}
