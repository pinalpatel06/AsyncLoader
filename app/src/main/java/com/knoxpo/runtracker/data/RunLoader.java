package com.knoxpo.runtracker.data;

import android.content.Context;

import com.knoxpo.runtracker.model.Run;
import com.knoxpo.runtracker.model.RunManager;

/**
 * Created by Tejas Sherdiwala on 12/6/2016.
 * &copy; Knoxpo
 */

public class RunLoader extends DataLoader<Run> {
    private long mRunId;

    public RunLoader(Context context , long runId){
        super(context);
        mRunId = runId;
    }

    @Override
    public Run loadInBackground() {
        return RunManager.get(getContext()).getRun(mRunId);
    }
}
