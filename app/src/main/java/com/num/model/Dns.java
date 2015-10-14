package com.num.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Dns implements BaseModel, Parcelable {

    private String target;
    private String server;
    private String address;
    private String error;
    private String success;
    private String totaltime;
    private String totalattempts;
    private String time;
    private Measure measure;

    public Dns(String target, String server, String address, String error, String success, String totaltime, String totalattempts) {
        this.target = target;
        this.server = server;
        this.address = address;
        this.error = error;
        this.success = success;
        this.totaltime = totaltime;
        this.totalattempts = totalattempts;
        //this.measure = measure;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        time = sdf.format(new Date());
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }


    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    public String getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(String totaltime) {
        this.totaltime = totaltime;
    }

    public String getTotalattempts() {
        return totalattempts;
    }

    public void setTotalattempts(String totalattempts) {
        this.totalattempts = totalattempts;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    @Override
    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        try {
            obj.putOpt("target", target);
            obj.putOpt("server", server);
            obj.putOpt("address", address);
            obj.putOpt("error", error);
            obj.putOpt("success", success);
            obj.putOpt("totaltime", totaltime);
            obj.putOpt("totalattempts", totalattempts);
            obj.putOpt("time", time);
            //obj.putOpt("measure", measure.toJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(target);
        dest.writeString(server);
        dest.writeString(address);
        dest.writeString(error);
        dest.writeString(totaltime);
        dest.writeString(totalattempts);
        dest.writeString(success);
        dest.writeString(time);
        //dest.writeParcelable(measure, 0);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Dns createFromParcel(Parcel parcel) {
            return new Dns(parcel);
        }

        @Override
        public Dns[] newArray(int size) {
            return new Dns[size];
        }
    };

    private Dns(Parcel parcel) {
    }
}
