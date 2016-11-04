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
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.database.TownNewsLocalDB;
import com.fivetrue.gimpo.ac05.firebase.database.TownNewsDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.TownNews;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.adapter.AdminListAdapter;
import com.fivetrue.gimpo.ac05.utils.TownDataParser;

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

    private TownNewsLocalDB mTownNewsLocalDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        initData();
        initView();
    }

    protected void initData(){
        mConfig = new ConfigPreferenceManager(this);
        mTownNewsLocalDB = new TownNewsLocalDB(this);
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
                return "구래동 뉴스 업데이트";
            }

            @Override
            public void doFunction() {
                mLoadingDialog.show();
                new TownDataParser(AdminActivity.this
                        , mConfig.getAppConfig(), new TownDataParser.OnLoadTownDataListener() {
                    @Override
                    public void onLoad(List<TownNews> list) {

                        if(list != null && list.size() > 0){
                            for(TownNews news : list){
                                if(!mTownNewsLocalDB.existsTownNews(news.url)){
                                    new TownNewsDatabase().pushData(news);
                                }
                            }
                        }
                        Toast.makeText(AdminActivity.this, getTitle() + " : 완료", Toast.LENGTH_SHORT).show();
                        mLoadingDialog.dismiss();
                    }
                }).execute();

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
