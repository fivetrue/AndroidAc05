package com.fivetrue.gimpo.ac05;

import android.app.Application;

import com.fivetrue.gimpo.ac05.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.manager.NaverApiManager;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;

/**
 * Created by kwonojin on 16. 6. 2..
 */
public class ApplicationEX extends Application {


    private AppConfig mAppConfig = null;

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());
        NetworkManager.init(this);
        ImageLoadManager.init(NetworkManager.getInstance().getRequestQueue());
//        UserInfoManager.init(this);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        Session.initialize(this, AuthType.KAKAO_TALK);
    }

    public AppConfig getAppConfig() {
        return mAppConfig;
    }

    public void setAppConfig(AppConfig config) {
        this.mAppConfig = config;
    }

}
