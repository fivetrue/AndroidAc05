package com.fivetrue.gimpo.ac05.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.chatting.FirebaseChattingService;

/**
 * Created by kwonojin on 2016. 10. 13..
 */

public class ApplicationChecker extends BroadcastReceiver {

    private static final String TAG = "ApplicationChecker";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent != null && intent.getAction() != null){
            String action = intent.getAction();
            if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
                onBootCompleted(context);
            }
        }
    }

    private void onBootCompleted(Context context){
        if(!((ApplicationEX)context.getApplicationContext()).isServiceRunning(FirebaseChattingService.class)){
            Intent intent = new Intent(context.getApplicationContext(), FirebaseChattingService.class);
            context.getApplicationContext().startActivity(intent);
        }
    }
}
