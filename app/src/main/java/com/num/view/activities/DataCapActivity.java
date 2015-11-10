package com.num.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mobilyzer.Config;
import com.mobilyzer.MeasurementScheduler;
import com.mobilyzer.api.API;
import com.mobilyzer.exceptions.MeasurementError;
import com.mobilyzer.util.Logger;
import com.num.R;

public class DataCapActivity extends Activity {

    private TextView start;
    private RadioGroup radioGroup;
    private String[] radioGroup_text;
    private String[] radioGroup_values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_cap);

        start = (TextView) findViewById(R.id.data_cap_start);
        radioGroup = (RadioGroup) findViewById(R.id.data_cap_radio_group);
        radioGroup_text = getResources().getStringArray(R.array.data_cap_entries);
        radioGroup_values = getResources().getStringArray(R.array.data_cap_values);

        LinearLayout.LayoutParams rg = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);

        for(int i=0; i<radioGroup_text.length; i++){
            RadioButton radiobutton = new RadioButton(this);
            radiobutton.setText(radioGroup_text[i]);
            radiobutton.setId(i);
            radiobutton.setTextColor(Color.BLACK);
            radioGroup.addView(radiobutton, rg);
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int index = radioGroup.getCheckedRadioButtonId();
                if(index<0) return;


                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor e = prefs.edit();
                e.putString("pref_data_cap", radioGroup_values[index]);
                e.commit();
                finish();

                try {
                    API api = API.getAPI(DataCapActivity.this, Config.CHECKIN_KEY);
                    if (index == 0) {
                        api.setDataUsage(MeasurementScheduler.DataUsageProfile.UNLIMITED);
                    } else if (index == 1 || index == 7 || index == 8) {//DEFAULT PROFILE, ~250MB
                        api.setDataUsage(MeasurementScheduler.DataUsageProfile.PROFILE3);
                    } else if (index >= 2 && index <= 5) {//~50MB
                        api.setDataUsage(MeasurementScheduler.DataUsageProfile.PROFILE1);
                    } else if (index == 6) {//~100MB
                        api.setDataUsage(MeasurementScheduler.DataUsageProfile.PROFILE2);
                    }
                }catch (MeasurementError measurementError){
                    Logger.e("Error setting data usage profile");
                }

                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(myIntent);
            }
        });
    }

}
