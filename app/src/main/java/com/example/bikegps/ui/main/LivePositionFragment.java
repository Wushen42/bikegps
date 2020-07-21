package com.example.bikegps.ui.main;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.bikegps.ForegroundService;
import com.example.bikegps.data.AcquisitionService;
import com.example.bikegps.data.DataHolder;
import com.example.bikegps.R;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.function.Function;

/**
 * A placeholder fragment containing a simple view.
 */
public class LivePositionFragment extends Fragment {

    private static final String TAG = "FragmentPlaceholder";
    private static final String ARG_SECTION_NUMBER = "section_number";

    private DataHolder dataModel;
    public static LivePositionFragment newInstance() {
        LivePositionFragment fragment = new LivePositionFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
    ServiceConnection mServiceConnection;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent mIntent = new Intent(this.getActivity().getApplication(), AcquisitionService.class);
        mServiceConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                dataModel=((AcquisitionService.MyBinder) iBinder).getDataHolder();
                final AcquisitionService.MyBinder binder = ((AcquisitionService.MyBinder) iBinder);
                final TextView speedView = root.findViewById(R.id.speed_textView);
                final TextView altitudeView = root.findViewById(R.id.altitude);
                final TextView compassText = root.findViewById(R.id.compass_text);
                final ImageView compassImage = root.findViewById(R.id.compassImage);
                final TextView distanceText = root.findViewById(R.id.distance);
                dataModel.getCurrentLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
                    @Override
                    public void onChanged(Location location) {
                        if( location==null) return;
                        speedView.setText(new DecimalFormat("#.#").format(location.getSpeed()*3.6));
                        altitudeView.setText(trimNumber(location.getAltitude())+" m");
                    }
                });
                dataModel.getDirectionCompass().observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        compassText.setText(s);
                    }
                });
                dataModel.getRotationCompass().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer i) {
                        compassImage.setRotation((float)-i);
                    }
                });
                distanceText.setText(new DecimalFormat("#.#").format(dataModel.getDistance().getValue()/1000));
                dataModel.getDistance().observe(getViewLifecycleOwner(), new Observer<Double>() {
                    @Override
                    public void onChanged(Double aDouble) {
                        distanceText.setText(new DecimalFormat("#.#").format(aDouble/1000));
                    }
                });

                final Button startButton = root.findViewById(R.id.buttonStart);
                final Intent mI=new Intent(getContext(),ForegroundService.class);
                startButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(),"Restart Travel",Toast.LENGTH_SHORT).show();
                        binder.StartAction();
                        dataModel.setLastKnownLocationLocation(null);
                        dataModel.setDistance(0.0);
                        dataModel.setState(R.string.running);

                        getContext().startService(mI);
                    }
                });

                final Button stopButton = root.findViewById(R.id.buttonStop);
                stopButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(),"Stop Travel",Toast.LENGTH_SHORT).show();
                        dataModel.setState(R.string.stop);
                        binder.StopAction();
                        getContext().stopService(mI);
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d("OnDisconnected",componentName+"");
            }
        };
       getActivity().bindService(mIntent, mServiceConnection,Service.BIND_AUTO_CREATE);

    }
    private String trimNumber(float f){
        return new DecimalFormat("#").format(f);
    }
    private String trimNumber(double d){
        return new DecimalFormat("#").format(d);
    }

    @Override
    public void onDestroy() {
        Objects.requireNonNull(this.getActivity()).unbindService(mServiceConnection);
        super.onDestroy();
    }

    private View root;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_livedata, container, false);
        final LinearLayout footer=root.findViewById(R.id.bottom_sheet);
        footer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d("Footer","focus: "+b);
            }
        });
       /* dataModel.getCurrentLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                speedView.setText(trimNumber(location.getSpeed()*5/18));
                altitudeView.setText(trimNumber(location.getAltitude())+" m");
            }
        });
        dataModel.getDirectionCompass().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                compassText.setText(s);
            }
        });
        dataModel.getRotationCompass().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                compassImage.setRotation((float)-i);
            }
        });

       /* final TextView textView = root.findViewById(R.id.section_label);

        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
}