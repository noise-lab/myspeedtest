package com.num.controller.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.net.Uri;

import com.num.Constants;
import com.num.controller.managers.DataUsageManager;
import com.num.controller.managers.DnsManager;
import com.num.controller.managers.MeasurementManager;
import com.num.controller.utils.DataUsageUtil;

import java.util.Calendar;
import java.util.TimeZone;


public class MeasurementAlarmReceiver extends BroadcastReceiver {

    final static String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wakeLock.acquire();

            DataUsageManager dataUsageManager = new DataUsageManager(new Handler());
            dataUsageManager.execute(context);

            // run only when background_service is set to run (true)
            switch(intent.getIntExtra("repeating", 0))
            {
                case Constants.UPDATE_INTERVAL:
                    Log.d(TAG, "Background Interval Update");
                    SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                    if (prefs.getBoolean("accept_conditions", false) && prefs.getBoolean("background_service", false)) {
                        MeasurementManager manager = new MeasurementManager(context);
                        manager.execute();
                        try {
                            Thread.sleep(Constants.SHORT_SLEEP_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Constants.DAILY_INTERVAL:
                    Log.d(TAG, "Background Daily Update");
                    //DnsManager dnsManager=new DnsManager(context);
                    //dnsManager.execute();
                    break;
                default:

            }

            wakeLock.release();
        }catch(Exception e){
            Log.d(TAG, "onReceive");
        }
    }


    public void setAlarm(Context context) {
        AlarmManager manager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("update", Uri.parse("interval"), context, MeasurementAlarmReceiver.class);
        intent.putExtra("repeating", Constants.UPDATE_INTERVAL);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.UPDATE_INTERVAL, pendingIntent);
    }


    public void setAlarmDaily(Context context) {
        AlarmManager manager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("update", Uri.parse("daily"), context, MeasurementAlarmReceiver.class);
        intent.putExtra("repeating", Constants.DAILY_INTERVAL);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar updateTime = Calendar.getInstance();
        TimeZone tz = updateTime.getTimeZone();
        updateTime.set(Calendar.HOUR_OF_DAY, 00);
        updateTime.set(Calendar.MINUTE, 40);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, MeasurementAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }


}
