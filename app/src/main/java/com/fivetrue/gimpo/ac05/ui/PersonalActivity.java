package com.fivetrue.gimpo.ac05.ui;

import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.chatting.ChatMessage;
import com.fivetrue.gimpo.ac05.chatting.ChatMessageDatabase;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.adapter.PersonalListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by kwonojin on 2016. 10. 19..
 */

public class PersonalActivity extends BaseActivity {

    private static final String TAG = "PersonalActivity";

    private RecyclerView mRecyclerView;
    private PersonalListAdapter mAdapter;

    private ChatMessageDatabase mChatMessageDatabase;

    private ConfigPreferenceManager mConfigPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initData();
        initView();
    }

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mChatMessageDatabase = new ChatMessageDatabase(this);
        List<ChatMessage> messages = mChatMessageDatabase.getChatMessages(Constants.PERSON_MESSAGE_NOTIFICATION_ID, true);
        mAdapter = new PersonalListAdapter(messages, mConfigPref.getUserInfo(),
                getCurrentFragmentManager());
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.person_alarm);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_personal_message_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.d(TAG, "onMove() called with: recyclerView = [" + recyclerView + "], viewHolder = [" + viewHolder + "], target = [" + target + "]");
                return false;
            }


            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                Log.d(TAG, "onSwiped() called with: viewHolder = [" + viewHolder + "], swipeDir = [" + swipeDir + "]");
                final int position = viewHolder.getAdapterPosition();
                final ChatMessage message = mAdapter.getItem(position);
                mAdapter.getData().remove(message);
                mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    Snackbar.make(mRecyclerView, R.string.delete_chat_message, Snackbar.LENGTH_LONG)
                            .setAction(android.R.string.cancel , new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAdapter.getData().add(position, message);
                                    mAdapter.notifyItemInserted(position);
                                }
                            }).setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            if(event != Snackbar.Callback.DISMISS_EVENT_ACTION){
                                FirebaseDatabase.getInstance()
                                        .getReference(Constants.FIREBASE_DB_ROOT_PERSON)
                                        .child(mConfigPref.getUserInfo().getUid()).child(Constants.FIREBASE_DB_MESSAGE).child(message.key).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess() called with: aVoid = [" + aVoid + "]");
                                        int count = mChatMessageDatabase.removeChatMessage(Constants.PERSON_MESSAGE_NOTIFICATION_ID, message);
                                        Log.d(TAG, "onSuccess() called with: remove = [" + count + "]");

                                    }
                                });
                            }
                        }
                    }).show();
                }else{
                    FirebaseDatabase.getInstance()
                            .getReference(Constants.FIREBASE_DB_ROOT_PERSON)
                            .child(message.getUserId()).child(Constants.FIREBASE_DB_MESSAGE).child(message.key).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess() called with: aVoid = [" + aVoid + "]");
                            int count = mChatMessageDatabase.removeChatMessage(Constants.PERSON_MESSAGE_NOTIFICATION_ID, message);
                            Log.d(TAG, "onSuccess() called with: remove = [" + count + "]");
                        }
                    });
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_noti);
        if(item != null){
            if(DefaultPreferenceManager.getInstance(this).isPushChatting(Constants.PERSON_MESSAGE_NOTIFICATION_ID)){
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
        return R.menu.menu_personal;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;


            case R.id.action_noti :
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
                DefaultPreferenceManager.getInstance(this)
                        .setPushChatting(Constants.PERSON_MESSAGE_NOTIFICATION_ID, !DefaultPreferenceManager.getInstance(this).isPushChatting(Constants.PERSON_MESSAGE_NOTIFICATION_ID));
                invalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
