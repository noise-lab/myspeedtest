package com.num.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.num.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

//import com.mobilyzer.Config;
//import com.mobilyzer.MeasurementScheduler;
//import com.mobilyzer.api.API;
//import com.mobilyzer.exceptions.MeasurementError;
//import com.num.controller.utils.Logger;

public class DataPlanActivity extends Activity {

    private TextView start;
    private RadioGroup radioGroup1;
    private String[] radioGroup_text1;
    private String[] radioGroup_values1;
    private RadioGroup radioGroup2;
    private String[] radioGroup_text2;
    private String[] radioGroup_values2;
    private EditText other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_plan);

        start = (TextView) findViewById(R.id.data_plan_start);
        radioGroup1 = (RadioGroup) findViewById(R.id.data_plan_type_radio_group);
        radioGroup_text1 = getResources().getStringArray(R.array.data_plan_type_entries);
        radioGroup_values1 = getResources().getStringArray(R.array.data_plan_type_values);
        radioGroup2 = (RadioGroup) findViewById(R.id.data_plan_promotion_radio_group);
        radioGroup_text2 = getResources().getStringArray(R.array.data_plan_promotion_entries);
        radioGroup_values2 = getResources().getStringArray(R.array.data_plan_promotion_values);
        other = (EditText) findViewById(R.id.data_plan_promotion_edit);

        LinearLayout.LayoutParams rg = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int dataplan_type = Integer.parseInt(prefs.getString("pref_data_plan_type", "-3"));
        for(int i=0; i<radioGroup_text1.length; i++){
            RadioButton radiobutton = new RadioButton(this);
            radiobutton.setText(radioGroup_text1[i]);
            radiobutton.setId(i);
            radiobutton.setTextColor(Color.BLACK);
            if ( dataplan_type > -3 && dataplan_type == Integer.parseInt(radioGroup_values1[i]) )
                radiobutton.setChecked(true);
            radioGroup1.addView(radiobutton, rg);
        }

        int dataplan_promo = Integer.parseInt(prefs.getString("pref_data_plan_promo", "-3"));
        for(int i=0; i<radioGroup_text2.length; i++){
            RadioButton radiobutton = new RadioButton(this);
            radiobutton.setText(radioGroup_text2[i]);
            radiobutton.setId(i);
            if(radioGroup_text2[i].contains("Other"))
                radiobutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        other.setVisibility((b)?View.VISIBLE:View.INVISIBLE);
                        other.setEnabled(b);
                        other.setFocusable(b);
                        other.setFocusableInTouchMode(b);
                        other.setText(prefs.getString("pref_data_plan_other", ""));
                    }
                });
            radiobutton.setTextColor(Color.BLACK);
            if ( dataplan_promo > -3 && dataplan_promo == Integer.parseInt(radioGroup_values2[i]) )
                radiobutton.setChecked(true);
            radioGroup2.addView(radiobutton, rg);
        }


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                int index1 = radioGroup1.getCheckedRadioButtonId();
                if(index1<0) return;

                int index2 = radioGroup2.getCheckedRadioButtonId();
                if(index2<0) return;

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor e = prefs.edit();
                e.putString("pref_data_plan_type", radioGroup_values1[index1]);
                e.putString("pref_data_plan_promo", radioGroup_values2[index2]);
                e.putString("pref_data_plan_other", other.getText().toString());
                e.putString("pref_data_plan_lastupd", df.format(c.getTime()));
                e.commit();
                finish();

                /*
                try {
                    API api = API.getAPI(DataPlanActivity.this, Config.CHECKIN_KEY);
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
                } */

                Intent myIntent = new Intent(getApplicationContext(), DataCapActivity.class);
                startActivity(myIntent);
            }
        });
    }

}
