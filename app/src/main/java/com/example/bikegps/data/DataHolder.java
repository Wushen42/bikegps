package com.example.bikegps.data;

import android.location.Location;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.bikegps.Helpers;
import com.example.bikegps.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class DataHolder {

    private ArrayList<Location> mLocationList = new ArrayList<>();

    public ArrayList<Location> getLocationList() {
        return mLocationList;
    }

    private MutableLiveData<Integer> mRotationCompass = new MutableLiveData<>();

    public void setRotationCompass(Integer i) {
        mRotationCompass.postValue(i);
    }

    public LiveData<Integer> getRotationCompass() {
        return mRotationCompass;
    }

    public LiveData<String> getDirectionCompass() {
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
                return i + "° " + where;
            }
        });
    }

    private MutableLiveData<Double> mDistance = new MutableLiveData<>();

    public void setDistance(Double d) {
        mDistance.postValue(d);
    }

    public LiveData<Double> getDistance() {
        return mDistance;
    }

    private MutableLiveData<Integer> mState = new MutableLiveData<>();

    public void setState(int i) {
        mState.postValue(i);
    }

    public LiveData<Integer> getState() {
        return mState;
    }

    private MutableLiveData<Location> mCurrentLocation = new MutableLiveData<>();

    public void setCurrentLocation(Location loc) {
        if (loc != null) tryUpdate(loc, getLastKnownLocationLocation().getValue());
        mCurrentLocation.postValue(loc);
    }

    private void tryUpdate(Location current, Location last) {
        if (getState().getValue() == null) return;
        if (getState().getValue() != R.string.running) return;
        if (last == null) {
            setLastKnownLocationLocation(current);
            return;
        }
        float dist = current.distanceTo(last);
        if (dist < 50) return;
        if (getDistance().getValue() == null) {
            setDistance((double) dist);
            return;
        }
        setDistance(getDistance().getValue() + dist);
        setLastKnownLocationLocation(current);

    }

    public LiveData<Location> getCurrentLocation() {
        return mCurrentLocation;
    }

    private MutableLiveData<Location> mLastKnownLocation = new MutableLiveData<>();

    public void setLastKnownLocationLocation(Location loc) {
        if (loc != null) mLocationList.add(loc);
        mLastKnownLocation.postValue(loc);
    }

    public LiveData<Location> getLastKnownLocationLocation() {
        return mLastKnownLocation;
    }

    private MutableLiveData<Long> mElapsedTimeMS = new MutableLiveData<>();

    public void setElapsedTimeMS(Long l) {
        mElapsedTimeMS.postValue(l);
    }

    public LiveData<Long> getElapsedTimeMS() {
        return mElapsedTimeMS;
    }

    public DataHolder() {
        Reset();
    }

    public void Reset() {
        mLocationList.clear();
        setDistance(0.0);
        setLastKnownLocationLocation(null);
        setCurrentLocation(null);
        setRotationCompass(0);
        setState(R.string.stop);
        setElapsedTimeMS(0L);
    }

    private JSONObject createJsonData(JSONObject json) throws JSONException {
        if(json == null)json = new JSONObject();
        json.put("duration",Helpers.getElapsedTime(mElapsedTimeMS.getValue()));
        json.put("distance",mDistance.getValue());
        long elapsedRealTime = new Date().getTime();
        json.put("date", Helpers.getDate(elapsedRealTime,"dd-MM-yyyy hh:mm:ss"));
        json.put("dateTimeMs",elapsedRealTime);
        JSONArray locationArray = new JSONArray();
        if(mLocationList!=null){
            for (Location location : mLocationList){
                locationArray.put(
                        new JSONObject()
                                .put("lat",location.getLatitude())
                                .put("lng",location.getLongitude())
                                .put("alt",location.getAltitude())
                );
            }
        }

        json.put("travel",locationArray);
        return json;
    }

    @NonNull
    @Override
    public String toString() {
        try {
            return createJsonData(null).toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }

    }

    public String toString(String filename) {
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("name",filename);
            return createJsonData(jsonObject).toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
