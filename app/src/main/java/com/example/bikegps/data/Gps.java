package com.example.bikegps.data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Callable;

public class Gps {
    private DataHolder mDataHolder;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient client;
    private boolean isLocationCallbackActive;
    public Gps(Context context,@NonNull DataHolder dataHolder){
        isLocationCallbackActive=false;
        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        mDataHolder=dataHolder;
        client = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return TODO;
            return;
        }
        client.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                });
        isLocationCallbackActive=true;
        client.requestLocationUpdates(
                LocationRequest.create()
                        .setInterval(1000)
                        .setFastestInterval(500)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                ,
                locationCallback
                ,null
        );
    }

    @SuppressLint("MissingPermission")
    public void StartGPS(){
        if(isLocationCallbackActive) return;
        isLocationCallbackActive=true;
        client.requestLocationUpdates(
                LocationRequest.create()
                        .setInterval(1000)
                        .setFastestInterval(500)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                ,
                locationCallback
                ,null
        );
    }
    public void StopGPS(){
        if(!isLocationCallbackActive) return;
        client.removeLocationUpdates(locationCallback);
        isLocationCallbackActive=false;
    }
    public void onLocationChanged(Location location) {
        /*setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
        setSpeed(location.getSpeed());
        setAltitude(location.getAltitude());
        setHeading(location.getBearing());*/
        mDataHolder.setCurrentLocation(location);

    }
}
