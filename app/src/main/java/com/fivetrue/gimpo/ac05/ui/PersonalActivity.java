package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.chatting.ChatMessage;
import com.fivetrue.gimpo.ac05.database.ChatMessageDatabase;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.adapter.PersonalListAdapter;

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
