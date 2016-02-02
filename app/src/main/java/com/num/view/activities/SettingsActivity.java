package com.num.view.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.mobilyzer.Config;
import com.mobilyzer.MeasurementScheduler;
import com.mobilyzer.api.API;
import com.mobilyzer.exceptions.MeasurementError;
import com.mobilyzer.util.Logger;
import com.num.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if( key.equals("pref_data_plan_promo") )
        {
            String dataplan = sharedPreferences.getString(key, "");
            //
        } else if ( key.equals("pref_data_cap") ) {
            String val = sharedPreferences.getString(key, "");
            int datacap=Integer.parseInt(val);
            try {
                API api = API.getAPI(SettingsActivity.this, Config.CHECKIN_KEY);
                //TODO remove replicated code in DataCapActivity
                if (datacap >= 2000 && datacap < 0) {
                    api.setDataUsage(MeasurementScheduler.DataUsageProfile.PROFILE3); //DEFAULT PROFILE, ~250MB
                } else if (datacap >= 0 && datacap < 250) {//~50MB
                    api.setDataUsage(MeasurementScheduler.DataUsageProfile.PROFILE1);
                } else {//~100MB
                    api.setDataUsage(MeasurementScheduler.DataUsageProfile.PROFILE2);
                }
            }catch (MeasurementError measurementError){
                Logger.e("Error setting data usage profile");
            }
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor e = prefs.edit();
        e.putString("pref_data_plan_lastupd", df.format(c.getTime()));
        e.commit();
        finish();
    }
}
