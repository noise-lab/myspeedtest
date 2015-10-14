package com.num.controller.managers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import com.mobilyzer.MeasurementTask;
//import com.mobilyzer.api.API;
//import com.mobilyzer.exceptions.MeasurementError;
import com.num.Constants;
import com.num.controller.tasks.DnsLookupTask;
import com.num.controller.tasks.LatencyTask;
import com.num.controller.utils.PingUtil;
import com.num.controller.utils.ServerUtil;
import com.num.model.Address;
import com.num.model.Dns;
import com.num.model.Measurement;

import java.util.ArrayList;
//import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
//import java.util.Map;
//import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import android.os.Message;

/**
 *
 */
public class DnsManager {

    private static ThreadPoolExecutor dnsThreadPool;
    private static BlockingQueue<Runnable> workQueue;
    private Context context;
    private List<Dns> dnses;
    Measurement measurement;
    private int count;
    //private Handler handler;

    /**
     * Initialize a work queue and a thread pool.
     * @param context
     */
    public DnsManager(Context context) {
        workQueue = new LinkedBlockingQueue<>();
        dnsThreadPool = new ThreadPoolExecutor(Constants.CORE_POOL_SIZE,
                Constants.MAX_POOL_SIZE, Constants.KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue);
        //this.handler = handler;
        this.context = context;
        dnses = new ArrayList<>();

    }

    /**
     * Retrieve Dns Targets and fire Dns threads for resolving names
     *
     */
    public void execute() {
        //API mobilyzer = API.getAPI(c, "My Speed Test");
        Date endTime = null;
        //MeasurementTask task = null;

        ArrayList<MeasurementTask> taskList = new ArrayList<MeasurementTask>();
        List <Pair<String, String>> slist=ServerUtil.getDnsTargets();
            /*if(slist != null && slist.size() != 0 ) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }*/
        count = slist.size();
        for (int i=0; i < count; i++)
        {
            String target=((Pair<String, String>)slist.get(i)).first;
            String server=((Pair<String, String>)slist.get(i)).second;

            DnsHandler dnsHandler=new DnsHandler(context);
            DnsLookupTask task = new DnsLookupTask(target, server, dnsHandler);
            dnsThreadPool.execute(task);
        }
        //params.put("target", "www.youtube.com");
        //params.put("server", "8.8.8.8");

        //task = mobilyzer.createTask(API.TaskType.DNSLOOKUP, Calendar.getInstance().getTime(),
        //        endTime, 60, 1, MeasurementTask.USER_PRIORITY, 1, params);
        //mobilyzer.submitTask(task);

        //} catch (MeasurementError e) {
        //    e.printStackTrace();
        //}
    }

    private class DnsHandler extends Handler {

        Context context;

        public DnsHandler(Context context)
        {
            this.context = context;
            measurement = new Measurement(context, false);
        }

        @Override
        public void handleMessage(Message msg) {
            String target = msg.getData().getString("target");
            String server = msg.getData().getString("server");
            String address = msg.getData().getString("address");
            String totaltime = msg.getData().getString("totaltime");
            String success = msg.getData().getString("success");
            String totalattempts = msg.getData().getString("totalattempts");
            String error = msg.getData().getString("error");
            Log.i("DnsLookupTask", "Target:"+target+" Server:"+server+" Address:"+address+" TotalTime:"+totaltime+" Success:"+success+
                    " TotalAttempts: "+totalattempts+" Error:"+error);

            dnses.add(new Dns(target, server,address, error, success, totaltime, totalattempts));
            onComplete();
        }
    }

    private void onComplete() {
        //Log.d("DnsManager", "count:"+count+" dnses.size:"+dnses.size()+" ********************");
        if(dnses.size() >= count) {
            measurement.setDnses(dnses);
            sendMeasurement(measurement);
        }
    }

    public void sendMeasurement(final Measurement measurement) {
        if(Constants.DEBUG) {
            int maxLogStringSize = 1000;
            String veryLongString = measurement.toJSON().toString();
            for (int i = 0; i <= veryLongString.length() / maxLogStringSize; i++) {
                int start = i * maxLogStringSize;
                int end = (i + 1) * maxLogStringSize;
                end = end > veryLongString.length() ? veryLongString.length() : end;
                Log.d(Constants.LOG_TAG, veryLongString.substring(start, end));
            }
        }
        Runnable task = new Runnable() {
            @Override
            public void run() {
                ServerUtil.sendMeasurement(measurement);
            }
        };
        dnsThreadPool.execute(task);
    }
}
