package com.num.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.num.Constants;
import com.num.R;
import com.num.controller.receivers.MeasurementAlarmReceiver;
import com.num.controller.receivers.MonthlyResetAlarmReceiver;
import com.num.controller.services.SignalService;
import com.num.controller.utils.BootstrapUtil;
import com.num.controller.utils.DeviceUtil;

public class TermsAndConditionsActivity extends Activity {

    private WebView textView;
    private TextView accept;
    private TextView reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        textView = (WebView) findViewById(R.id.terms_and_conditions_text);
        textView.loadUrl("file:///android_asset/terms_and_conditions_text.html");
        accept = (TextView) findViewById(R.id.terms_and_conditions_accept);
        reject = (TextView) findViewById(R.id.terms_and_conditions_reject);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedpreferences =
                        getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sharedpreferences.edit();
                e.putBoolean("accept_conditions", true);
                e.putBoolean("background_service", true);
                e.putInt(Constants.NEXT_MONTHLY_RESET, new DeviceUtil().getNextMonth());
                e.commit();
                finish();

                Context context = getApplicationContext();

                BootstrapUtil.bootstrap(context);

                Intent myIntent = new Intent(context, DataPlanActivity.class);
                startActivity(myIntent);
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
