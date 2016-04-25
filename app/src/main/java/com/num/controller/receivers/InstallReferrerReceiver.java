package com.num.controller.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.num.Constants;
import com.num.controller.utils.ServerUtil;

public class InstallReferrerReceiver extends BroadcastReceiver {

    final static String TAG = "InstallReferrerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.hasExtra("id")) {
            String id = intent.getStringExtra("id");

            if(id != null)
            {
                SharedPreferences.Editor e_def= context.getSharedPreferences(
                        Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
                e_def.putString("referrer_id", id);
                e_def.commit();

                Log.d(TAG, "REFERRER_ID recorded!");
                ServerUtil.sendUrlPostBack(context);

            }
            else
            {
                Log.d(TAG, "Warning: referrer id not found!");
            }
        }
    }
}
