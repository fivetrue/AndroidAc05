package com.fivetrue.gimpo.ac05;

import android.app.Application;

import com.fivetrue.gimpo.ac05.analytics.GoogleAnalytics;
import com.fivetrue.gimpo.ac05.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;
import com.fivetrue.gimpo.ac05.vo.user.District;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 2..
 */
public class ApplicationEX extends Application {


    private AppConfig mAppConfig = null;
    private ArrayList<District> mDistricts = null;

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());
        NetworkManager.init(this);
        ImageLoadManager.init(NetworkManager.getInstance().getRequestQueue());
        GoogleAnalytics.init(this);
    }

    public AppConfig getAppConfig() {
        return mAppConfig;
    }

    public void setAppConfig(AppConfig config) {
        this.mAppConfig = config;
    }

    public void setDistricts(ArrayList<District> districts){
        this.mDistricts = districts;
    }

    public ArrayList<District> getDistricts(){
        return mDistricts;
    }

}
