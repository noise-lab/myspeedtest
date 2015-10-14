package com.num.controller.utils;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.num.Constants;
import com.num.model.Address;
import com.num.model.Measurement;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
                Log.d("ServerDnsTask",  "Exception doInBackground");
                e.printStackTrace();
            }
            return dnsTargets;
        }
    }

}
