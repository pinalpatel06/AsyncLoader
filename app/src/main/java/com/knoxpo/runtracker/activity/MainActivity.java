package com.knoxpo.runtracker.activity;


import android.support.v4.app.Fragment;

import com.knoxpo.runtracker.fragment.MainFragment;

public class MainActivity extends ToolbarActivity {
    public static final String EXTRA_RUN_ID = "com.knoxpo.runtracker.run_id";
    @Override
    public Fragment getContentFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID,-1);
        if(runId != -1){
            return new MainFragment().newInstance(runId);
        }else{
            return new MainFragment();
        }

    }
}
