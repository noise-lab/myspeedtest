package com.num.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.num.Constants;
import com.num.R;
import com.num.model.Survey;
import com.num.view.adapters.SurveyListAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import android.os.StrictMode;
import android.widget.ProgressBar;


public class SurveyActivity extends Activity implements AdapterView.OnItemClickListener {
    private ListView listView;

    private ListAdapter adapter;
    private List<Survey> surveys;
    private ProgressBar progressBar;

    public static final String SURVEY_LINK = "edu.princeton.bismark.myspeedtest.SURVEY_LINK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_survey);
        surveys = new ArrayList<>();
        getSurveys();
        listView = (ListView) findViewById(R.id.list_view_surveys);

        adapter = new SurveyListAdapter(getApplicationContext(), surveys);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Log.i("SurveyActivity", "You clicked Item: " + id + " at position:" + position);
        // Then you start a new Activity via Intent
        Intent intent = new Intent();
        intent.setClass(this, DisplaySurvey.class);
        intent.putExtra(SurveyActivity.SURVEY_LINK, surveys.get(position).link);
        startActivity(intent);
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        public boolean downloadComplete;
        public String result;

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                downloadComplete = false;
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            downloadComplete = true;
            this.result = result;
            loadSurveys(result);
        }
    }


    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {


        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);*/

        InputStream is = null;
        // Only display the first 50000 characters of the retrieved
        // web page content.
        int len = 50000;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("SurveyActivity", "The downloaded json is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readIt(is, len);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // download the JSON with survey info and construct data structures for each survey
    private void getSurveys() {
        //String jsonString = "[{\"link\": \"www.google.com\", \"title\": \"survey1\", \"desc\": \"this is a short description\"}, {\"link\": \"www.yahoo.com\", \"title\": \"yahoo survey\", \"desc\": \"this is also a short description of yahoo.com survey\"}]";
        //String jsonString = "";
        try {

            // When user clicks button, calls AsyncTask.
            // Before attempting to fetch the URL, makes sure that there is a network connection.
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new DownloadWebpageTask().execute(Constants.SURVEY_SERVER_ADDRESS);
                //jsonString = downloadUrl(Constants.SURVEY_SERVER_ADDRESS);
            } else {
                throw (new IOException("No network connection available"));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //loadSurveys(jsonString);
    }
    private void loadSurveys(String jsonString){
        try {
            JSONArray values = new JSONArray(jsonString);
            Log.d("SurveyActivity", values.toString());

            // now create an array with just the names
            for (int i =0; i < values.length(); i++){
                JSONObject entry = (JSONObject) values.get(i);
                String title = (String) entry.get("title");
                String desc = (String) entry.get("desc");
                String link = (String) entry.get("link");
                Survey survey = new Survey(title, desc, link);
                //surveys.add(survey);
                ((ArrayAdapter) adapter).add(survey);
            }
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
        Log.d("SurveyActivity", surveys.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_survey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String output = String.format("Received item %d", id);
        Log.d("Survey Activity", output);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
