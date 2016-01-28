package com.num.view.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.mobilyzer.util.Logger;
import com.num.Constants;
import com.num.R;
import com.num.controller.utils.BootstrapUtil;

public class InterferenceConsentActivity extends AppCompatActivity {
    private WebView textView;
    private TextView accept;
    private TextView reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interference_consent);

        textView = (WebView) findViewById(R.id.interference_consent_text);
        textView.loadUrl("file:///android_asset/interference_consent_text.html");
        accept = (TextView) findViewById(R.id.interference_consent_accept);
        reject = (TextView) findViewById(R.id.interference_consent_reject);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("given_interference_consent", true);
                editor.commit();

                finish();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("given_interference_consent", false);
                editor.commit();

                finish();
            }
        });
    }
}
