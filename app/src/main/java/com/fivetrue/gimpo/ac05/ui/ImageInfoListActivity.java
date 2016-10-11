package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.NetworkManager;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.net.request.ImageInfoDataRequest;
import com.fivetrue.gimpo.ac05.net.request.ImageInfoEntryDataRequest;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfo;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfoEntry;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class ImageInfoListActivity extends BaseListDataActivity<ImageInfo>{

    private static final String TAG = "ImageInfoEntryListActiv";

    private ImageInfoDataRequest mRequest = null;
    private String mType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        NetworkManager.getInstance().request(mRequest);
    }

    private void initData(){
        mType = getIntent().getStringExtra("type");
        mRequest = new ImageInfoDataRequest(this, mType, baseApiResponse);
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        mRequest.setCached(false);
        NetworkManager.getInstance().request(mRequest);
    }

    private BaseApiResponse.OnResponseListener<ArrayList<ImageInfo>> baseApiResponse = new BaseApiResponse.OnResponseListener<ArrayList<ImageInfo>>() {

        private int mRetry = 0;
        @Override
        public void onResponse(BaseApiResponse<ArrayList<ImageInfo>> response) {
            onRefreshFinish();
            goneProgressBar();
            if(response != null && response.getData() != null){
                mRequest.setCached(true);
                setData(response.getData());
            }else{
                if(mRetry < BaseApiResponse.RETRY_COUNT){
                    showProgressBar();
                    NetworkManager.getInstance().request(mRequest);
                    mRetry ++;
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            onRefreshFinish();
            goneProgressBar();
            if(mRetry < BaseApiResponse.RETRY_COUNT){
                showProgressBar();
                NetworkManager.getInstance().request(mRequest);
                mRetry ++;
            }
        }
    };

    @Override
    public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, ImageInfo data) {
        super.onClickItem(holder, data);
        if(data != null) {
            Intent intent = new Intent(this, ImageDetailActivity.class);
            intent.putExtra("url", data.getImageUrl());
            startActivityWithClipRevealAnimation(intent, holder.layout);
        }
    }

    @Override
    protected boolean transitionModeWhenFinish() {
        return true;
    }

    @Override
    protected int getAdapterItemLayout() {
        return R.layout.item_image_list_item_vertical;
    }
}
