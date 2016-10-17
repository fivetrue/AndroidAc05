package com.fivetrue.gimpo.ac05.chatting;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.service.notification.NotificationHelper;
import com.fivetrue.gimpo.ac05.ui.DeepLinkManager;
import com.fivetrue.gimpo.ac05.ui.SplashActivity;
import com.fivetrue.gimpo.ac05.vo.user.FirebaseUserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by kwonojin on 2016. 10. 13..
 */

public class FirebaseChattingService extends Service{

    private static final String TAG = "FirebaseChattingService";

    public static final String ACTION_BIND_SERVICE = "com.fivetrue.gimpo.ac05.bind.chatting";
    public static final String ACTION_DELETE_NOTIFICATION_MESSAGE = "com.fivetrue.gimpo.ac05.delete.message";

    public static final int PUBLIC_CHATTING_NOTIFICATION_ID = 0x123;
    public static final int DISTRICT_CHATTING_NOTIFICATION_ID = 0x312;

    public static final int GALLERY_NOTIFICATION_ID = 0x333;

    private static final String DB_CHATTING = "chatting";
    private static final String DB_PUBLIC = "public";
    private static final String DB_DISTRICT = "district";
    private static final String DB_GALLERY = "gallery";


    private DatabaseReference mChattingDBReference;
    private DatabaseReference mPublicDBReference;
    private DatabaseReference mDistrictDBReference;
    private DatabaseReference mGalleryDBReference;

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
                onReceivedChatMessage(PUBLIC_CHATTING_NOTIFICATION_ID, key, chatMessage);
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
        mChattingDBReference = database.getReference(DB_CHATTING);
        mPublicDBReference = mChattingDBReference.child(DB_PUBLIC);
        updateDistrictChatting();

        /**
         * Gallery
         */
        mGalleryDBReference = database.getReference(DB_GALLERY);
        mGalleryDBReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded() called with: dataSnapshot = [" + dataSnapshot + "], s = [" + s + "]");
                String key = dataSnapshot.getKey();
                GalleryMessage galleryMessage = dataSnapshot.getValue(GalleryMessage.class);
                onReceivedGallery(GALLERY_NOTIFICATION_ID, key, galleryMessage);
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

    private void updateDistrictChatting(){
        FirebaseUserInfo user = mConfigPref.getUserInfo();
        if(user != null && user.getDistrict() > 0 && mChattingDBReference != null){
            mDistrictDBReference = mChattingDBReference.child(DB_DISTRICT).child(user.getDistrict() + "");
            mDistrictDBReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildAdded() called with: dataSnapshot = [" + dataSnapshot + "], s = [" + s + "]");
                    String key = dataSnapshot.getKey();
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    onReceivedChatMessage(DISTRICT_CHATTING_NOTIFICATION_ID, key, chatMessage);
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
        if(type == PUBLIC_CHATTING_NOTIFICATION_ID){
            mPublicDBReference.push().setValue(msg, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Log.d(TAG, "onComplete() called with: databaseError = [" + databaseError + "], databaseReference = [" + databaseReference + "]");
                }
            });
        }else{
            mDistrictDBReference.push().setValue(msg, new DatabaseReference.CompletionListener() {
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

    private synchronized void onReceivedGallery(int type, String key, GalleryMessage msg){
        Log.d(TAG, "onReceivedChatMessage() called with: msg = [" + msg + "]");
        if(!mGalleryMessageDatabase.existsChatMessage(key)){
            mGalleryMessageDatabase.putGalleryMessage(type, key, msg);
            showNotification(type, key, msg);
        }
    }

    private void showNotification(final int type, String key, final MessageData message){
        boolean b = DefaultPreferenceManager.getInstance(FirebaseChattingService.this).isPushChatting(type);
        if(message != null && !message.getUser().equals(mConfigPref.getUserInfo().getEmail()) && b){
            ImageLoadManager.getInstance().loadImageUrl(message.getUserImage(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    String title = "";
                    switch (type){
                        case PUBLIC_CHATTING_NOTIFICATION_ID :
                            title = getString(R.string.talk);
                            break;
                        case DISTRICT_CHATTING_NOTIFICATION_ID :
                            int district = mConfigPref.getUserInfo().getDistrict();
                            title = String.format(getString(R.string.district_talk), String.valueOf(district));
                            break;

                        case GALLERY_NOTIFICATION_ID :
                            title = getString(R.string.camera_photo);
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
                    String summary = message.getUser();
                    NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                    bigTextStyle.setBigContentTitle(title)
                            .setSummaryText(summary)
                            .bigText(message.getMessage());
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(FirebaseChattingService.this);
                    builder.setColor(getResources().getColor(R.color.colorPrimary))
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setSmallIcon(R.drawable.push_icon)
                            .setLargeIcon(bm)
                            .setContentText(message.getMessage())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setStyle(bigTextStyle);

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        builder.setVibrate(new long[0]);
                    }

                    Intent intent = new Intent(FirebaseChattingService.this, SplashActivity.class);
                    intent.setAction(NotificationHelper.ACTION_NOTIFICATION);
                    intent.setData(DeepLinkManager.makeChattingLink(type));
                    PendingIntent pendingIntent = PendingIntent.getActivity(FirebaseChattingService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);
                    mNotificationManager.notify(type, builder.build());
                }

                @Override
                public void onErrorResponse(VolleyError error) {

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
            FirebaseChattingService.this.sendMessage(type, msg);
        }

    };
}
