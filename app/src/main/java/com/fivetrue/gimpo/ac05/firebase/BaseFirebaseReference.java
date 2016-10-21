package com.fivetrue.gimpo.ac05.firebase;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public class BaseFirebaseReference <T extends FirebaseData> {

    private static final String TAG = "BaseFirebaseReference";


    private FirebaseDatabase firebaseDatabase;
    private String path;


    public BaseFirebaseReference(String path){
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this. path = path;
    }

    public DatabaseReference getReference(){
        return firebaseDatabase.getReference(path);
    }

    public Task<Void> pushData(T data){
        return getReference().push().setValue(data.getValues());
    }

    public Task<Void> putData(String key, T data){
        return getReference().child(key).setValue(data.getValues());
    }

    protected DatabaseReference getUpdateTimeRef(){
        throw new IllegalStateException("If you call updateTime(), do overriding getUpdateTimeRef()");
    }

    public void updateTime(){
        getUpdateTimeRef().setValue(ServerValue.TIMESTAMP);
    }
}
