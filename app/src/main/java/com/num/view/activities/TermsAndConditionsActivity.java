package com.num.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.num.Constants;
import com.num.R;
import com.num.controller.utils.BootstrapUtil;
import com.num.controller.utils.DeviceUtil;
import com.num.controller.utils.DownloadJSONListener;
import com.num.controller.utils.RemoteJSONUtil;

import org.json.JSONArray;
import org.json.JSONException;

public class TermsAndConditionsActivity extends Activity implements DownloadJSONListener {

    private WebView textView;
    private TextView accept;
    private TextView reject;
    private String surveyJSON=null;
    private String networkCountryIso=null;
    private String reqCountry=null;

    public void onJSONReceived(String jsonContent){
        String surveyJSON = jsonContent;
        JSONArray jsurvey=null;
        try {
            jsurvey = new JSONArray(surveyJSON);
        } catch (JSONException jsone) {
            Log.d("TermsAndConditions", "invalid survey json.");
            jsone.printStackTrace();
            return;
        }

        for (int i = 0; i < jsurvey.length(); ++i) {
            try {
                //check required countries to survey redirect
                reqCountry=jsurvey.getJSONObject(i).getString("required");
                if(reqCountry.equalsIgnoreCase(networkCountryIso)) {
                    SharedPreferences sharedpreferences =
                            getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                                    Context.MODE_PRIVATE);

                    SharedPreferences.Editor e = sharedpreferences.edit();

                    e.putString("survey_required", jsurvey.getJSONObject(i).getString("link"));
                    //System.out.println("survey_required set to ("+i+") "+jsurvey.getJSONObject(i).getString("link"));
                    e.commit();
                    //finish();
                    break;
                }
            } catch (JSONException jsone) {} //ok, "required" not required :)
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkCountryIso = ((TelephonyManager) getApplicationContext().
                getSystemService(Context.TELEPHONY_SERVICE)).getNetworkCountryIso();
        RemoteJSONUtil.setJSONtoDownload(this, Constants.SURVEY_SERVER_ADDRESS,
                ((ConnectivityManager) getApplicationContext().
                        getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo());
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
