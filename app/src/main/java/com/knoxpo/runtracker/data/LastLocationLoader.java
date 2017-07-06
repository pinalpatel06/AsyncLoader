package com.knoxpo.runtracker.data;

import android.content.Context;
import android.location.Location;

import com.knoxpo.runtracker.model.RunManager;

/**
 * Created by Tejas Sherdiwala on 12/7/2016.
 * &copy; Knoxpo
 */

public class LastLocationLoader extends DataLoader<Location> {
    private long mRunId;

    public LastLocationLoader(Context context,long runId){
        super(context);
        mRunId = runId;
    }

    @Override
    public Location loadInBackground() {
        return RunManager.get(getContext()).getLastLocationForRun(mRunId);
    }
}
