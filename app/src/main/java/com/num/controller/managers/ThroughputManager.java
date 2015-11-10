package com.num.controller.managers;

import android.content.Context;
import android.os.Handler;

import com.mobilyzer.Config;
import com.mobilyzer.MeasurementTask;
import com.mobilyzer.api.API;
import com.mobilyzer.exceptions.MeasurementError;
import com.num.Constants;
import com.num.controller.tasks.DataUsageTask;
import com.num.controller.tasks.ThroughputTask;
import com.num.model.Throughput;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.berkeley.icsi.netalyzr.tests.NetProbeStats;
import edu.berkeley.icsi.netalyzr.tests.connectivity.BandwidthTestTCP;

public class ThroughputManager {
    private static final boolean useNetalyzr = true;
    private static ThreadPoolExecutor throughputThreadPool;
    private static BlockingQueue<Runnable> workQueue;

    private Handler handler;

    /**
     * Initialize a work queue and a thread pool.
     * @param handler Parent defined handler to handle throughput
     */
    public ThroughputManager(Handler handler) {
        workQueue = new LinkedBlockingQueue<>();
        throughputThreadPool =
                new ThreadPoolExecutor(Constants.CORE_POOL_SIZE, Constants.MAX_POOL_SIZE,
                        Constants.KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue);
        this.handler = handler;
    }

    public static void execute(Context c) {
        API mobilyzer = API.getAPI(c, Config.CHECKIN_KEY);
        Map<String, String> params = new HashMap<String, String>();
        int priority = MeasurementTask.USER_PRIORITY;
        Date endTime = null;
        int contextIntervalSec = 1;
        MeasurementTask task = null;
        ArrayList<MeasurementTask> taskList = new ArrayList<MeasurementTask>();

        try {
            task = mobilyzer.createTask(API.TaskType.TCPTHROUGHPUT, Calendar.getInstance().getTime(),
                    endTime, 60, 1, priority, contextIntervalSec, params);
            mobilyzer.submitTask(task);

            params.put("dir_up", "true");
            task = mobilyzer.createTask(API.TaskType.TCPTHROUGHPUT, Calendar.getInstance().getTime(),
                    endTime, 60, 1, priority, contextIntervalSec, params);
            mobilyzer.submitTask(task);
        } catch (MeasurementError e) {
            e.printStackTrace();
        }
    }

    public static void execute(Context c, BandwidthTestTCP bwt, Handler handler) {
        ThroughputTask task = new ThroughputTask(c, bwt, handler);
        throughputThreadPool.execute(task);
    }

    public static void execute(Context c, NetProbeStats nps, Handler handler) {
        ThroughputTask task = new ThroughputTask(c, nps, handler);
        throughputThreadPool.execute(task);
    }

    public static String outputString(long l) {
        if(l <= 0){
            return "0.0 Mbps";
        }
        else if(l >= 1000000) {
            double d = (double) l / 1000000;
            String n = String.format("%.3f", d);
            return n + " Gbps";
        }
        else if(l >= 1000) {
            double d = (double) l / 1000;
            String n = String.format("%.3f", d);
            return n + " Mbps";
        }
        else {
            double d = (double) l;
            String n = String.format("%.3f", d);
            return n + " Kbps";
        }
    }
}
