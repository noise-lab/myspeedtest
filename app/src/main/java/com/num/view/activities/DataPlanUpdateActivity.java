package com.num.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.num.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataPlanUpdateActivity extends AppCompatActivity {

    private Button b_cancel;
    private Button b_ok;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_plan_update);

        activity = this;

        b_cancel = (Button) findViewById(R.id.cancel_data_plan_upd_btn_id);
        b_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor e = prefs.edit();
                e.putString("pref_data_plan_lastupd", df.format(c.getTime()));
                e.commit();
                Intent i = new Intent(activity, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        b_ok = (Button) findViewById(R.id.ok_data_plan_upd_btn_id);
        b_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(activity, DataPlanActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}
