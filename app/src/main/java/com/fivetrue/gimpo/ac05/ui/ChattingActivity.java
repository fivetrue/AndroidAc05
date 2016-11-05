package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.fivetrueandroid.google.FirebaseStorageManager;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.fivetrueandroid.ui.diaglog.LoadingDialog;
import com.fivetrue.fivetrueandroid.ui.fragment.BaseDialogFragment;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.firebase.database.ActiveChatUserDatabase;
import com.fivetrue.gimpo.ac05.firebase.database.ChatDatabase;
import com.fivetrue.gimpo.ac05.database.ChatLocalDB;
import com.fivetrue.gimpo.ac05.firebase.model.ChatMessage;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.adapter.ChatListAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.UserInfoDialogFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.UserListDialogFragment;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kwonojin on 2016. 10. 11..
 */

public class ChattingActivity extends FirebaseBaseAcitivty implements ChildEventListener{

    private static final String TAG = "ChattingActivity";

    public static final int TYPE_CHATTING_PUBLIC = Constants.PUBLIC_CHATTING_ID;
    public static final int TYPE_CHATTING_DISTRICT = Constants.DISTRICT_CHATTING_ID;

    private static final int REQUEST_CODE_CHOOSE_IMAGE = 0x30;
    private static final String CHATTING_STORAGE_PATH = "/images/chatting/";

    private RecyclerView mDialogueList;
    private ImageButton mAddImage;
    private ImageButton mSendMessage;
    private EditText mInputMessage;
    private TextView mActiveUserCount;

    private ConfigPreferenceManager mConfigPref;
    private int mType = TYPE_CHATTING_PUBLIC;
    private User mUserInfo;

    private ChatLocalDB mChatLocalDB;

    private ChatListAdapter mAdapter;

    private Map<String, User> mActiveUsers = new HashMap<>();

    private ActiveChatUserDatabase mActiveUserDBReference;
    private ChatDatabase mChatDatabase;
    private FirebaseStorageManager mFirebaseStorageManager;

    private InputMethodManager mImm;

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
        if(mActiveUserDBReference != null){
            mActiveUserDBReference.getReference().child(mConfigPref.getUserInfo().uid).setValue(mConfigPref.getUserInfo().getValues());
            mActiveUserDBReference.getReference().addChildEventListener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mActiveUserDBReference.getReference().child(mConfigPref.getUserInfo().uid).setValue(null);
        mActiveUserDBReference.getReference().removeEventListener(this);
    }


    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mType = getIntent().getIntExtra("type", TYPE_CHATTING_PUBLIC);
        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        mFirebaseStorageManager = new FirebaseStorageManager();

        mUserInfo = mConfigPref.getUserInfo();
        mChatLocalDB = new ChatLocalDB(this);
        mAdapter = new ChatListAdapter(mChatLocalDB.getChatMessages(mType), mConfigPref.getUserInfo().uid);
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

        String child = null;
        if(mType == TYPE_CHATTING_PUBLIC){
            child = String.valueOf(mType);
        }else{
            child = mType + "/" + mConfigPref.getUserInfo().district;
        }
        mChatDatabase = new ChatDatabase(child);
        mActiveUserDBReference = new ActiveChatUserDatabase(child);
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = mType == TYPE_CHATTING_PUBLIC ? getString(R.string.talk) : getString(R.string.district_talk, mConfigPref.getUserInfo().district + "");
        getSupportActionBar().setTitle(title);

        mDialogueList = (RecyclerView) findViewById(R.id.rv_chatting_dialogue_list);
        mAddImage = (ImageButton) findViewById(R.id.iv_chatting_add);
        mSendMessage = (ImageButton) findViewById(R.id.btn_chatting_send);
        mInputMessage = (EditText) findViewById(R.id.et_chatting_input);
        mActiveUserCount = (TextView) findViewById(R.id.tv_chatting_active_count);

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

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseStorageManager.chooseDeviceImage(ChattingActivity.this, REQUEST_CODE_CHOOSE_IMAGE);
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
        mDialogueList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = mDialogueList.getRootView().getHeight() - mDialogueList.getHeight();

                if (heightDiff > 100) {
                    Log.e("MyActivity", "keyboard opened");
                }

                if (heightDiff < 100) {
                    Log.e("MyActivity", "keyboard closed");
                }
            }
        });

    }

    private void sendMessage(String message){
        if(message != null){
            ChatMessage msg = new ChatMessage(null, message.trim()
                    , null, mUserInfo);
            mChatDatabase.pushData(msg);
        }
    }

    @Override
    protected void onReceivedPublicChat(String key, ChatMessage msg) {
        super.onReceivedPublicChat(key, msg);
        if(mType == Constants.PUBLIC_CHATTING_ID){
            mAdapter.getData().add(msg);
            mAdapter.notifyDataSetChanged();
            mDialogueList.smoothScrollToPosition(mAdapter.getCount() - 1);
        }
    }

    @Override
    protected void onReceivedDistrictChat(String key, ChatMessage msg) {
        super.onReceivedDistrictChat(key, msg);
        if(mType == Constants.DISTRICT_CHATTING_ID){
            mAdapter.getData().add(msg);
            mAdapter.notifyDataSetChanged();
            mDialogueList.smoothScrollToPosition(mAdapter.getCount() - 1);
        }
    }

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
        if(mActiveUserCount != null){
            mActiveUserCount.setText(mActiveUsers.size() + "");
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved() called with: dataSnapshot = [" + dataSnapshot + "]");
        mActiveUsers.remove(dataSnapshot.getKey());
        if(mActiveUserCount != null){
            mActiveUserCount.setText(mActiveUsers.size() + "");
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CHOOSE_IMAGE){
            if(resultCode == RESULT_OK){
                FirebaseStorageManager.cropChooseDeviceImage(this, data.getData());
            }
        }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri croppedUri =  result.getUri();
                if(croppedUri != null){
                    try {
                        final Bitmap bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedUri));
                        if(bm != null && !bm.isRecycled()){
                            final LoadingDialog dialog = new LoadingDialog(this);
                            dialog.show();
                            String fileName = mUserInfo.uid + "_" + System.currentTimeMillis() + ".png";
                            mFirebaseStorageManager.uploadBitmapImage(CHATTING_STORAGE_PATH, fileName, bm, new FirebaseStorageManager.OnUploadResultListener(){

                                @Override
                                public void onUploadSuccess(Uri url, String path) {
                                    Log.d(TAG, "onUploadSuccess: url = " + url);
                                    Log.d(TAG, "onUploadSuccess: path = " + path);
                                    bm.recycle();
                                    ChatMessage message = new ChatMessage(null, getString(R.string.image_infomation), url.toString(), mUserInfo);
                                    mChatDatabase.pushData(message);
                                    dialog.dismiss();
                                }

                                @Override
                                public void onUploadFailed(Exception e) {
                                    Toast.makeText(ChattingActivity.this, R.string.error_image_upload_failed, Toast.LENGTH_SHORT).show();
                                    bm.recycle();
                                    dialog.dismiss();
                                }
                            });

                        }
                    } catch (FileNotFoundException e) {
                        Log.w(TAG, "onActivityResult: image decoded error", e);
                    }
                }
            }
        }
    }
}
