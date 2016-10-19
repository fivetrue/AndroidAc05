package com.fivetrue.gimpo.ac05.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fivetrue.gimpo.ac05.R;

/**
 * Created by kwonojin on 2016. 10. 10..
 */

public class DefaultPreferenceManager {

    private static final String TAG = "DefaultPreferenceManage";

    private static DefaultPreferenceManager sInstance = null;

    private SharedPreferences mSharedPreferences = null;

    private Context mContext = null;

    public static DefaultPreferenceManager getInstance(Context context){
        if(sInstance == null){
            sInstance = new DefaultPreferenceManager(context);
        }
        return sInstance;
    }

    private DefaultPreferenceManager(Context context){
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public boolean isPushService(){
        return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_key_push), true);
    }

    public void setPushService(boolean b){
        mSharedPreferences.edit().putBoolean(mContext.getString(R.string.pref_key_push), b).commit();
    }

    public boolean isPushChatting(int type){
        return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_key_push) + "." + type, true);
    }

    public void setPushChatting(int type, boolean b){
        mSharedPreferences.edit().putBoolean(mContext.getString(R.string.pref_key_push) + "." + type, b).commit();
    }
}
