package com.example.bikegps.ui.main;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.bikegps.data.AcquisitionService;
import com.example.bikegps.data.DataHolder;
import com.example.bikegps.R;

import java.text.DecimalFormat;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent mIntent = new Intent(this.getActivity(), AcquisitionService.class);
        ServiceConnection serviceConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                dataModel=((AcquisitionService)iBinder).mDataHolder;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d("OnDisconnected",componentName+"");
            }
        };
        this.getActivity().bindService(mIntent, serviceConnection,Service.BIND_AUTO_CREATE);

    }

    private String trimNumber(float f){
        return new DecimalFormat("#").format(f);
    }
    private String trimNumber(double d){
        return new DecimalFormat("#").format(d);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_livedata, container, false);
        final TextView speedView = root.findViewById(R.id.speed_textView);
        final TextView altitudeView = root.findViewById(R.id.altitude);
        final TextView compassText = root.findViewById(R.id.compass_text);
        final ImageView compassImage = root.findViewById(R.id.compassImage);
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