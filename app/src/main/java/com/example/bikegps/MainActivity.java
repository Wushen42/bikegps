package com.example.bikegps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.bikegps.data.AcquisitionService;
import com.example.bikegps.data.DataHolder;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.WindowManager;
import android.widget.Toast;

import com.example.bikegps.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private final static String TAG="MAIN";
    private final static int REQUEST_LOCATION=1000;
    private final static int REQUEST_BLUETOOTH=1001;
    private ArrayList<String> requestedPermissionsLocations =new ArrayList<>();
    private ArrayList<String> requestedPermissionsBluetooth =new ArrayList<>();
    private DataHolder mDataHolder;
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermissions();
       // checkPermissions();
       /* if (requestedPermissionsLocations.size() > 0) {
            String[] strings = new String[requestedPermissionsLocations.size()];
            for (int i = 0; i < strings.length; i++) {
                strings[i] = requestedPermissionsLocations.get(i);
                requestPermissions(strings, REQUEST_LOCATION);
            }
        }

        if (requestedPermissionsBluetooth.size() > 0) {
            String[] strings = new String[requestedPermissionsBluetooth.size()];
            for (int i = 0; i < strings.length; i++) {
                strings[i] = requestedPermissionsBluetooth.get(i);
                requestPermissions(strings, REQUEST_BLUETOOTH);
            }
        }*/

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
        public void askPermissions(){
            if (checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.FOREGROUND_SERVICE},0);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE},3);
            }
            if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.INTERNET},4);
            }
            if (checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH},5);
            }
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_ADMIN},6);
            }
        }
            public void checkPermissions () {
                if (checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_DENIED) {
                    requestedPermissionsLocations.add(Manifest.permission.FOREGROUND_SERVICE);
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    requestedPermissionsLocations.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    requestedPermissionsLocations.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED) {
                    requestedPermissionsLocations.add(Manifest.permission.ACCESS_NETWORK_STATE);
                }
                if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
                    requestedPermissionsLocations.add(Manifest.permission.INTERNET);
                }
                if (checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED) {
                    requestedPermissionsBluetooth.add(Manifest.permission.BLUETOOTH);
                }
                if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED) {
                    requestedPermissionsBluetooth.add(Manifest.permission.BLUETOOTH_ADMIN);
                }
            }

            @Override
            protected void onDestroy () {
                super.onDestroy();
                //((SensorManager) getSystemService(Context.SENSOR_SERVICE)).unregisterListener(DataHolder.getInstance(this));
                //this.stopService(new Intent(this,ForegroundService.class));
                //android.os.Process.killProcess(android.os.Process.myPid());
            }

           /* @Override
            public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults){
                if (requestCode == REQUEST_LOCATION) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "Permission not granted, the app won't work", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }*/
        }


