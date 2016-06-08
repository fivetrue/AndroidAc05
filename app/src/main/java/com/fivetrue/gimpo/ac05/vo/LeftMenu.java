package com.fivetrue.gimpo.ac05.vo;

import android.app.Activity;

/**
 * Created by Fivetrue on 2016-02-15.
 */
public class LeftMenu {

    private String name = null;
    private Class< ? extends Activity> activity = null;

    public LeftMenu(String name, Class<? extends Activity> activityCls){
        this.name = name;
        this.activity = activityCls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends Activity> getActivity() {
        return activity;
    }

    public void setActivity(Class<? extends Activity> activity) {
        this.activity = activity;
    }
}
