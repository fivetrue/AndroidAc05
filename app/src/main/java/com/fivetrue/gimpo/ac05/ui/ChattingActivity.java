package com.fivetrue.gimpo.ac05.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.fivetrueandroid.ui.fragment.BaseDialogFragment;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.chatting.ChatMessage;
import com.fivetrue.gimpo.ac05.firebase.database.ActiveChatUserDatabase;
import com.fivetrue.gimpo.ac05.service.FirebaseService;
import com.fivetrue.gimpo.ac05.database.ChatMessageDatabase;
import com.fivetrue.gimpo.ac05.chatting.IChattingCallback;
import com.fivetrue.gimpo.ac05.chatting.IChattingService;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.adapter.ChatListAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.UserInfoDialogFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.UserListDialogFragment;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kwonojin on 2016. 10. 11..
 */

public class ChattingActivity extends BaseActivity implements ChildEventListener{

    private static final String TAG = "ChattingActivity";

    public static final int TYPE_CHATTING_PUBLIC = Constants.PUBLIC_CHATTING_NOTIFICATION_ID;
    public static final int TYPE_CHATTING_DISTRICT = Constants.DISTRICT_CHATTING_NOTIFICATION_ID;

    private RecyclerView mDialogueList;
    private ImageButton mSendMessage;
    private EditText mInputMessage;

    private ConfigPreferenceManager mConfigPref;
    private int mType = TYPE_CHATTING_PUBLIC;
    private User mUserInfo;

    private IChattingService iChattingService;

    private ChatMessageDatabase mChatMessageDb;

    private ChatListAdapter mAdapter;

    private Map<String, User> mActiveUsers = new HashMap<>();

    private ActiveChatUserDatabase mActiveUserDBReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        initData();
        initView();
    }


    @Override
    protected void onStart() {
        super.onStart();
        bindChatting();
        if(mActiveUserDBReference != null){
            mActiveUserDBReference.getReference().child(mConfigPref.getUserInfo().uid).setValue(mConfigPref.getUserInfo().getValues());
            mActiveUserDBReference.getReference().addChildEventListener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(iChattingService != null){
            unbindService(serviceConnection);
        }
        mActiveUserDBReference.getReference().child(mConfigPref.getUserInfo().uid).setValue(null);
    }

    private void bindChatting(){
        Intent intent = new Intent(this, FirebaseService.class);
        intent.setAction(FirebaseService.ACTION_BIND_SERVICE);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mType = getIntent().getIntExtra("type", TYPE_CHATTING_PUBLIC);
        mUserInfo = mConfigPref.getUserInfo();
        mChatMessageDb = new ChatMessageDatabase(this);
        mAdapter = new ChatListAdapter(mChatMessageDb.getChatMessages(mType), mConfigPref.getUserInfo().uid);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<ChatMessage, ChatListAdapter.ChatItemViewHolder>() {
            @Override
            public void onClickItem(ChatListAdapter.ChatItemViewHolder holder, ChatMessage data) {
                Bundle b = new Bundle();
                b.putParcelable(User.class.getName(), data.getUser());
                UserInfoDialogFragment dialog = new UserInfoDialogFragment();
                dialog.show(getCurrentFragmentManager(), null
                        , getString(android.R.string.ok), null, b, new BaseDialogFragment.OnClickDialogFragmentListener() {
                            @Override
                            public void onClickOKButton(BaseDialogFragment f, Object data) {
                                f.dismiss();
                            }

                            @Override
                            public void onClickCancelButton(BaseDialogFragment f, Object data) {
                                f.dismiss();
                            }
                        });
            }
        });

        if(mType == TYPE_CHATTING_PUBLIC){
            mActiveUserDBReference = new ActiveChatUserDatabase(Constants.FIREBASE_DB_CHATTING_PUBLIC);
        }else{
            mActiveUserDBReference = new ActiveChatUserDatabase(Constants.FIREBASE_DB_CHATTING_DISTRICT + "/" + mConfigPref.getUserInfo().district);
        }
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = mType == TYPE_CHATTING_PUBLIC ? getString(R.string.talk) : getString(R.string.district_talk, mConfigPref.getUserInfo().district + "");
        getSupportActionBar().setTitle(title);

        mDialogueList = (RecyclerView) findViewById(R.id.rv_chatting_dialogue_list);
        mSendMessage = (ImageButton) findViewById(R.id.btn_chatting_send);
        mInputMessage = (EditText) findViewById(R.id.et_chatting_input);

        mDialogueList.setAdapter(mAdapter);
        mDialogueList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mInputMessage.getText().toString().trim();
                if(!TextUtils.isEmpty(msg)){
                    sendMessage(mInputMessage.getText().toString());
                    mInputMessage.setText("");
                }
            }
        });

        mInputMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    sendMessage(mInputMessage.getText().toString());
                    mInputMessage.setText("");
                }
                return false;
            }
        });

        mDialogueList.scrollToPosition(mAdapter.getCount() - 1);
    }

    private void sendMessage(String message){
        if(message != null){
            ChatMessage msg = new ChatMessage(null, message.trim()
                    , null, mUserInfo);
            try {
                iChattingService.sendMessage(mType, msg);
            } catch (RemoteException e) {
                Log.w(TAG, "sendMessage: ", e);
            }
        }
    }

    private void onReceiveMessage(int type, String key, ChatMessage message){
        if(message != null ){
            Log.d(TAG, "onReceiveMessage() called with: message = [" + message + "]");
            mAdapter.getData().add(message);
            mAdapter.notifyDataSetChanged();
            mDialogueList.smoothScrollToPosition(mAdapter.getCount() - 1);
        }
    }

    private IChattingCallback.Stub callback = new IChattingCallback.Stub(){

        @Override
        public void onReceivedMessage(int type, String key, ChatMessage msg) throws RemoteException {
            ChattingActivity.this.onReceiveMessage(type, key, msg);
        }
    };

    private void onBindService(){
        try {
            this.iChattingService.registerCallback(mType, callback);
        } catch (RemoteException e) {
            Log.w(TAG, "onBindService: ", e);
        }

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iChattingService = IChattingService.Stub.asInterface(service);
            onBindService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                iChattingService.unregisterCallback(callback);
            } catch (RemoteException e) {
                Log.w(TAG, "onServiceDisconnected: ", e);
            }finally {
                iChattingService = null;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;

            case R.id.action_chat_user :
                ArrayList<User> userInfoArrayList = new ArrayList<User>();
                for(User info : mActiveUsers.values()){
                    userInfoArrayList.add(info);
                }
                Bundle b = new Bundle();
                b.putParcelableArrayList(User.class.getName(), userInfoArrayList);
                UserListDialogFragment dialog = new UserListDialogFragment();
                dialog.show(getCurrentFragmentManager(), getString(R.string.chat_user_list), getString(android.R.string.ok)
                        , null, b
                        , new BaseDialogFragment.OnClickDialogFragmentListener() {
                    @Override
                    public void onClickOKButton(BaseDialogFragment f, Object data) {
                        f.dismiss();
                    }

                    @Override
                    public void onClickCancelButton(BaseDialogFragment f, Object data) {
                        f.dismiss();
                    }
                });
                return true;

            case R.id.action_chat :
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
                DefaultPreferenceManager.getInstance(this)
                        .setPushChatting(mType, !DefaultPreferenceManager.getInstance(this).isPushChatting(mType));
                invalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_chat);
        if(item != null){
            if(DefaultPreferenceManager.getInstance(this).isPushChatting(mType)){
                item.setIcon(R.drawable.ic_chat_20dp);
                item.setTitle(R.string.chat_alarm_off);
            }else{
                item.setIcon(R.drawable.ic_no_chat_20dp);
                item.setTitle(R.string.chat_alarm_on);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected int getOptionsMenuResource() {
        return R.menu.menu_chatting;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "onChildAdded() called with: dataSnapshot = [" + dataSnapshot + "], s = [" + s + "]");
        String key = dataSnapshot.getKey();
        User userInfo = dataSnapshot.getValue(User.class);
        mActiveUsers.put(key, userInfo);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved() called with: dataSnapshot = [" + dataSnapshot + "]");
        mActiveUsers.remove(dataSnapshot.getKey());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
