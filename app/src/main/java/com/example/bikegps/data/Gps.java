package com.example.bikegps.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class Gps {
    private DataHolder mDataHolder;
    public Gps(Context context,@Nullable DataHolder dataHolder){
        mDataHolder=dataHolder!=null?dataHolder:DataHolder.getInstance(context);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
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
        client.requestLocationUpdates(
                LocationRequest.create()
                        .setInterval(1000)
                        .setFastestInterval(500)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                ,
                new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        onLocationChanged(locationResult.getLastLocation());
                    }

                },null
        );
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
