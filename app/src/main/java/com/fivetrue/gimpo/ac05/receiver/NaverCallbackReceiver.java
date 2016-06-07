package com.fivetrue.gimpo.ac05.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fivetrue.gimpo.ac05.utils.Log;

/**
 * Created by kwonojin on 16. 6. 3..
 */
public class NaverCallbackReceiver extends BroadcastReceiver {

    private static final String TAG = "NaverCallbackReceiver";

    public static final String ACTION_NAVER_CALLBACK_INTENT = "com.fivetrue.gimpo.ac05.naver.callback.intent";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(context != null && intent != null){
            String action = intent .getAction();
            Log.i(TAG, "action = " + action);
            if(action != null){

            }
        }
    }
}
