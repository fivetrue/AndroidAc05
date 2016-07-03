package com.fivetrue.gimpo.ac05.vo;

import android.app.Activity;
import android.graphics.Bitmap;

/**
 * Created by Fivetrue on 2016-02-15.
 */
public class LeftMenu {

    private String name = null;
    private int icon = 0;
    private Class< ? extends Activity> activity = null;
    private int flag = -1;

    public LeftMenu(String name, int imageResource, Class<? extends Activity> activityCls, int flag){
        this.name = name;
        this.icon = imageResource;
        this.activity = activityCls;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public Class<? extends Activity> getActivity() {
        return activity;
    }

    public int getFlag() {
        return flag;
    }
}
