package com.example.bikegps.data;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.List;

public class DataHolder extends ViewModel {

    private static volatile DataHolder sInstance;

    public static DataHolder getInstance(Context c){
        if (sInstance==null){
            sInstance=new DataHolder(c);
        }
        return sInstance;
    }

    private MutableLiveData<Integer> mRotationCompass =new MutableLiveData<>();
    private MutableLiveData<String> mDirectionCompass=new MutableLiveData<>();
    public void setRotationCompass(Integer i){
        mRotationCompass.postValue(i);
        String where = "NW";

        if (i >= 350 || i <= 10)
            where = "N";
        if (i < 350 && i > 280)
            where = "NW";
        if (i <= 280 && i > 260)
            where = "W";
        if (i <= 260 && i > 190)
            where = "SW";
        if (i <= 190 && i > 170)
            where = "S";
        if (i <= 170 && i > 100)
            where = "SE";
        if (i <= 100 && i > 80)
            where = "E";
        if (i <= 80 && i > 10)
            where = "NE";
        mDirectionCompass.postValue(i + "° " + where);
    }
    public LiveData<Integer> getRotationCompass(){
        return mRotationCompass;
    }
    public LiveData<String> getDirectionCompass(){
        return mDirectionCompass;
    }

    private MutableLiveData<Double> mDistance =new MutableLiveData<>();
    public void setDistance(Double d){
        mDistance.postValue(d);
    }
    public LiveData<Double> getDistance(){
        return mDistance;
    }
    private MutableLiveData<String> mState=new MutableLiveData<>();
    public void setState(String s){
        mState.postValue(s);
    }
    public LiveData<String> getState(){
        return mState;
    }
    private MutableLiveData<Float> mSpeed = new MutableLiveData<>();
    private MutableLiveData<Location> mCurrentLocation = new MutableLiveData<>();
    public void setCurrentLocation(Location loc){
        mCurrentLocation.postValue(loc);
    }
    public LiveData<Location> getCurrentLocation(){
        return mCurrentLocation;
    }

    private Context mContext;

    public void setSpeed(float i) {
        mSpeed.postValue((i*5/18));
    }

    public LiveData<Float> getSpeed() {
        return mSpeed;
    }

    private DataHolder(Context context) {
        mContext = context;
        BroadcastReceiver rr= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("yes this is a tag","received intent");
            }
        };
    }


    public void startAcquisition() {
        new Gps(mContext,sInstance);
        new Compass(mContext,sInstance);
    }
}
