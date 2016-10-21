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
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.chatting.ChatMessage;
import com.fivetrue.gimpo.ac05.chatting.GalleryMessage;
import com.fivetrue.gimpo.ac05.chatting.IChattingCallback;
import com.fivetrue.gimpo.ac05.chatting.IChattingService;
import com.fivetrue.gimpo.ac05.chatting.MessageData;
import com.fivetrue.gimpo.ac05.database.ChatMessageDatabase;
import com.fivetrue.gimpo.ac05.database.GalleryMessageDatabase;
import com.fivetrue.gimpo.ac05.firebase.database.UserMessageBoxDatabase;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.ByPassAcitivty;
import com.fivetrue.gimpo.ac05.ui.ChattingActivity;
import com.fivetrue.gimpo.ac05.ui.DeepLinkManager;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kwonojin on 2016. 10. 13..
 */

public class FirebaseService extends Service{

    private static final String TAG = "FirebaseChattingService";

    public static final String ACTION_BIND_SERVICE = "com.fivetrue.gimpo.ac05.bind.chatting";
    public static final String ACTION_DELETE_NOTIFICATION_MESSAGE = "com.fivetrue.gimpo.ac05.delete.message";
    public static final String ACTION_FIREBASE_MESSAGE = "com.fivetrue.gimpo.ac05.firebase.message";

    private DatabaseReference mChattingDBReference;
    private DatabaseReference mPublicDBReference;
    private DatabaseReference mDistrictDBReference;
    private DatabaseReference mGalleryDBReference;

    private UserMessageBoxDatabase mUserMessageBoxDatabase;

    private RemoteCallbackList<IChattingCallback> mCallbacks = new RemoteCallbackList<>();

    private ConfigPreferenceManager mConfigPref;
    private ChatMessageDatabase mChatMessageDatabase;
    private GalleryMessageDatabase mGalleryMessageDatabase;

    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");
        initData();

        mPublicDBReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded() called with: dataSnapshot = [" + dataSnapshot + "], s = [" + s + "]");
                String key = dataSnapshot.getKey();
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                onReceivedChatMessage(Constants.PUBLIC_CHATTING_NOTIFICATION_ID, key, chatMessage);
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
        mChatMessageDatabase = new ChatMessageDatabase(this);
        mGalleryMessageDatabase = new GalleryMessageDatabase(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        /**
         * Chatting
         */
        mChattingDBReference = database.getReference(Constants.FIREBASE_DB_ROOT_CHATTING);
        mPublicDBReference = mChattingDBReference.child(Constants.FIREBASE_DB_CHATTING_PUBLIC);
        updateDistrictChatting();

        /**
         * Gallery
         */
        mGalleryDBReference = database.getReference(Constants.FIREBASE_DB_ROOT_GALLERY);
        mGalleryDBReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded() called with: dataSnapshot = [" + dataSnapshot + "], s = [" + s + "]");
                String key = dataSnapshot.getKey();
                GalleryMessage galleryMessage = dataSnapshot.getValue(GalleryMessage.class);
                onReceivedGallery(Constants.GALLERY_NOTIFICATION_ID, key, galleryMessage);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mGalleryMessageDatabase.removeGalleryMessage(Constants.GALLERY_NOTIFICATION_ID, dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(mConfigPref.getUserInfo() != null){
            mUserMessageBoxDatabase = new UserMessageBoxDatabase(mConfigPref.getUserInfo().uid);
            mUserMessageBoxDatabase.getReference().addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String key = dataSnapshot.getKey();
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    onReceivedChatMessage(Constants.PERSON_MESSAGE_NOTIFICATION_ID, key, chatMessage);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    mChatMessageDatabase.removeChatMessage(Constants.PERSON_MESSAGE_NOTIFICATION_ID, dataSnapshot.getKey());
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
        if(user != null && user.district > 0 && mChattingDBReference != null){
            mDistrictDBReference = mChattingDBReference.child(Constants.FIREBASE_DB_CHATTING_DISTRICT).child(user.district + "");
            mDistrictDBReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildAdded() called with: dataSnapshot = [" + dataSnapshot + "], s = [" + s + "]");
                    String key = dataSnapshot.getKey();
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    onReceivedChatMessage(Constants.DISTRICT_CHATTING_NOTIFICATION_ID, key, chatMessage);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    mChatMessageDatabase.removeChatMessage(Constants.DISTRICT_CHATTING_NOTIFICATION_ID, dataSnapshot.getKey());
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

    public void sendMessage(int type, ChatMessage msg){
        if(type == Constants.PUBLIC_CHATTING_NOTIFICATION_ID){
            mPublicDBReference.push().setValue(msg.getValues(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Log.d(TAG, "onComplete() called with: databaseError = [" + databaseError + "], databaseReference = [" + databaseReference + "]");
                }
            });
        }else{
            mDistrictDBReference.push().setValue(msg.getValues(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Log.d(TAG, "onComplete() called with: databaseError = [" + databaseError + "], databaseReference = [" + databaseReference + "]");
                }
            });
        }
    }

    private synchronized void onReceivedChatMessage(int type, String key, ChatMessage msg){
        Log.d(TAG, "onReceivedChatMessage() called with: msg = [" + msg + "]");
        if(!mChatMessageDatabase.existsChatMessage(type, key)){
            mChatMessageDatabase.putChatMessage(type, key, msg);
            showNotification(type, key, msg);

            if(type == Constants.PUBLIC_CHATTING_NOTIFICATION_ID || type == Constants.DISTRICT_CHATTING_NOTIFICATION_ID){
                mCallbacks.beginBroadcast();
                for(int i = 0 ; i < mCallbacks.getRegisteredCallbackCount() ; i++){
                    try {
                        mCallbacks.getBroadcastItem(i).onReceivedMessage(type, key, msg);
                    } catch (RemoteException e) {
                        Log.w(TAG, "onReceivedChatMessage: ", e);
                    }
                }
                mCallbacks.finishBroadcast();
            }
        }
    }

    private synchronized void onReceivedGallery(int type, String key, GalleryMessage msg){
        Log.d(TAG, "onReceivedChatMessage() called with: msg = [" + msg + "]");
        if(!mGalleryMessageDatabase.existsChatMessage(key)){
            mGalleryMessageDatabase.putGalleryMessage(type, key, msg);
            showNotification(type, key, msg);
        }
    }

    private void showNotification(final int type, String key, final MessageData message){

        /**
         * 채팅방에서는 Notification 울리지 않음
         */
        boolean canPush = true;

        if(type == Constants.PUBLIC_CHATTING_NOTIFICATION_ID || type == Constants.DISTRICT_CHATTING_NOTIFICATION_ID){
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            if(cn.getClassName().equals(ChattingActivity.class.getName())){
                return;
            }

            boolean isPush = DefaultPreferenceManager.getInstance(FirebaseService.this).isPushChatting(type);
            canPush = message != null && !message.getUser().uid.equals(mConfigPref.getUserInfo().uid) && isPush;
        }else if(type == Constants.PERSON_MESSAGE_NOTIFICATION_ID){
            canPush = DefaultPreferenceManager.getInstance(FirebaseService.this).isPushChatting(type);
        }else if(type == Constants.GALLERY_NOTIFICATION_ID){

        }

        if(canPush){
            ImageLoadManager.getInstance().loadImageUrl(message.getUser().profileImage, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    String title = "";
                    String content = "";
                    String summary = "";
                    switch (type){
                        case Constants.PUBLIC_CHATTING_NOTIFICATION_ID :
                            title = getString(R.string.talk);
                            content = message.getMessage();
                            summary = message.getUser().getDisplayName();
                            break;
                        case Constants.DISTRICT_CHATTING_NOTIFICATION_ID :
                            int district = mConfigPref.getUserInfo().district;
                            title = String.format(getString(R.string.district_talk), String.valueOf(district));
                            content = message.getMessage();
                            summary = message.getUser().getDisplayName();
                            break;

                        case Constants.GALLERY_NOTIFICATION_ID :
                            title = getString(R.string.camera_photo);
                            content = getString(R.string.upload_new_photo_from_user);
                            summary = message.getMessage();
                            break;

                        case Constants.PERSON_MESSAGE_NOTIFICATION_ID :
                            title = getString(R.string.person_alarm);
                            content = getString(R.string.received_messages_from_user);
                            summary = message.getUser().getDisplayName();
                            break;

                    }

                    Bitmap bm = null;
                    if(response != null && response.getBitmap() != null
                            && !response.getBitmap().isRecycled()){
                        bm = response.getBitmap();
                    }

                    if(bm == null){
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

                    Intent intent = new Intent(FirebaseService.this, ByPassAcitivty.class);
                    intent.setAction(ACTION_FIREBASE_MESSAGE);
                    intent.setData(DeepLinkManager.makeFirebaseNotification(type));
                    PendingIntent pendingIntent = PendingIntent.getActivity(FirebaseService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);
                    mNotificationManager.notify(type, builder.build());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w(TAG, "onErrorResponse: ", error);
                }
            });
        }
    }

    private IChattingService.Stub chattingService = new IChattingService.Stub() {

        @Override
        public boolean registerCallback(int type, IChattingCallback callback) throws RemoteException {
            updateDistrictChatting();
            return mCallbacks.register(callback);
        }

        @Override
        public boolean unregisterCallback(IChattingCallback callback) throws RemoteException {
            return mCallbacks.unregister(callback);
        }

        @Override
        public void sendMessage(int type, ChatMessage msg) throws RemoteException {
            FirebaseService.this.sendMessage(type, msg);
        }

    };
}
