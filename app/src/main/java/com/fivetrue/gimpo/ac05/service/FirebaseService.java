package com.fivetrue.gimpo.ac05.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.firebase.MessageData;
import com.fivetrue.gimpo.ac05.database.ChatLocalDB;
import com.fivetrue.gimpo.ac05.firebase.database.ChatDatabase;
import com.fivetrue.gimpo.ac05.firebase.database.ScrapContentDatabase;
import com.fivetrue.gimpo.ac05.firebase.database.MessageBoxDatabase;
import com.fivetrue.gimpo.ac05.firebase.database.TownNewsDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.ChatMessage;
import com.fivetrue.gimpo.ac05.firebase.model.ScrapContent;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.MainActivity;
import com.fivetrue.gimpo.ac05.ui.ChattingActivity;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.fivetrue.gimpo.ac05.ui.PersonalActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by kwonojin on 2016. 10. 13..
 */

public class FirebaseService extends Service{

    private static final String TAG = "FirebaseChattingService";

    public static final String ACTION_BIND_SERVICE = "com.fivetrue.gimpo.ac05.bind.chatting";
    public static final String ACTION_DELETE_NOTIFICATION_MESSAGE = "com.fivetrue.gimpo.ac05.delete.message";
    public static final String ACTION_FIREBASE_MESSAGE = "com.fivetrue.gimpo.ac05.firebase.message";

    private static final long VALID_NOTIFICATION_GAP = 1000 * 60;


    private ChatDatabase mPublicChatDB;
    private ChatDatabase mDistricChatDB;
    private MessageBoxDatabase mMessageBoxDatabase;
    private TownNewsDatabase mTownNewsDatabase;

    private ChatLocalDB mChatLocalDB;

    private RemoteCallbackList<IFirebaseServiceCallback> mCallbacks = new RemoteCallbackList<>();

    private ConfigPreferenceManager mConfigPref;
    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");
        initData();

        mPublicChatDB.getReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded() called with: dataSnapshot = [" + dataSnapshot + "], s = [" + s + "]");
                String key = dataSnapshot.getKey();
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                onReceivedChatMessage(Constants.PUBLIC_CHATTING_ID, key, chatMessage);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mChatLocalDB = new ChatLocalDB(this);

        /**
         * Chatting
         */
        mPublicChatDB = new ChatDatabase(String.valueOf(Constants.PUBLIC_CHATTING_ID));
        updateDistrictChatting();

        if(mConfigPref.getUserInfo() != null){
            mMessageBoxDatabase = new MessageBoxDatabase(mConfigPref.getUserInfo().uid);
            mMessageBoxDatabase.getPersonReference().addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String key = dataSnapshot.getKey();
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    onReceivedChatMessage(Constants.PERSON_MESSAGE_ID, key, chatMessage);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    mChatLocalDB.removeChatMessage(Constants.PERSON_MESSAGE_ID, dataSnapshot.getKey());
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void updateDistrictChatting(){
        User user = mConfigPref.getUserInfo();
        if(user != null && user.district > 0){
            if(mDistricChatDB != null){
                mDistricChatDB.getReference().removeEventListener(districtChattingChildEventListener);
            }
            mDistricChatDB = new ChatDatabase(Constants.DISTRICT_CHATTING_ID + "/" + user.district);
            mDistricChatDB.getReference().addChildEventListener(districtChattingChildEventListener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(intent != null && ACTION_BIND_SERVICE.equals(intent.getAction())){
            return chattingService;
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getAction() != null){
            String action = intent.getAction();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private synchronized void onReceivedChatMessage(int type, String key, ChatMessage msg){
        Log.d(TAG, "onReceivedChatMessage() called with: msg = [" + msg + "]");
        if(!mChatLocalDB.existsChatMessage(type, key)){
            mChatLocalDB.putChatMessage(type, key, msg);
            showNotification(type, key, msg);

            if(type == Constants.PUBLIC_CHATTING_ID || type == Constants.DISTRICT_CHATTING_ID){
                mCallbacks.beginBroadcast();
                for(int i = 0 ; i < mCallbacks.getRegisteredCallbackCount() ; i++){
                    try {
                        mCallbacks.getBroadcastItem(i).onReceivedChat(type, key, msg);
                    } catch (RemoteException e) {
                        Log.w(TAG, "onReceivedChatMessage: ", e);
                    }
                }
                mCallbacks.finishBroadcast();
            }
        }
    }

    private void showNotification(final int type, String key, final MessageData message){

        /**
         * 채팅방에서는 Notification 울리지 않음
         */
        boolean canPush = true;
        if(message.getUpdateTime() + VALID_NOTIFICATION_GAP < System.currentTimeMillis()){
            return;
        }

        if(type == Constants.PUBLIC_CHATTING_ID || type == Constants.DISTRICT_CHATTING_ID){
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            if(cn.getClassName().equals(ChattingActivity.class.getName())){
                return;
            }

            boolean isPush = DefaultPreferenceManager.getInstance(FirebaseService.this).isPushChatting(type);
            canPush = message != null && !message.getUser().uid.equals(mConfigPref.getUserInfo().uid) && isPush;
        }else if(type == Constants.PERSON_MESSAGE_ID){
            canPush = DefaultPreferenceManager.getInstance(FirebaseService.this).isPushChatting(type);
        }else if(type == Constants.NOTIFY_MESSASGE_ID){
            boolean isPush = DefaultPreferenceManager.getInstance(this).isPushService();
            if(message != null && message.getUser() != null){
                canPush = message != null && !message.getUser().uid.equals(mConfigPref.getUserInfo().uid) && isPush;
            }else{
                canPush = isPush;
            }
        }

        if(canPush){
            if(message.getImageUrl() != null){
                ImageLoadManager.getInstance().loadImageUrl(message.getImageUrl(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if(response != null){
                            showNotification(type, message, response.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, "onErrorResponse: ", error);
                    }
                });
            }else{
                showNotification(type, message, null);
            }
        }
    }

    private void showNotification(int type, MessageData message, Bitmap bm){
        if(message != null){
            String title = "";
            String content = "";
            String summary = "";
            Intent intent = new Intent();
            switch (type){
                case Constants.PUBLIC_CHATTING_ID:
                case Constants.DISTRICT_CHATTING_ID:
                    title = type == Constants.PUBLIC_CHATTING_ID
                            ? getString(R.string.talk)
                            : String.format(getString(R.string.district_talk), String.valueOf(mConfigPref.getUserInfo().district));
                    content = message.getMessage();
                    summary = message.getUser().getDisplayName();
                    intent.setClass(FirebaseService.this, ChattingActivity.class);
                    intent.putExtra("type", type);
                    break;

                case Constants.PERSON_MESSAGE_ID:
                    title = getString(R.string.person_alarm);
                    content = getString(R.string.received_messages_from_user);
                    summary = message.getUser().getDisplayName();
                    intent.setClass(FirebaseService.this, PersonalActivity.class);
                    break;
            }

            if(bm == null || bm.isRecycled()){
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            }
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(title)
                    .setSummaryText(summary)
                    .bigText(content);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(FirebaseService.this);
            builder.setColor(getResources().getColor(R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.push_icon)
                    .setLargeIcon(bm)
                    .setContentText(content)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setStyle(bigTextStyle);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                builder.setVibrate(new long[0]);
            }
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
            Intent parentIntent = new Intent(this, MainActivity.class);
            parentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = taskStackBuilder.addNextIntent(parentIntent)
                .addNextIntent(intent)
                .getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);


//            PendingIntent pendingIntent = PendingIntent.getActivity(FirebaseService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            mNotificationManager.notify(type, builder.build());
        }
    }

    private IFirebaseService.Stub chattingService = new IFirebaseService.Stub() {

        @Override
        public boolean registerCallback(IFirebaseServiceCallback callback) throws RemoteException {
            updateDistrictChatting();
            return mCallbacks.register(callback);
        }

        @Override
        public boolean unregisterCallback(IFirebaseServiceCallback callback) throws RemoteException {
            return mCallbacks.unregister(callback);
        }

    };

    private ChildEventListener districtChattingChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "onChildAdded() called with: dataSnapshot = [" + dataSnapshot + "], s = [" + s + "]");
            String key = dataSnapshot.getKey();
            ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
            onReceivedChatMessage(Constants.DISTRICT_CHATTING_ID, key, chatMessage);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            mChatLocalDB.removeChatMessage(Constants.DISTRICT_CHATTING_ID, dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
