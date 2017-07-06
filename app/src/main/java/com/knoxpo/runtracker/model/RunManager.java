package com.knoxpo.runtracker.model;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.knoxpo.runtracker.data.RunDatabaseHelper;

/**
 * Created by Tejas Sherdiwala on 12/5/2016.
 * &copy; Knoxpo
 */

public class RunManager {
    private static final String TAG = RunManager.class.getSimpleName();
    public static final String ACTION_LOCATION = "com.knoxpo.runtracker.ACTION_LOCATION";

    private static final String PREFS_FILE = "runs",
                PREFS_CURRENT_RUN_ID = "RunManager.currentRunId",
                TEST_PROVIDER = "TEST_PROVIDER";


    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;
    private RunDatabaseHelper mHelper;
    private SharedPreferences mPrefs;
    private long mCurrentRunId;


    private RunManager(Context context){
        mAppContext = context;
        mLocationManager = (LocationManager) mAppContext
                .getSystemService(Context.LOCATION_SERVICE);
        mHelper = new RunDatabaseHelper(mAppContext);
        mPrefs = mAppContext.getSharedPreferences(PREFS_FILE,Context.MODE_PRIVATE);
        mCurrentRunId = mPrefs.getLong(PREFS_CURRENT_RUN_ID,-1);
    }

    public static RunManager get(Context c){
        if(sRunManager==null){
            sRunManager = new RunManager(c.getApplicationContext());
        }
        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate){
        Intent broadCast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate?0:PendingIntent.FLAG_NO_CREATE;

        return PendingIntent.getBroadcast(mAppContext,0,broadCast,flags);
    }

    public void startLocationUpdate(){
        String provider = LocationManager.GPS_PROVIDER;
        Location mLastKnown= mLocationManager.getLastKnownLocation(provider);

        if(mLastKnown!=null){
            mLastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(mLastKnown);
        }

        PendingIntent pi = getLocationPendingIntent(true);
        if(ContextCompat.checkSelfPermission(mAppContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
        }
    }

    public Location getLastLocationForRun(long runId){
        Location location = null;
        RunDatabaseHelper.LocationCursor locationCursor = mHelper.queryLastLocationForRun(runId);
        locationCursor.moveToFirst();
        if(!locationCursor.isAfterLast()){
            location = locationCursor.getLocation();
        }
        locationCursor.close();
        return location;
    }

    public void stopLocationUpdate(){
        PendingIntent pi = getLocationPendingIntent(false);
        if(pi!=null){
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    public boolean isTrackingRun(){
        return getLocationPendingIntent(false) != null;
    }

    private void broadcastLocation(Location lastKnown){
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED,lastKnown);
        mAppContext.sendBroadcast(broadcast);
    }

    public Run startNewRun(){
        Run run = insertRun();
        startTrackingRun(run);
        return run;
    }

    public void startTrackingRun(Run run){
        mCurrentRunId = run.getId();
        mPrefs.edit()
                .putLong(PREFS_CURRENT_RUN_ID,mCurrentRunId)
                .apply();
        startLocationUpdate();
    }

    public void stopRun(){
        stopLocationUpdate();
        mCurrentRunId = -1;
        mPrefs.edit().remove(PREFS_CURRENT_RUN_ID).apply();
    }

    private Run insertRun(){
        Run run = new Run();
        run.setId(mHelper.insertRun(run));
        return run;
    }

    public void insertLocation(Location loc){
        if(mCurrentRunId != -1){
            mHelper.insertLocation(mCurrentRunId,loc);
        }else{
            Log.e(TAG , " Location received with no tracking run; ignoring");
        }
    }

    public RunDatabaseHelper.RunCursor queryRuns(){
        return mHelper.queryRuns();
    }

    public Run getRun(long id){
        Run run = null;
        RunDatabaseHelper.RunCursor cursor = mHelper.queryRun(id);
        cursor.moveToFirst();

        if(!cursor.isAfterLast()){
            run = cursor.getRun();
        }
        cursor.close();
        return run;
    }

    public boolean isTrackingRun(Run run){
        return run != null && run.getId() == mCurrentRunId;
    }
}
