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
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.ImageInfoDataRequest;
import com.fivetrue.gimpo.ac05.ui.adapter.InfomationImageRecyclerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.WebViewFragment;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class InfomationImageActivity extends DrawerActivity{

    private static final String TAG = "InfomationImageActivity";

    private RecyclerView mRecyclerView = null;
    private ProgressBar mProgress = null;

    private InfomationImageRecyclerAdapter mAdapter = null;

    private ArrayList<String> mInfomationUrls = null;



    private ImageInfoDataRequest mRequest = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);
        initData();
        initView();
        NetworkManager.getInstance().request(mRequest);
    }

    private void initView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_infomation_image);
        mProgress = (ProgressBar) findViewById(R.id.pb_infomation_image);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initData(){
        mRequest = new ImageInfoDataRequest(this, baseApiResponse);
    }

    public void setData(ArrayList<ImageInfo> datas){
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
        public void onClick(View view, ImageInfo info, Bitmap bitmap) {
            if(info != null && info.getImageUrl() != null){
                Intent intent = new Intent(InfomationImageActivity.this, ImageDetailActivity.class);
                intent.putExtra("url", info.getImageUrl());
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

    private BaseApiResponse<ArrayList<ImageInfo>> baseApiResponse = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<ArrayList<ImageInfo>>() {

        private int mRetry = 0;
        @Override
        public void onResponse(BaseApiResponse<ArrayList<ImageInfo>> response) {
            mProgress.setVisibility(View.GONE);
            if(response != null && response.getData() != null){
                setData(response.getData());
            }else{
                if(mRetry < BaseApiResponse.RETRY_COUNT){
                    mProgress.setVisibility(View.VISIBLE);
                    NetworkManager.getInstance().request(mRequest);
                    mRetry ++;
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            mProgress.setVisibility(View.GONE);
            if(mRetry < BaseApiResponse.RETRY_COUNT){
                mProgress.setVisibility(View.VISIBLE);
                NetworkManager.getInstance().request(mRequest);
                mRetry ++;
            }
        }
    }, new TypeToken<ArrayList<ImageInfo>>(){}.getType());
}
