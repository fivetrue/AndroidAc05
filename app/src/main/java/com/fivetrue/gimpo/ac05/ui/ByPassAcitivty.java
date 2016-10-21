package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;

import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.gimpo.ac05.service.FirebaseService;
import com.fivetrue.gimpo.ac05.service.notification.NotificationHelper;

/**
 * Created by kwonojin on 2016. 10. 18..
 */

public class ByPassAcitivty extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent() != null && getIntent().getAction() != null){
            String action = getIntent().getAction();
            if(action.equals(NotificationHelper.ACTION_NOTIFICATION)){
                Intent intent = new Intent(getIntent());
                intent.setClass(this, SplashActivity.class);
                startActivity(intent);
            }else if(action.equals(FirebaseService.ACTION_FIREBASE_MESSAGE)){
                DeepLinkManager.goLink(this, getIntent());
            }
        }
        finish();
    }
}
