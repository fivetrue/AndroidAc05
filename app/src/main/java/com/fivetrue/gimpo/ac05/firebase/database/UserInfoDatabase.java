package com.fivetrue.gimpo.ac05.firebase.database;

import android.content.Context;

import com.fivetrue.gimpo.ac05.firebase.BaseFirebaseReference;
import com.fivetrue.gimpo.ac05.firebase.model.AppConfig;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public class UserInfoDatabase extends BaseFirebaseReference<User> {

    public static final String PATH = "/user/%s";

    public UserInfoDatabase(String uid) {
        super(String.format(PATH, uid));
    }

    @Override
    protected DatabaseReference getUpdateTimeRef() {
        return getReference().child("updateTime").getRef();
    }

    public Task<Void> updateDistrict(int district){
        return getReference().child("district").getRef().setValue(district);
    }

    public Task<Void> updateNickname(String nickName){
        return getReference().child("nickName").getRef().setValue(nickName);
    }

    public Query validNickname(String nickName){
        return getReference().getParent().orderByChild("nickName").equalTo(nickName);
    }
}
