package com.num.view.activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.R.layout;

import com.num.R;
import com.num.model.Survey;
import com.num.view.adapters.SurveyListAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import android.util.Log;

public class SurveyActivity extends Activity {
    private ListView listView;
    private ListAdapter adapter;
    private List<Survey> surveys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_survey);
        // Create a progress bar to display while the list loads
        /*ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar); */

        listView = (ListView) findViewById(R.id.list_view_surveys);
        getSurveys();
        adapter = new SurveyListAdapter(getApplicationContext(), surveys);
        //String [] items = new String[] { "Vegetables","Fruits","Flower Buds","Legumes","Bulbs","Tubers" };
        //adapter = new ArrayAdapter<String>(this, layout.simple_list_item_1, items);

        listView.setAdapter(adapter);

    }

    private void getSurveys(){
        String jsonString = "[{\"link\": \"www.google.com\", \"title\": \"survey1\", \"desc\": \"this is a short description\"}, {\"link\": \"www.yahoo.com\", \"title\": \"yahoo survey\", \"desc\": \"this is also a short description of yahoo.com survey\"}]";
        try {
            JSONArray values = new JSONArray(jsonString);
            Log.d("SurveyActivity", values.toString());

            surveys = new ArrayList<>();
            // now create an array with just the names
            for (int i =0; i < values.length(); i++){
                JSONObject entry = (JSONObject) values.get(i);
                String title = (String) entry.get("title");
                String desc = (String) entry.get("desc");
                String link = (String) entry.get("link");
                Survey survey = new Survey(title, desc, link);
                surveys.add(survey);
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
