package com.num.controller.utils;

import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by gmartins on 3/4/16.
 */
public class RemoteJSONUtil {

    private DownloadJSONListener downloadJSONListener=null;

    private void setDownloadJSONListener(DownloadJSONListener downloadJSONListener) {
        this.downloadJSONListener = downloadJSONListener;
    }

    private boolean getJSON(String url, NetworkInfo networkInfo)
    {
        try {
            if (networkInfo != null && networkInfo.isConnected()) {
                new DownloadWebpageTask().execute(url);
            } else {
                throw (new IOException("No network connection available"));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean setJSONtoDownload(DownloadJSONListener downloadJSONListener, String url,
                                            NetworkInfo networkInfo)
    {
        RemoteJSONUtil rju=new RemoteJSONUtil();
        rju.setDownloadJSONListener(downloadJSONListener);

        if (networkInfo != null && networkInfo.isConnected()) {
            return rju.getJSON(url, networkInfo);
        }
        return false;
    }

    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        int size=0;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        StringBuffer stringBuffer = new StringBuffer();

        while ((size = reader.read(buffer)) >= 0 )
        {
            stringBuffer.append(buffer);
        }
        return stringBuffer.toString();
    }

    private String downloadUrl(String myurl) throws IOException {

        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);*/

        InputStream is = null;

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
            Log.d("RemoteJSONUtil", "The downloaded json is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readIt(is, conn.getResponseMessage().length());
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

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
                e.printStackTrace();
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            downloadComplete = true;
            this.result = result;
            if (downloadJSONListener != null)
                downloadJSONListener.onJSONReceived(result);
            //loadSurveys(result);
        }
    }
}
