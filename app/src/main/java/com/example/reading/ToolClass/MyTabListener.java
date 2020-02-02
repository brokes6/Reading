package com.example.reading.ToolClass;

import android.app.ActionBar;
import android.app.Activity;

import android.app.FragmentTransaction;

import androidx.fragment.app.Fragment;


public class MyTabListener implements ActionBar.TabListener {
    private final Activity activity;
    private final Class aClass;
    private Fragment fragment;

    public MyTabListener(Activity activity, Class aClass) {
        this.activity = activity;
        this.aClass = aClass;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (fragment == null){
            fragment = fragment.instantiate(activity,aClass.getName());
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
