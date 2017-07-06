package com.knoxpo.runtracker.activity;

import android.support.v4.app.Fragment;

import com.knoxpo.runtracker.fragment.RunListFragment;

/**
 * Created by Tejas Sherdiwala on 12/6/2016.
 * &copy; Knoxpo
 */

public class RunListActivity extends ToolbarActivity {

    @Override
    public Fragment getContentFragment() {
        return new RunListFragment();
    }
}
