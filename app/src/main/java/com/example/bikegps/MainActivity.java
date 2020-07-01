package com.example.bikegps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.bikegps.data.AcquisitionService;
import com.example.bikegps.data.DataHolder;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.core.app.BundleCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.bikegps.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private final static String TAG="MAIN";
    private final static int REQUEST_LOCATION=1000;
    private ArrayList<String> requestedPermissions=new ArrayList<>();
    private DataHolder mDataHolder;
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        if(requestedPermissions.size()<0) {
            requestPermissions(requestedPermissions.toArray(new String[requestedPermissions.size()]), REQUEST_LOCATION);
        }
        Intent serviceIntent = new Intent(this, AcquisitionService.class);
        startService(serviceIntent);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /*FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public void checkPermissions(){
        if(checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE)==PackageManager.PERMISSION_DENIED){
            requestedPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_DENIED){
            requestedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_DENIED){
            requestedPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)==PackageManager.PERMISSION_DENIED){
            requestedPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if(checkSelfPermission(Manifest.permission.INTERNET)==PackageManager.PERMISSION_DENIED){
            requestedPermissions.add(Manifest.permission.INTERNET);
        }
        if(checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)==PackageManager.PERMISSION_DENIED){
            requestedPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //((SensorManager) getSystemService(Context.SENSOR_SERVICE)).unregisterListener(DataHolder.getInstance(this));
        //this.stopService(new Intent(this,ForegroundService.class));
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_LOCATION){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this,"Permission not granted, the app won't work",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
}