package com.num;

import com.num.model.Address;

public class Constants {

    public static final String SHARED_PREFERENCES_NAME = "com.num";
    public static final String API_SERVER_ADDRESS = "http://ruggles.gtnoise.net/";
    public static final String DATA_DIRECTORY = "/data/data/com.num";
    public static final String LOG_TAG = "My Speed Test";
    public static final boolean DEBUG = true;

    /* Threads */
    public static final int CORE_POOL_SIZE = 10;
    public static final int MAX_POOL_SIZE = 10;
    public static final int KEEP_ALIVE_TIME = 5;

    /* Traceroute */
    public static final int MAX_HOP = 20;
    public static final int TRACE_PORT = 33434;
    public static final int TRACE_COUNT = 1;

    /* Ping */
    public static final double PING_WARMUP_SEQUENCE_GAP = 0.2f;
    public static final Address PING_SEQUENCE_ADDRESS = new Address("143.215.131.173", "Atlanta, GA");
    public static final int PING_SEQUENCE_TOTAL = 20;
    public static final String PING_SEQUENCE_VERSION = "1";

    /* Network */
    public static final String UNAVAILABLE_CELLID = "65535";
    public static final String UNAVAILABLE_CELLLAC = "65535";

    /* Background Measurement Service */
    public static final int UPDATE_INTERVAL = 1000 * 60 * 15; // 15 minutes
    public static final int DAILY_INTERVAL = 1000 * 60 * 60 * 24; // used only for switch id
    public static final int NORMAL_SLEEP_TIME = 1000;
    public static final int SHORT_SLEEP_TIME = 100;
    public static final int ONE_MINUTE_TIME = 60 * 1000;

    /* Data Usage */
    public static final String NEXT_MONTHLY_RESET = "first_of_the_month_reset";
}