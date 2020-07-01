package com.example.bikegps;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.core.app.BundleCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikegps.data.DataHolder;
import com.example.bikegps.data.Receiver;

import java.text.DecimalFormat;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private DataHolder mDataHolder;
    private String title="Stopped";
    private String toto="ah";
    private float mSpeed=0;
    private double distance=0;
    public ForegroundService() {
    }

    private String getMessage(){
        if(title=="Stopped") return formatSpeed(mSpeed);
        return formatSpeed(mSpeed)+" distance: "+trimNumberOne(distance)+" km" + toto;
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
    @Override
    public void onCreate() {
        super.onCreate();
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);


       /* title=DataHolder.getInstance(this).getState().getValue();
        Float tSpeed=DataHolder.getInstance(this).getSpeed().getValue();
        mSpeed= tSpeed==null?0:tSpeed;
        Double tDouble=DataHolder.getInstance(this).getDistance().getValue();
        distance= tDouble==null?0:tDouble;
        writeNotification();

        //do heavy work on a background thread
        //stopSelf();
        DataHolder.getInstance(this).getState().observe( this,new Observer<String>() {
            @Override
            public void onChanged(String state) {
                title=state;
                writeNotification();
            }
        });
        DataHolder.getInstance(this).getSpeed().observe( this,new Observer<Float>() {
            @Override
            public void onChanged(Float speed) {
                mSpeed=speed;
                writeNotification();
            }
        });
        DataHolder.getInstance(this).getDistance().observe( this,new Observer<Double>() {
            @Override
            public void onChanged(Double dist) {
                distance=dist;
                writeNotification();
            }
        });
        DataHolder.getInstance(this).getDirectionCompass().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
               // notificationLayout.setTextViewText(R.id.notification_title,s);
                writeNotification();
            }
        });*/

        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       // super.onBind(intent);
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
}