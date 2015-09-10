package com.num.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.LinearLayout;

import com.num.Constants;
import com.num.R;
import com.num.controller.utils.DeviceUtil;
import com.num.controller.utils.TracerouteUtil;
import com.num.model.Signal;


public class MainActivity extends ActionBarActivity {

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout throughputButton, latencyButton, tracerouteButton, dataUsageButton, configureButton, aboutUsButton;
        activity = this;

        throughputButton = (LinearLayout) findViewById(R.id.main_button_throughput);
        throughputButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(activity, ThroughputActivity.class);
                startActivity(i);
            }
        });

        latencyButton = (LinearLayout) findViewById(R.id.main_button_latency);
        latencyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(activity, LatencyActivity.class);
                startActivity(i);
            }
        });

        dataUsageButton = (LinearLayout) findViewById(R.id.main_button_data_usage);
        dataUsageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, DataUsageActivity.class);
                startActivity(i);
            }
        });

        tracerouteButton = (LinearLayout) findViewById(R.id.main_button_traceroute);
        tracerouteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(activity, TracerouteActivity.class);
                startActivity(i);
            }
        });

        configureButton = (LinearLayout) findViewById(R.id.main_button_configure);
        configureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, SettingsActivity.class);
                startActivity(i);
            }
        });

        aboutUsButton = (LinearLayout) findViewById(R.id.main_button_surveys);
        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, SurveyActivity.class);
                startActivity(i);
            }
        });

        /* Check Internet Connection */
        if (!DeviceUtil.getInstance().isInternetAvailable(this)) {
            String message =
                    "You are not currently connected to the Internet. Some features may not work.";
            new AlertDialog.Builder(this).setTitle("Internet Warning")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_info:
                Intent aboutUs = new Intent(activity, AboutUsActivity.class);
                startActivity(aboutUs);
                return true;
            case R.id.action_settings:
                Intent settings = new Intent(activity, SettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        if (!prefs.contains("accept_conditions") || !prefs.getBoolean("accept_conditions", false)) {
            finish();
            Intent myIntent = new Intent(getApplicationContext(), TermsAndConditionsActivity.class);
            startActivity(myIntent);
        }
    }
}
