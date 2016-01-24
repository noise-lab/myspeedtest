package com.num.controller.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import com.mobilyzer.Config;
import com.mobilyzer.api.API;
import com.mobilyzer.util.Logger;
import com.mobilyzer.exceptions.MeasurementError;
import com.num.Constants;
import com.num.controller.receivers.MeasurementAlarmReceiver;
import com.num.controller.receivers.MonthlyResetAlarmReceiver;
import com.num.controller.services.SignalService;

public class BootstrapUtil {



    public static void bootstrap(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        // Setting up signal strength listener
        context.startService(new Intent(context, SignalService.class));

        API api = API.getAPI(context, Config.CHECKIN_KEY);
        try {
            api.setAuthenticateAccount(Config.DEFAULT_USER);
        } catch (MeasurementError e) {
            Logger.e("Error when setting account", e);
        }

        // Monthly alarm resets the data usage table
        if (prefs.getInt(Constants.NEXT_MONTHLY_RESET, 0) == (new DeviceUtil().getCurrentMonth())) {
            // Reset usage data
            DataUsageUtil.resetMobileData(context);
            DataUsageUtil.clearTable(context);
            DataUsageUtil.setFirstMonthOfTheMonthFlag(context, new DeviceUtil().getNextMonth());
        }

        // Set alarms for resetting data usage and periodic measurement
        MonthlyResetAlarmReceiver monthlyAlarm = new MonthlyResetAlarmReceiver();
        MeasurementAlarmReceiver alarm = new MeasurementAlarmReceiver();
        monthlyAlarm.setAlarm(context);
        alarm.setAlarm(context);

    }
}
