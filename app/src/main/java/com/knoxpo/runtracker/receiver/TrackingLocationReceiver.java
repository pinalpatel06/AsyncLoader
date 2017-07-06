package com.knoxpo.runtracker.receiver;

import android.content.Context;
import android.location.Location;

import com.knoxpo.runtracker.model.RunManager;

/**
 * Created by Tejas Sherdiwala on 12/6/2016.
 * &copy; Knoxpo
 */

public class TrackingLocationReceiver extends LocationReceiver {

    @Override
    protected void onLocationReceived(Context context, Location loc) {
        RunManager.get(context).insertLocation(loc);
    }
}
