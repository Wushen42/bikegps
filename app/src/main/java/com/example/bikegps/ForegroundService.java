package com.example.bikegps;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
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
import androidx.core.app.BundleCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikegps.data.AcquisitionService;
import com.example.bikegps.data.DataHolder;
import com.example.bikegps.data.Receiver;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private DataHolder mDataModel;
    private String title="Stopped";
    private String mTimeElapsed = "0:00:00s";
    private float mSpeed=0;
    private double mDistance=0;
    public ForegroundService() {
    }

    private String getMessage(){
        return new DecimalFormat("#.#").format(mSpeed*3.6)+"km/h -- distance: "+trimNumberOne(mDistance/1000)+" km "+mTimeElapsed;
    }
    private String formatSpeed(float s){
        return trimNumber(s)+" km/h";
    }

    private String trimNumber(float f){
        return new DecimalFormat("#").format(f);
    }
    private String trimNumberOne(double f){
        return new DecimalFormat("#.#").format(f);
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
    @Override
    public void onCreate() {
        super.onCreate();
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
    }

    private String formatHeader(float speed){
        return new String(trimNumber(speed)+"km/h  ");
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
