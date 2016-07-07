package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.ImageInfoDataRequest;
import com.fivetrue.gimpo.ac05.ui.adapter.pager.ImageInfoFragmentViewPagerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.ImageInfomationFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.WebViewFragment;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfo;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfoEntry;
import com.fivetrue.gimpo.ac05.widget.PagerSlidingTabStrip;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class ImageInfomationActivity extends DrawerActivity implements ImageInfomationFragment.OnChooseImageInfomationListener{

    private static final String TAG = "ImageInfomationActivity";

    private PagerSlidingTabStrip mPageTab = null;
    private ViewPager mViewPager = null;
    private ProgressBar mProgress = null;

    private ImageInfoFragmentViewPagerAdapter mAdapter = null;

    private ImageInfoDataRequest mRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_infomation);
        initData();
        initView();
        NetworkManager.getInstance().request(mRequest);
    }

    private void initView(){
        mPageTab = (PagerSlidingTabStrip) findViewById(R.id.tab_image_infomation_strip);
        mViewPager = (ViewPager) findViewById(R.id.vp_image_infomation);
        mProgress = (ProgressBar) findViewById(R.id.pb_infomation_image);
    }

    private void initData(){
        mRequest = new ImageInfoDataRequest(this, baseApiResponse);
    }

    public void setData(ArrayList<ImageInfo> datas){
        if(datas != null && datas.size() > 0){
            ArrayList<ImageInfoEntry> entries = new ArrayList<>();
            for(ImageInfo info : datas){
                if(entries.size() > 0) {
                    int entryIndex = entries.size() - 1;
                    if(entries.get(entryIndex).getTitle().equals(info.getImageName())){
                        entries.get(entryIndex).getImageInfos().add(info);
                    }else{
                        ImageInfoEntry entry = new ImageInfoEntry();
                        entry.setTitle(info.getImageName());
                        entry.getImageInfos().add(info);
                        entries.add(entry);
                    }
                }else{
                    ImageInfoEntry entry = new ImageInfoEntry();
                    entry.setTitle(info.getImageName());
                    entry.getImageInfos().add(info);
                    entries.add(entry);
                }

            }

            if(entries.size() > 0){
                if(mAdapter == null){
                    mAdapter = new ImageInfoFragmentViewPagerAdapter(getCurrentFragmentManager(), entries);
                    mViewPager.setAdapter(mAdapter);
                    mPageTab.setViewPager(mViewPager);
                }else{
                    mAdapter.setData(entries);
                }
            }else{

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

    @Override
    public void onSelected(ImageInfo info, Bitmap bm) {
        if(info != null && info.getImageUrl() != null){
            Intent intent = new Intent(ImageInfomationActivity.this, ImageDetailActivity.class);
            intent.putExtra("url", info.getImageUrl());
            startActivity(intent);
            overridePendingTransition(R.anim.enter_transform, 0);
        }
    }
}
