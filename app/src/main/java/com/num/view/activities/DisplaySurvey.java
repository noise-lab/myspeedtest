package com.num.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.num.R;

public class DisplaySurvey extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_survey);
        Intent intent = getIntent();
        String site = intent.getStringExtra(SurveyActivity.SURVEY_LINK);
        if (!site.contains("http")) {
            site = "http://" + site;
        }

        // setup a new webview and try to load the website
        WebView webView = (WebView) findViewById(R.id.survey_web_view);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(site);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_survey, menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (item.getTitle() == null) //workaround TODO: get proper id for "Survey"
        {
            finish();
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(myIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
