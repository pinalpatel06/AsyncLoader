package com.knoxpo.runtracker.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.knoxpo.runtracker.R;

/**
 * Created by Tejas Sherdiwala on 12/5/2016.
 * &copy; Knoxpo
 */

public abstract class ToolbarActivity extends SingleFragementActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        setSupportActionBar(mToolbar);
    }

    private void init(){
        mToolbar = (Toolbar) findViewById(getToolbarId());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_toolbar;
    }

    public int getToolbarId() {
        return R.id.toolbar;
    }
}
