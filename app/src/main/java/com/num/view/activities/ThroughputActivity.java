package com.num.view.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobilyzer.Config;
import com.mobilyzer.MeasurementResult;
import com.mobilyzer.UpdateIntent;
import com.mobilyzer.api.API;
import com.mobilyzer.measurements.TCPThroughputTask;
import com.mobilyzer.measurements.TCPThroughputTask.TCPThroughputDesc;
import com.num.R;
import com.num.controller.managers.MeasurementManager;
import com.num.controller.managers.ThroughputManager;
import com.num.controller.utils.DeviceUtil;
import com.num.controller.utils.Logger;
import com.num.db.DatabaseHelper;
import com.num.model.Link;
import com.num.model.Measurement;
import com.num.model.Throughput;

public class ThroughputActivity extends ActionBarActivity {

    private static String TAG = "ThroughputActivity";
    private DatabaseHelper dbHelper;
    private TextView downSpeed, upSpeed, percentage;
    private TextView startButtonTxt;
    private ProgressBar progressBar;
    private LinearLayout startButton, historyButton;
    private ImageView startButtonImage;
    private CountDownTimer countDownTimer;
    private API mobilyzer;
    private Context context;
    private BroadcastReceiver br;
    private final long progressLength = 60000; //1 mins
    private final long progressInterval = 1000; //.5 seconds
    private boolean isRunningUp;
    private boolean isRunningDown;
    private boolean testSuccessful;
    private Link upLink;
    private Link downLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throughput);

        this.context = this;

        dbHelper = new DatabaseHelper(this);

        API mobilyzer = API.getAPI(this, Config.CHECKIN_KEY);
        br = new ThroughputReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(mobilyzer.userResultAction);
        this.registerReceiver(br, filter);

        isRunningUp = false;
        isRunningDown = false;
        testSuccessful = false;
//        Log.d(TAG, "isRunningUp: " + isRunningUp + " isRunningDown: " + isRunningDown + " testSuccessful: " + testSuccessful);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        percentage = (TextView) findViewById(R.id.text_pvalue);
        downSpeed = (TextView) findViewById(R.id.text_dvalue);
        upSpeed = (TextView) findViewById(R.id.text_uvalue);
        startButton = (LinearLayout) findViewById(R.id.button_start);
        startButtonImage = (ImageView) findViewById(R.id.button_start_image);
        startButtonTxt = (TextView) findViewById(R.id.button_start_txt);
        historyButton = (LinearLayout) findViewById(R.id.button_history);

        countDownTimer = new CountDownTimer(progressLength, progressInterval) {
            @Override
            public void onTick(long millisUntilFinished_) {
                if (millisUntilFinished_ > progressInterval && (isRunningUp || isRunningDown)) {
                    int percentage = 100 - (int) (millisUntilFinished_ / 600);
                    progressBar.setProgress(percentage);
                }
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(100);
                if (!testSuccessful) {
                    percentage.setText("Connection Failed");
                    startButtonImage.setImageResource(R.drawable.ic_action_play);
                    startButtonTxt.setText("Start");
                    startButton.setClickable(true);
                    downSpeed.setText("0.0 Mbps");
                    upSpeed.setText("0.0 Mbps");
                }
                testSuccessful = false;
            }

        };

        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DeviceUtil.getInstance().isInternetAvailable(context)) {
                    percentage.setText("No Connection");
                } else {
                    isRunningUp = true;
                    isRunningDown = true;
                    downLink = null;
                    upLink = null;
                    startButtonImage.setImageResource(R.drawable.ic_action_stop);
                    startButtonTxt.setText("Running");
                    startButton.setClickable(false);
                    percentage.setText("In progress...");
                    downSpeed.setText("Running");
                    upSpeed.setText("Running");
                    ThroughputManager.execute(context);
                    countDownTimer.start();
//                    if (!isRunningUp && !isRunningDown) {
//                        isRunningUp = true;
//                        isRunningDown = true;
//                        startButtonImage.setImageResource(R.drawable.ic_action_stop);
//                        startButtonTxt.setText("Running");
//                        startButton.setClickable(false);
//                        percentage.setText("In progress...");
//                        downSpeed.setText("Running");
//                        upSpeed.setText("Running");
//                        ThroughputManager.execute(context);
//                        countDownTimer.start();
//                    } else {
//                        isRunningUp = false;
//                        isRunningDown = false;
//                    }
                }
            }
        });

        historyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ThroughputHistoryActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onDestroy() {
        /* update database */
        dbHelper.updateThroughput();
        dbHelper.close();
        this.unregisterReceiver(br);
        super.onDestroy();
    }

    private class ThroughputReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Parcelable[] parcels = intent.getParcelableArrayExtra(UpdateIntent.RESULT_PAYLOAD);
            MeasurementResult result = (MeasurementResult) parcels[0];
            TCPThroughputDesc desc = (TCPThroughputDesc) result.getMeasurementDesc();
            String throughputJSON = result.getValues().get("tcp_speed_results");
            long tp = (long) desc.calMedianSpeedFromTCPThroughputOutput(throughputJSON);

            Log.d(TAG, "something received");

            if (isRunningDown && !desc.dir_up) {
                downLink = new Link(1, tp, desc.duration_period_sec, desc.target, TCPThroughputTask.PORT_DOWNLINK + "");
                downSpeed.setText(ThroughputManager.outputString(tp));
                isRunningDown = false;
                Log.d(TAG, "downlink");
            } else if(isRunningUp && desc.dir_up){
                if(null!=downLink){
                    upLink = new Link(1, tp, desc.duration_period_sec, desc.target, TCPThroughputTask.PORT_UPLINK + "");
                    upSpeed.setText(ThroughputManager.outputString(tp));

                    //update display
                    startButtonImage.setImageResource(R.drawable.ic_action_replay);
                    startButtonTxt.setText("Start");
                    startButton.setClickable(true);
                    progressBar.setProgress(100);
                    percentage.setText("Test Complete");


                    String dateTime = dbHelper.getDateTime();
                    Throughput throughput =
                            new Throughput(downLink, upLink, dateTime);

                    dbHelper.insertThroughput(throughput);
                    Measurement measurement = new Measurement(context,true);
                    measurement.setThroughput(throughput);
                    MeasurementManager manager = new MeasurementManager(context);
                    manager.sendMeasurement(measurement);

                    testSuccessful = true;

                    Log.d(TAG, "uplink if");
                }else{
                    Log.d(TAG, "uplink else");
                }
                isRunningUp = false;
                upLink = null;
                downLink = null;
            }
        }
    }
}
