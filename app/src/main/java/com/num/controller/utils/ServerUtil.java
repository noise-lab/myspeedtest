package com.num.controller.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.num.Constants;
import com.num.model.Address;
import com.num.model.Measurement;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServerUtil {

    public static List<Address> getTargets() {
        List<Address> pingTargets = new ArrayList<>();
        ServerTask task = new ServerTask();
        task.execute();
        try {
            return task.get();
        } catch (Exception e) {
            pingTargets.add(new Address("www.google.com", "Google"));
            pingTargets.add(new Address("www.facebook.com", "Facebook"));
            pingTargets.add(new Address("www.twitter.com", "Twitter"));
            pingTargets.add(new Address("www.youtube.com", "YouTube"));
            pingTargets.add(new Address("www.bing.com", "Bing"));
            pingTargets.add(new Address("www.wikipedia.com", "Wikipedia"));
            pingTargets.add(new Address("143.215.131.173", "Atlanta, GA"));
            pingTargets.add(new Address("www.yahoo.com", "Yahoo!"));
            pingTargets.add(new Address("www.whatsapp.com", "WhatsApp"));
        }
        return pingTargets;
    }

    public static void sendMeasurement(Measurement measurement) {
        String url = Constants.API_SERVER_ADDRESS + "measurement_v2";
        Measurement currentMeasurement = measurement;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(url);
            postRequest.setHeader("Accept", "application/json");
            postRequest.setHeader("Content-type", "application/json");
            Measurement.unsentStack.push(currentMeasurement);
            while(!Measurement.unsentStack.isEmpty()) {
                currentMeasurement = Measurement.unsentStack.pop();
                StringEntity se = new StringEntity(currentMeasurement.toJSON().toString());
                postRequest.setEntity(se);
                HttpResponse response = httpClient.execute(postRequest);
                String r = EntityUtils.toString(response.getEntity());
                if(Constants.DEBUG) {
                    int maxLogStringSize = 1000;
                    String veryLongString = r;
                    for (int i = 0; i <= veryLongString.length() / maxLogStringSize; i++) {
                        int start = i * maxLogStringSize;
                        int end = (i + 1) * maxLogStringSize;
                        end = end > veryLongString.length() ? veryLongString.length() : end;
                        Log.d("DEBUG", veryLongString.substring(start, end));
                    }
                }
            }
        } catch (IOException e) {
            Measurement.unsentStack.push(currentMeasurement);
            Log.d("DEBUG", "Postponing measurement");
        }
    }

    private static class ServerTask extends AsyncTask<Void,Void,List<Address>> {
        @Override
        protected List<Address> doInBackground(Void... params) {
            List<Address> pingTargets = new ArrayList<>();
            try {
                String url = Constants.API_SERVER_ADDRESS + "values";
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet getRequest = new HttpGet(url);
                getRequest.setHeader("Accept", "application/json");
                getRequest.setHeader("Content-type", "application/json");
                HttpResponse response = null;
                response = httpClient.execute(getRequest);
                String r = EntityUtils.toString(response.getEntity());
                JSONObject jsonResponse = new JSONObject(r);
                JSONArray servers = jsonResponse.getJSONObject("values").getJSONObject("ping_servers")
                        .getJSONObject("servers").getJSONArray("default");
                for(int i=0; i<servers.length(); i++) {
                    JSONObject obj = servers.getJSONObject(i);
                    String tag = obj.getString("tag");
                    String ip = obj.getString("ipaddress");
                    Address address = new Address(ip, tag);
                    pingTargets.add(address);
                }
            } catch (Exception e) {
                pingTargets.add(new Address("www.google.com", "Google"));
                pingTargets.add(new Address("www.facebook.com", "Facebook"));
                pingTargets.add(new Address("www.twitter.com", "Twitter"));
                pingTargets.add(new Address("www.youtube.com", "YouTube"));
                pingTargets.add(new Address("www.bing.com", "Bing"));
                pingTargets.add(new Address("www.wikipedia.com", "Wikipedia"));
                pingTargets.add(new Address("143.215.131.173", "Atlanta, GA"));
                pingTargets.add(new Address("www.yahoo.com", "Yahoo!"));
                pingTargets.add(new Address("www.whatsapp.com", "WhatsApp"));
            }
            return pingTargets;
        }
    }

    public static List<Pair<String, String>> getDnsTargets() {
        List<Pair<String, String>> dnsTargets = new ArrayList<>();
        ServerDnsTask task = new ServerDnsTask();
        task.execute();
        try {
            return task.get();
        } catch (Exception e) {
            System.err.println("getDnsTargets");
            e.printStackTrace();
        }
        return dnsTargets;
    }

    private static class ServerDnsTask extends AsyncTask<Void,Void,List<Pair<String, String>>> {
        @Override
        protected List<Pair<String, String>> doInBackground(Void... params) {
            List<Pair<String, String>> dnsTargets = new ArrayList<>();
            try {
                String url = Constants.API_SERVER_ADDRESS + "dnsvalues";
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet getRequest = new HttpGet(url);
                getRequest.setHeader("Accept", "application/json");
                getRequest.setHeader("Content-type", "application/json");
                HttpResponse response = null;
                response = httpClient.execute(getRequest);
                String r = EntityUtils.toString(response.getEntity());
                JSONObject jsonResponse = new JSONObject(r);
                JSONArray servers = jsonResponse.getJSONObject("values").getJSONArray("dns_targets");

                for(int i=0; i<servers.length(); i++) {
                    JSONObject obj = servers.getJSONObject(i);
                    String target = obj.getString("target");
                    String server = obj.getString("server");
                    dnsTargets.add(new Pair<String, String>(target, server));
                }
            } catch (Exception e) {
                Log.d("ServerDnsTask", "Exception doInBackground");
                e.printStackTrace();
            }
            return dnsTargets;
        }
    }

    public static void sendUrlPostBack(Context context) {
        AsyncUrlPostBack apburl = new AsyncUrlPostBack();
        apburl.setContext(context);
        apburl.execute("referrer_id");
    }
    private static class AsyncUrlPostBack extends AsyncTask<String, Integer, Double> {

        private final static String TAG = "AsyncUrlPostBack";
        private Context context = null;


        private void setContext(Context context)
        {
            this.context = context;
        }

        @Override
        protected Double doInBackground(String... params) {

            URL postbackurl;
            SharedPreferences.Editor e_def = null;
            if( context == null ) {
                Log.d(TAG, "Context not set");
                return null;
            }
            SharedPreferences p_def = context.getSharedPreferences(
                    Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            String referrer_id = p_def.getString(params[0], "novalue");
            e_def=p_def.edit();

            try {
                postbackurl = new URL(Constants.CANPAIGN_POSTBACK_URL + referrer_id);
            }
            catch(MalformedURLException mue)
            {
                Log.d(TAG, "failed to decode post url: "+mue.getMessage());
                return null;
            }
            try {

                HttpURLConnection connection = (HttpURLConnection) postbackurl.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                //DataOutputStream outstream = new DataOutputStream(connection.getOutputStream());
                //outstream.writeBytes(referrer_id);
                //outstream.flush();
                //outstream.close();
                int responseCode = connection.getResponseCode();
                if ( responseCode == 200 && context != null) {
                    e_def.remove(params[0]);
                    e_def.commit();
                    Log.d(TAG, "POSTED to " + Constants.CANPAIGN_POSTBACK_URL + referrer_id);
                }
                else {
                    Log.d(TAG, "Failed to POST to " + Constants.CANPAIGN_POSTBACK_URL + referrer_id);
                }
            }
            catch (IOException ioe)
            {
                Log.d(TAG, "" + ioe.getMessage());
            }
            return null;
        }
    }

}
