package com.example.bikegps.data;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.bikegps.R;

import java.util.ArrayList;

public class DataHolder {

    private ArrayList<Location> mLocationList = new ArrayList<>();
    public ArrayList<Location> getLocationList(){
        return mLocationList;
    }

    private MutableLiveData<Integer> mRotationCompass =new MutableLiveData<>();
    public void setRotationCompass(Integer i){
        mRotationCompass.postValue(i);
    }
    public LiveData<Integer> getRotationCompass(){
        return mRotationCompass;
    }
    public LiveData<String> getDirectionCompass(){
        return Transformations.map(mRotationCompass, new Function<Integer, String>() {
            @Override
            public String apply(Integer i) {
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
                return i+"° "+ where;
            }
        });
    }

    private MutableLiveData<Double> mDistance =new MutableLiveData<>();
    public void setDistance(Double d){
        mDistance.postValue(d);
    }
    public LiveData<Double> getDistance(){
        return mDistance;
    }

    private MutableLiveData<Integer> mState=new MutableLiveData<>();
    public void setState(int i){
        mState.postValue(i);
    }
    public LiveData<Integer> getState(){
        return mState;
    }

    private MutableLiveData<Location> mCurrentLocation = new MutableLiveData<>();
    public void setCurrentLocation(Location loc){
        if(loc!=null)tryUpdate(loc,getLastKnownLocationLocation().getValue());
        mCurrentLocation.postValue(loc);
    }
    private void tryUpdate(Location current,Location last){
        if(getState().getValue()==null) return;
        if(getState().getValue()!= R.string.running) return;
        if (last==null){
            setLastKnownLocationLocation(current);
            return;
        }
        float dist=current.distanceTo(last);
        if(dist<50) return;
        if(getDistance().getValue()==null){
            setDistance((double) dist);
            return;
        }
        setDistance(getDistance().getValue()+dist);
        setLastKnownLocationLocation(current);

    }
    public LiveData<Location> getCurrentLocation(){
        return mCurrentLocation;
    }

    private MutableLiveData<Location> mLastKnownLocation = new MutableLiveData<>();
    public void setLastKnownLocationLocation(Location loc){
        if(loc !=null) mLocationList.add(loc);
        mLastKnownLocation.postValue(loc);
    }
    public LiveData<Location> getLastKnownLocationLocation(){
        return mLastKnownLocation;
    }

    private MutableLiveData<Long> mElapsedTimeMS =new MutableLiveData<>();
    public void setElapsedTimeMS(Long l){
        mElapsedTimeMS.postValue(l);
    }
    public LiveData<Long> getElapsedTimeMS(){
        return mElapsedTimeMS;
    }

    public DataHolder(){
        Reset();
    }
    public void Reset(){
        mLocationList.clear();
        setDistance(0.0);
        setLastKnownLocationLocation(null);
        setCurrentLocation(null);
        setRotationCompass(0);
        setState(R.string.stop);
        setElapsedTimeMS(0L);
    }

}
