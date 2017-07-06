package com.knoxpo.runtracker.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.knoxpo.runtracker.R;
import com.knoxpo.runtracker.data.LastLocationLoader;
import com.knoxpo.runtracker.data.RunLoader;
import com.knoxpo.runtracker.model.Run;
import com.knoxpo.runtracker.model.RunManager;
import com.knoxpo.runtracker.receiver.LocationReceiver;

/**
 * Created by Tejas Sherdiwala on 12/5/2016.
 * &copy; Knoxpo
 */

public class MainFragment extends Fragment implements View.OnClickListener{
    private static final String
            TAG = MainFragment.class.getSimpleName(),
            ARGS_RUN_ID = "RUN_ID";

    private static final int
            REQUEST_COARSE_LOCATION_PERMISSION = 0,
            LOAD_RUN=0,
            LOAD_LOCATION=1;



    private TextView mStartedTV,mLatitudeTV,mLongitudeTV,mAltitudeTV,mDurationTV;
    private Button mStartBtn,mStopBtn;

    private RunManager mRunManager;
    private Run mRun;
    private Location mLastLocation;

    public static MainFragment newInstance(long runId) {
        
        Bundle args = new Bundle();
        args.putLong(ARGS_RUN_ID,runId);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private BroadcastReceiver mBroadcastReceiver = new LocationReceiver(){

        @Override
        protected void onLocationReceived(Context context, Location loc) {
            if(!mRunManager.isTrackingRun(mRun)){
                return;
            }
            mLastLocation = loc;
            if(isVisible()){
                updateUI();
            }
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getActivity(),toastText,Toast.LENGTH_LONG).show();
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        assignPermission();
        mRunManager = RunManager.get(getActivity());
        Bundle args = getArguments();
        if(args!=null){
            long runId = args.getLong(ARGS_RUN_ID,-1);
            if(runId!=-1){
                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_RUN,args,new RunLoaderCallbacks());
                lm.initLoader(LOAD_LOCATION,args,new LocationLoaderCallbacks());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main,container,false);

        init(rootView);
        updateUI();
        return rootView;
    }

    private void init(View view){
        mStartedTV = (TextView) view.findViewById(R.id.tv_run_started);
        mLatitudeTV = (TextView) view.findViewById(R.id.tv_run_latitude);
        mLongitudeTV = (TextView) view.findViewById(R.id.tv_run_longitude);
        mAltitudeTV = (TextView) view.findViewById(R.id.tv_run_altitude);
        mDurationTV = (TextView) view.findViewById(R.id.tv_run_duration);

        mStartBtn = (Button) view.findViewById(R.id.btn_run_start);
        mStopBtn = (Button) view.findViewById(R.id.btn_run_stop);

        mStartBtn.setOnClickListener(this);
        mStopBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.findViewById(R.id.btn_run_start) == mStartBtn){
            if(mRun==null) {
                mRun = mRunManager.startNewRun();
            }else{
                mRunManager.startTrackingRun(mRun);
            }
            updateUI();
        }
        if(view.findViewById(R.id.btn_run_stop) == mStopBtn){
            mRunManager.stopRun();
            updateUI();
        }
    }

    private void updateUI(){
        boolean started = mRunManager.isTrackingRun();
        boolean trackingThisRun = mRunManager.isTrackingRun(mRun);
        if(mRun != null){
            mStartedTV.setText(mRun.getStartDate().toString());
        }

        int durationSeconds = 0;
        if(mRun!=null && mLastLocation!=null){
            durationSeconds = mRun.getDurationSecond(mLastLocation.getTime());
            mLatitudeTV.setText(Double.toString(mLastLocation.getLatitude()));
            mLongitudeTV.setText(Double.toString(mLastLocation.getLongitude()));
            mAltitudeTV.setText(Double.toString(mLastLocation.getAltitude()));
        }
        mDurationTV.setText(Run.FormatDuration(durationSeconds));
        mStartBtn.setEnabled(!started);
        mStopBtn.setEnabled(started && trackingThisRun);
    }

    private int checkCoarseLocationPermission(){
        return  ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
    }
    private int checkFineLocationPermission(){
        return  ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    private void assignPermission(){
        if(checkCoarseLocationPermission() != PackageManager.PERMISSION_GRANTED &&
                checkFineLocationPermission() != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_COARSE_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if(requestCode == REQUEST_COARSE_LOCATION_PERMISSION){
                if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(getActivity(), R.string.resqest_denied, Toast.LENGTH_LONG)
                            .show();
                }
            }
    }

    private class RunLoaderCallbacks implements LoaderManager.LoaderCallbacks<Run>{

        @Override
        public Loader<Run> onCreateLoader(int id, Bundle args) {
            return new RunLoader(getActivity(),args.getLong(ARGS_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Run> loader, Run data) {
            mRun = data;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Run> loader) {

        }
    }

    private class LocationLoaderCallbacks implements LoaderManager.LoaderCallbacks<Location>{
        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args) {
            return new LastLocationLoader(getActivity(),args.getLong(ARGS_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location data) {
            mLastLocation = data;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Location> loader) {

        }
    }
}
