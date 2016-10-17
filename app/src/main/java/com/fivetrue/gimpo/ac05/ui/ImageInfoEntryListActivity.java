package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.ImageInfoEntryDataRequest;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfoEntry;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class ImageInfoEntryListActivity extends BaseListDataActivity<ImageInfoEntry>{

    private static final String TAG = "ImageInfoEntryListActiv";

    private ImageInfoEntryDataRequest mRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        NetworkManager.getInstance().request(mRequest);
    }

    private void initData(){
        mRequest = new ImageInfoEntryDataRequest(this, baseApiResponse);
    }


    private BaseApiResponse.OnResponseListener<ArrayList<ImageInfoEntry>> baseApiResponse = new BaseApiResponse.OnResponseListener<ArrayList<ImageInfoEntry>>() {

        private int mRetry = 0;
        @Override
        public void onResponse(BaseApiResponse<ArrayList<ImageInfoEntry>> response) {
            goneProgressBar();
            if(response != null && response.getData() != null){
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
            goneProgressBar();
            if(mRetry < BaseApiResponse.RETRY_COUNT){
                showProgressBar();
                NetworkManager.getInstance().request(mRequest);
                mRetry ++;
            }
        }
    };

    @Override
    public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, ImageInfoEntry data) {
        super.onClickItem(holder, data);
        if(data != null && data.getImageInfos().size() > 2){
            Intent intent = new Intent(ImageInfoEntryListActivity.this, ImageInfoListActivity.class);
            intent.putExtra("type", data.getImageInfos().get(0).getImageType());
        }else{
            Intent intent = new Intent(this, ImageDetailActivity.class);
            intent.putExtra("url", data.getImageUrl());
            startActivity(intent);
        }
    }

    @Override
    protected boolean transitionModeWhenFinish() {
        return true;
    }
}
