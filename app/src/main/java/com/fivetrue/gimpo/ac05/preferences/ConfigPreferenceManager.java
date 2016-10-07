package com.fivetrue.gimpo.ac05.preferences;

import android.content.Context;

import com.fivetrue.fivetrueandroid.preferences.SharedPreferenceHelper;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;
import com.fivetrue.gimpo.ac05.vo.config.Token;
import com.fivetrue.gimpo.ac05.vo.user.District;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by ojin.kwon on 2016-02-02.
 */
public class ConfigPreferenceManager {

    private static final String PREF_NAME = "config";

    private static final String VERSION_NUMBER = "_version_number";
    private static final String GCM_DEVICE = "gcm_dvice_id";

    private static final String SETTING_PUSH = "setting_push";
    private static final String USER_INFO = "user_info";
    private static final String USER_TOKEN = "user_token";

    private static final String APP_CONFIG = "app_config";
    private static final String DISTRICTS_INFO = "districts_info";

    private static final String FIRST_OPEN = "first_open";

    private SharedPreferenceHelper mHelper = null;

    private Gson mGson = null;

    public ConfigPreferenceManager(Context context){
        mHelper = new SharedPreferenceHelper(context, PREF_NAME);
        mGson = new Gson();
    }

    /**
     * 가장 최근 버전을 pref 에 저장
     * @param versionNumber
     */
    public void setVersionNumber(String name, int versionNumber){
        mHelper.putData(name + VERSION_NUMBER, versionNumber);
    }

    /**
     * 가장 최근의 Config의 버전을 가져온다.
     * @return
     */
    public int getVersionNumber(String name){
        return mHelper.getData(name + VERSION_NUMBER, 0);
    }

    public void setGcmDeviceId(String id){
        if(id != null){
            mHelper.putData(GCM_DEVICE, id);
        }
    }

    public String getGcmDeviceId(){
        return mHelper.getData(GCM_DEVICE, null);
    }

    public void setSettingPush(boolean b){
        mHelper.putData(SETTING_PUSH, b);
    }

    public boolean isSettingPush(){
        return mHelper.getData(SETTING_PUSH, true);
    }

    public void setUserInfo(UserInfo userinfo){
        if(userinfo != null){
            mHelper.putData(USER_INFO, mGson.toJson(userinfo));
        }else{
            mHelper.putData(USER_INFO, null);
        }
    }

    public UserInfo getUserInfo(){
        UserInfo userinfo = null;
        String info = mHelper.getData(USER_INFO, null);
        if(info != null){
            userinfo = mGson.fromJson(info, UserInfo.class);
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

    public ArrayList<District> getDistricts(){
        ArrayList<District> districts = null;
        String data = mHelper.getData(DISTRICTS_INFO, null);
        if(data != null){
            districts = mGson.fromJson(data, new TypeToken<ArrayList<District>>(){}.getType());
        }
        return districts;
    }

    public void setDistricts(ArrayList<District> districts){
        if(districts != null){
            mHelper.putData(DISTRICTS_INFO, mGson.toJson(districts));
        }else{
            mHelper.putData(DISTRICTS_INFO, null);
        }
    }

    public AppConfig getAppConfig(){
        AppConfig config = null;
        String data = mHelper.getData(APP_CONFIG, null);
        if(data != null){
            config = mGson.fromJson(data, AppConfig.class);
        }
        return config;
    }

    public void setToken(Token token){
        if(token != null){
            mHelper.putData(USER_TOKEN, mGson.toJson(token));
        }else{
            mHelper.putData(USER_TOKEN, null);
        }
    }

    public Token getToken(){
        Token token = null;
        String info = mHelper.getData(USER_TOKEN, null);
        if(info != null){
            token = mGson.fromJson(info, Token.class);
        }
        return token;
    }

    public void setFirstOpen(boolean b){
        mHelper.putData(FIRST_OPEN, b);
    }

    public boolean isFirstOpen(){
        return mHelper.getData(FIRST_OPEN, true);
    }

}
