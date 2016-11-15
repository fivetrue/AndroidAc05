package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.fivetrueandroid.ui.diaglog.LoadingDialog;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.firebase.database.TownNewsDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.TownNews;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.service.GcmMessage;
import com.fivetrue.gimpo.ac05.ui.adapter.AdminListAdapter;
import com.fivetrue.gimpo.ac05.utils.DeeplinkUtil;
import com.fivetrue.gimpo.ac05.utils.NotificationSender;
import com.fivetrue.gimpo.ac05.utils.TownDataParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 16. 6. 10..
 */
public class AdminActivity extends BaseActivity {

    private static final String TAG = "AdminActivity";

    public interface IAdminItem{
        String getTitle();
        void doFunction();
    }

    private RecyclerView mRecyclerView;
    private AdminListAdapter mAdapter;
    private LoadingDialog mLoadingDialog;
    private ConfigPreferenceManager mConfig;

    private TownNewsDatabase mTownDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        initData();
        initView();
    }

    protected void initData(){
        mConfig = new ConfigPreferenceManager(this);
        mTownDatabase = new TownNewsDatabase();
        mAdapter = new AdminListAdapter(new ArrayList<IAdminItem>());
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<IAdminItem, AdminListAdapter.AdminViewHolder>() {
            @Override
            public void onClickItem(AdminListAdapter.AdminViewHolder holder, final IAdminItem data) {
                if(data != null){
                    data.doFunction();
                }
            }
        });
        mAdapter.getData().add(new IAdminItem() {
            @Override
            public String getTitle() {
                return "구래동 소식 업데이트";
            }

            @Override
            public void doFunction() {
                mLoadingDialog.show();

                mTownDatabase.getReference().orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot != null){
                            final ArrayList<TownNews> townNewsArrayList = new ArrayList<TownNews>();
                            for(DataSnapshot s : dataSnapshot.getChildren()){
                                townNewsArrayList.add(s.getValue(TownNews.class));
                            }

                            new TownDataParser(AdminActivity.this
                                    , mConfig.getAppConfig(), new TownDataParser.OnLoadTownDataListener() {
                                @Override
                                public void onLoad(List<TownNews> list) {

                                    boolean isNew = false;
                                    GcmMessage message = new GcmMessage();
                                    message.id = Constants.TOWN_NEWS_CONTENT_ID;
                                    message.title = getTitle();
                                    message.message = "";
                                    message.deeplink = DeeplinkUtil.makeLink(DeeplinkUtil.Host.TownNews);
                                    if(list != null && list.size() > 0){
                                        for(TownNews news : list){
                                            if(!townNewsArrayList.contains(news)){
                                                isNew = true;
                                                new TownNewsDatabase().pushData(news);
                                                message.message += news.title + "\n";
                                            }
                                        }
                                    }
                                    Toast.makeText(AdminActivity.this, getTitle() + " : 완료", Toast.LENGTH_SHORT).show();
                                    if(isNew){
                                        NotificationSender.sendNotificationToUsers(message, mConfig.getAppConfig());
                                    }
                                    mLoadingDialog.dismiss();
                                }
                            }).execute();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.admin);
        mLoadingDialog = new LoadingDialog(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_admin_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean transitionModeWhenFinish() {
        return true;
    }
}
