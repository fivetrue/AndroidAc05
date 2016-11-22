package com.fivetrue.gimpo.ac05.preferences;

import android.content.Context;

import com.fivetrue.fivetrueandroid.preferences.SharedPreferenceHelper;
import com.fivetrue.gimpo.ac05.firebase.model.AppConfig;
import com.fivetrue.gimpo.ac05.firebase.model.AppMessage;
import com.fivetrue.gimpo.ac05.firebase.model.District;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by ojin.kwon on 2016-02-02.
 */
public class ConfigPreferenceManager {

    private static final String PREF_NAME = "config";

    private static final String GCM_DEVICE = "gcm_dvice_id";
    private static final String APP_CONFIG = "app_config";
    private static final String APP_MESSAGE = "app_message";
    private static final String APP_WEB_CACHE = "app_web_cache";

    private SharedPreferenceHelper mHelper = null;

    private Gson mGson = null;

    public ConfigPreferenceManager(Context context){
        mHelper = new SharedPreferenceHelper(context, PREF_NAME);
        mGson = new Gson();
    }

    public void setGcmDeviceId(String id){
        if(id != null){
            mHelper.putData(GCM_DEVICE, id);
        }
    }

    public String getGcmDeviceId(){
        return mHelper.getData(GCM_DEVICE, null);
    }

    public void setUserInfo(User userinfo){
        if(userinfo != null){
            mHelper.putData(User.class.getName(), mGson.toJson(userinfo));
        }else{
            mHelper.putData(User.class.getName(), null);
        }
    }

    public User getUserInfo(){
        User userinfo = null;
        String info = mHelper.getData(User.class.getName(), null);
        if(info != null){
            userinfo = mGson.fromJson(info, User.class);
        }
        return userinfo;
    }

    public void setAppConfig(AppConfig config){
        if(config != null){
            mHelper.putData(APP_CONFIG, mGson.toJson(config));
        }else{
            mHelper.putData(APP_CONFIG, null);
        }
    }

    public AppConfig getAppConfig(){
        AppConfig config = null;
        try{
            String data = mHelper.getData(APP_CONFIG, null);
            if(data != null){
                config = mGson.fromJson(data, AppConfig.class);
            }
        }catch (Exception e){
            mHelper.putData(APP_CONFIG, null);
        }
        return config;
    }

    public void setAppMessage(AppMessage message){
        if(message != null){
            mHelper.putData(APP_MESSAGE, mGson.toJson(message));
        }else{
            mHelper.putData(APP_MESSAGE, null);
        }
    }

    public AppMessage getAppMessage(){
        AppMessage message = null;
        String data = mHelper.getData(APP_MESSAGE, null);
        if(data != null){
            message = mGson.fromJson(data, AppMessage.class);
        }
        return message;
    }

    public boolean isExpiredWebCache(){
        long lastTime = mHelper.getData(APP_WEB_CACHE, 0);
        long currentTime = System.currentTimeMillis();
        return (lastTime + (60000 * 60 * 24 * 7)) < currentTime;
    }

    public void updateWebCacheTime(){
        mHelper.putData(APP_WEB_CACHE, System.currentTimeMillis());
    }
}
