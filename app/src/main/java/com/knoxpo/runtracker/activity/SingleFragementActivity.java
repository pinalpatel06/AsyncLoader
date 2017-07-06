package com.knoxpo.runtracker.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.knoxpo.runtracker.R;

/**
 * Created by Tejas Sherdiwala on 12/5/2016.
 * &copy; Knoxpo
 */

public abstract class SingleFragementActivity extends AppCompatActivity {
    public abstract Fragment getContentFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(getContainerId());

        if(fragment == null){
            fm
                    .beginTransaction()
                    .replace(getContainerId(),getContentFragment())
                    .commit();
        }
    }

    public int getContentViewId(){
        return R.layout.activity_single_fragment;
    }

    public int getContainerId(){
        return R.id.fragment_container;
    }
}
