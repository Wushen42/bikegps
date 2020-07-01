package com.example.bikegps.ui.main;

import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.bikegps.data.DataHolder;
import com.example.bikegps.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Recording#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Recording extends Fragment {


    private Location lastValidLocation = null;
    private DataHolder gpsDataModel;
    public Recording() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Recording newInstance() {
        Recording fragment = new Recording();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //gpsDataModel= DataHolder.Instance();
        gpsDataModel.getCurrentLocation().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if(lastValidLocation==null){
                    lastValidLocation=location;
                    return;
                }
                double dist = location.distanceTo(lastValidLocation);
                if(dist>50){
                    gpsDataModel.setDistance(gpsDataModel.getDistance().getValue()+dist);
                    lastValidLocation=location;
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_recording, container, false);
        gpsDataModel.setDistance(0.0);
        ImageButton start=root.findViewById(R.id.button_rec);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastValidLocation=null;
                gpsDataModel.setState("Running");
            }
        });
        return root;
    }
}