package com.fivetrue.gimpo.ac05.preferences;

import android.content.Context;

/**
 * Created by ojin.kwon on 2016-02-02.
 */
public class ConfigPreferenceManager {

    private static final String PREF_NAME = "config";

    private static final String VERSION_NUMBER = "_version_number";
    private static final String GCM_DEVICE = "gcm_dvice_id";

    private static final String SETTING_PUSH = "setting_push";
    private static final String USER_INFO = "user_info";

    private SharedPreferenceHelper mHelper = null;

    public ConfigPreferenceManager(Context context){
        mHelper = new SharedPreferenceHelper(context, PREF_NAME);
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
        return mHelper.getData(SETTING_PUSH, false);
    }

    public void setNaverUserInfo(String xml){
        if(xml != null){
            mHelper.putData(USER_INFO, xml);
        }
    }

    public String getNaverUserInfo(){
        return mHelper.getData(USER_INFO, null);
    }

}
