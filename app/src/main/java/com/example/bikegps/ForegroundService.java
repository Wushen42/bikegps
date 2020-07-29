package com.example.bikegps;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.BundleCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikegps.data.AcquisitionService;
import com.example.bikegps.data.DataHolder;
import com.example.bikegps.data.Receiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static android.bluetooth.BluetoothProfile.A2DP;
import static android.bluetooth.BluetoothProfile.GATT;
import static android.bluetooth.BluetoothProfile.GATT_SERVER;
import static android.bluetooth.BluetoothProfile.HEADSET;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTING;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTING;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private DataHolder mDataModel;
    private String title="Stopped";
    private String mTimeElapsed = "0:00:00";
    private float mSpeed=0;
    private double mDistance=0;
    private Bluetooth mBluetooth;
    private DecimalFormat df=new DecimalFormat("0.0");
    public ForegroundService() {
    }

    private String bluetoothMessage(){
        return df.format(mSpeed*3.6)+";"+df.format(mDistance/1000)+";"+mTimeElapsed+";";
    }

    private String getMessage(){
        return new DecimalFormat("0.0").format(mSpeed*3.6)+"km/h -- distance: "+trimNumberOne(mDistance/1000)+" km -- "+mTimeElapsed;
    }

    private String trimNumberOne(double f){
        return new DecimalFormat("0.0").format(f);
    }
    private Observer<Location> locationObserver =new Observer<Location>() {
        @Override
        public void onChanged(Location location) {
            if(location==null) return;
            if(location.getSpeedAccuracyMetersPerSecond()<=0.0) return;
            mSpeed=location.getSpeed();
        }
    };
    private Observer<Double> distanceObserver = new Observer<Double>() {
        @Override
        public void onChanged(Double aDouble) {
            mDistance=aDouble;
        }
    };
    private Observer<Integer> stateObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer integer) {
            title=getString(integer);
        }
    };
    private Observer<Long> timeElapsedObserver = new Observer<Long>() {
        @SuppressLint("DefaultLocale")
        @Override
        public void onChanged(Long millis) {
            mTimeElapsed=Helpers.getElapsedTime(millis);
        }
    };
    private Timer mTimer;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate() {
        super.onCreate();
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        mBluetooth=new Bluetooth((BluetoothManager) getSystemService(BLUETOOTH_SERVICE));
        Intent mIntent = new Intent(getApplicationContext(),AcquisitionService.class);
        mTimer=new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                writeNotification();
            }
        },0,1000);
        mServiceConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                mDataModel=((AcquisitionService.MyBinder) iBinder).getDataHolder();
                mDataModel.getCurrentLocation().observeForever(locationObserver);
                mDataModel.getDistance().observeForever(distanceObserver);
                mDataModel.getState().observeForever(stateObserver);
                mDataModel.getElapsedTimeMS().observeForever(timeElapsedObserver);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mDataModel.getCurrentLocation().removeObserver(locationObserver);
                mDataModel.getDistance().removeObserver(distanceObserver);
                mDataModel.getState().removeObserver(stateObserver);
                mDataModel.getElapsedTimeMS().removeObserver(timeElapsedObserver);
            }
        };


        bindService(mIntent,mServiceConnection,Service.BIND_AUTO_CREATE);
       // mDataHolder= new ViewModelProvider().get(DataHolder.class);

        createNotificationChannel();
        this.startForeground(1, consistentNotification());
    }



    private Notification consistentNotification(){
        Intent pauseIntent = new Intent(this, Receiver.class);
        pauseIntent.setAction(getResources().getString(R.string.intent_actions));
        pauseIntent.putExtra(getResources().getString(R.string.intent_notification_extra), getResources().getString(R.string.pause));
        PendingIntent pausePendingIntent =
                PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title)
                .setContentText(getMessage())
                .setContentIntent(
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                new Intent(getApplicationContext(),MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(R.drawable.ic_pause,"pause",pausePendingIntent)
                .build();
    }
    private void writeNotification(){
        NotificationManagerCompat.from(this).notify(1,consistentNotification());
        mBluetooth.write(bluetoothMessage());
    }
    ServiceConnection mServiceConnection;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager manager = getSystemService(NotificationManager.class);
        if(manager.getNotificationChannel(CHANNEL_ID)!=null)
            manager.deleteNotificationChannel(CHANNEL_ID);
        mDataModel.getCurrentLocation().removeObserver(locationObserver);
        mDataModel.getDistance().removeObserver(distanceObserver);
        unbindService(mServiceConnection);
        mTimer.cancel();
        mBluetooth.cancel();
        stopSelf();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if(manager.getNotificationChannel(CHANNEL_ID)==null)
             manager.createNotificationChannel(serviceChannel);
    }
}
