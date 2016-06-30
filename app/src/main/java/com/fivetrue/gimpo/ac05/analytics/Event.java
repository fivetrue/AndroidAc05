package com.fivetrue.gimpo.ac05.analytics;

import android.text.TextUtils;
import android.util.Pair;

import com.fivetrue.gimpo.ac05.utils.Log;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 26..
 */
public enum Event implements Runnable{



    AppConfigNull,

    EnterSplashActivity_LoginFailed,
    EnterSplashActivity_Notification,

    EnterMainActivity,
    ExitAppByBackPress,

    EnterNoticeActivity,
    ClickNoticeData,

    EnterSettingActivity,
    ClickSettingMenu_MyInfo,
    ClickSettingMenu_MyCafeInfo,
    ClickSettingMenu_Logout,
    ClickSettingMenu_NotificationOff,
    ClickSettingMenu_NotificationOn,

    EnterUserInfoInputActivity,
    ClickUserInfoInput_SelectedDistrict,
    ClickUserInfoInput_FinishUserInput,

    ClickLeftMenu,
    ClickLeftMenu_Main,
    ClickLeftMenu_Cafe,
    ClickLeftMenu_Notification,
    ClickLeftMenu_Setting,

    EnterPageDataDetailFragment,

    EnterWebviewFragment,

    ;

    private static final String TAG = "Event";

    private ArrayList<Pair<String,String>> params = new ArrayList<>();

    private String mName = null;

    private Event(String key, String value){
        params.add(new Pair<java.lang.String, java.lang.String>(key, value));
    }

    private Event(){

    }

    public Event addParams(String key, String value){
        params.add(new Pair<java.lang.String, java.lang.String>(key, value));
        return this;
    }

    public ArrayList<Pair<String,String>> getParams(){
        return params;
    }

    @Override
    public void run() {
        Log.i(TAG, "run: " + name() + " / " + params.toString());
    }
}
