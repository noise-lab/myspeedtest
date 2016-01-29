package com.num.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.num.R;

public class PromotionActivity extends Activity {

    private TextView start;
    private RadioGroup radioGroup1;
    private String[] radioGroup_text1;
    private String[] radioGroup_values1;
    private RadioGroup radioGroup2;
    private String[] radioGroup_text2;
    private String[] radioGroup_values2;


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


        LinearLayout.LayoutParams rg = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);

        for(int i=0; i<radioGroup_text1.length; i++){
            RadioButton radiobutton = new RadioButton(this);
            radiobutton.setText(radioGroup_text1[i]);
            radiobutton.setId(i);
            radiobutton.setTextColor(Color.BLACK);
            radioGroup1.addView(radiobutton, rg);
        }

        for(int i=0; i<radioGroup_text2.length; i++){
            RadioButton radiobutton = new RadioButton(this);
            radiobutton.setText(radioGroup_text2[i]);
            radiobutton.setId(i);
            radiobutton.setTextColor(Color.BLACK);
            radioGroup2.addView(radiobutton, rg);
        }


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*int index = radioGroup.getCheckedRadioButtonId();
                if(index<0) return;


                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor e = prefs.edit();
                e.putString("pref_data_cap", radioGroup_values[index]);
                e.commit();
                finish();

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
