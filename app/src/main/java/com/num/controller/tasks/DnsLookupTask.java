package com.num.controller.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mobilyzer.Config;
import com.num.Constants;
import com.num.controller.utils.TracerouteUtil;
import com.num.model.Traceroute;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

// TODO Handle interrupts
public class DnsLookupTask implements Runnable {

    private String target;
    private String server;
    private Handler handler;

    public DnsLookupTask(String target, String server, Handler handler) {
        this.target = target;
        this.server = server;
        this.handler = handler;
    }

    @Override
    public void run(){
        HashMap<String, String> params = new HashMap<>();
        //TODO use dnsjava http://www.dnsjava.org/
        //System.setProperty("sun.net.spi.nameservice.nameservers", server);
        //System.setProperty("sun.net.spi.nameservice.provider.1", "dns,dnsjava");

        long t1, t2;
        long totalTime = 0;
        InetAddress resultInet = null;
        int successCnt = 0;

        Bundle bundle = new Bundle();
        Message msg = new Message();

        for (int i = 0; i < Config.DEFAULT_DNS_COUNT_PER_MEASUREMENT; i++) {
            try {
                Log.i("DnsLookupTask", "Running DNS Lookup for target " + target);

                bundle.putString("target", target + "");
                bundle.putString("server", server + "");
                bundle.putString("totalattempts", Config.DEFAULT_DNS_COUNT_PER_MEASUREMENT+"");
                bundle.putString("success", "0");

                t1 = System.currentTimeMillis();
                InetAddress inet = InetAddress.getByName(target);
                t2 = System.currentTimeMillis();
                if (inet != null) {
                    totalTime += (t2 - t1);
                    resultInet = inet;
                    successCnt++;
                }

                bundle.putString("address", resultInet + "");
                bundle.putString("totaltime", totalTime+"");
                bundle.putString("success", successCnt+"");


                msg.setData(bundle);
                handler.handleMessage(msg);

            } catch (UnknownHostException e) {
                bundle.putString("error",  e.getMessage());
                msg.setData(bundle);
                handler.handleMessage(msg);
            }
        }



    }
}
