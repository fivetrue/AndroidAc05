package com.fivetrue.gimpo.ac05.firebase.database;

import android.content.Context;

import com.fivetrue.gimpo.ac05.firebase.BaseFirebaseReference;
import com.fivetrue.gimpo.ac05.firebase.model.AppConfig;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public class AppConfigDatabase extends BaseFirebaseReference<AppConfig> {

    public static final String PATH = "/config/app";

    public AppConfigDatabase() {
        super(PATH);
    }

    @Override
    protected DatabaseReference getUpdateTimeRef() {
        return getReference().child("updateTime").getRef();
    }

}
