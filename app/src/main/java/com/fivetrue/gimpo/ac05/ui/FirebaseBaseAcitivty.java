package com.fivetrue.gimpo.ac05.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.firebase.model.ChatMessage;
import com.fivetrue.gimpo.ac05.firebase.model.ScrapContent;
import com.fivetrue.gimpo.ac05.service.FirebaseService;
import com.fivetrue.gimpo.ac05.service.IFirebaseService;
import com.fivetrue.gimpo.ac05.service.IFirebaseServiceCallback;

/**
 * Created by kwonojin on 2016. 10. 27..
 */

public class FirebaseBaseAcitivty extends BaseActivity {

    private static final String TAG = "FirebaseBaseAcitivty";

    private IFirebaseService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, FirebaseService.class);
        intent.setAction(FirebaseService.ACTION_BIND_SERVICE);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mService != null){
            unbindService(serviceConnection);
        }
    }

    protected void onBindService(IFirebaseService service){

    }

    protected void onUnbindService(){

    }

    protected void onReceivedPublicChat(String key, ChatMessage msg){

    }

    protected void onReceivedDistrictChat(String key, ChatMessage msg){

    }

    protected void onReceivedPersonChat(String key, ChatMessage msg){

    }

    protected void onReceivedScrapContent(ScrapContent scrapContent){

    }

    protected void onRefreshData(){

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IFirebaseService.Stub.asInterface(service);
            try {
                mService.registerCallback(serviceCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            onBindService(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                mService.unregisterCallback(serviceCallback);
            } catch (RemoteException e) {
                Log.w(TAG, "onServiceDisconnected: ", e);
            }finally {
                mService = null;
            }
            onUnbindService();
        }
    };

    private IFirebaseServiceCallback.Stub serviceCallback = new IFirebaseServiceCallback.Stub() {
        @Override
        public void onReceivedChat(int type, String key, ChatMessage msg) throws RemoteException {
            if(type == Constants.PUBLIC_CHATTING_ID){
                FirebaseBaseAcitivty.this.onReceivedPublicChat(key, msg);
            }else if(type == Constants.DISTRICT_CHATTING_ID){
                FirebaseBaseAcitivty.this.onReceivedDistrictChat(key, msg);
            }else if(type == Constants.PERSON_MESSAGE_ID){
                FirebaseBaseAcitivty.this.onReceivedPersonChat(key, msg);
            }
        }

        @Override
        public void onReceivedScrapContent(ScrapContent scrapContent) throws RemoteException {
            FirebaseBaseAcitivty.this.onReceivedScrapContent(scrapContent);
        }

        @Override
        public void onRefreshData() throws RemoteException {
            FirebaseBaseAcitivty.this.onRefreshData();
        }
    };
}
