package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.adapter.InfomationImageRecyclerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.ImageDetailFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.WebViewFragment;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class InfomationImageActivity extends DrawerActivity{

    private static final String TAG = "InfomationImageActivity";

    private RecyclerView mRecyclerView = null;

    private InfomationImageRecyclerAdapter mAdapter = null;

    private AppConfig mAppConfig = null;
    private ArrayList<String> mInfomationUrls = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);
        initData();
        initView();
        setData(mInfomationUrls);
    }

    private void initView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_infomations);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initData(){
        mAppConfig = ((ApplicationEX)getApplicationContext()).getAppConfig();
        if(mAppConfig != null){
            mInfomationUrls = new ArrayList<>();
            for(String path : mAppConfig.getInfomationImageUrlList()){
                mInfomationUrls.add(Constants.API_SERVER_HOST + path);
            }
        }
    }

    public void setData(ArrayList<String> datas){
        if(datas != null){
            if(mAdapter == null){
                mAdapter = new InfomationImageRecyclerAdapter(datas, onInfomationImageItemClickListener);
                mRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.setData(datas);
            }
        }else{
            //TODO : Update data is empty
        }
    }

    @Override
    public void onBackPressed() {
        Fragment f = getCurrentFragmentManager().findFragmentById(getFragmentAnchorLayoutID());
        if(f != null && f instanceof WebViewFragment){
            if(((WebViewFragment) f).canGoback()){
                ((WebViewFragment) f).goBack();
                return;
            }
        }
        if(getCurrentFragmentManager().getBackStackEntryCount() > 0 && getCurrentFragmentManager().getBackStackEntryCount() == 1){
            getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
        }
        super.onBackPressed();
    }

    private InfomationImageRecyclerAdapter.OnInfomationImageItemClickListener onInfomationImageItemClickListener = new InfomationImageRecyclerAdapter.OnInfomationImageItemClickListener() {
        @Override
        public void onClick(View view, String imageUrl, Bitmap bitmap) {
            if(bitmap != null && !bitmap.isRecycled()){
                Intent intent = new Intent(InfomationImageActivity.this, ImageDetailActivity.class);
                intent.putExtra("url", imageUrl);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_transform, 0);
            }
        }
    };

    @Override
    public BaseFragment addFragment(Class<? extends BaseFragment> cls, Bundle arguments, int anchorLayout, int enterAnim, int exitAnim, boolean addBackstack) {
        BaseFragment f = super.addFragment(cls, arguments, anchorLayout, enterAnim, exitAnim, addBackstack);
        if(addBackstack){
            getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
        }
        return f;

    }
}
