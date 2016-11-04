package com.fivetrue.gimpo.ac05.firebase.database;

import com.fivetrue.gimpo.ac05.firebase.BaseFirebaseReference;
import com.fivetrue.gimpo.ac05.firebase.model.AppMessage;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public class AppMessageDatabase extends BaseFirebaseReference<AppMessage> {

    public static final String PATH = "/static/message";

    public AppMessageDatabase() {
        super(PATH);
    }

    @Override
    protected DatabaseReference getUpdateTimeRef() {
        return getReference().child("updateTime").getRef();
    }
}
