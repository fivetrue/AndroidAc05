package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.BasicRequest;
import com.fivetrue.fivetrueandroid.net.ErrorCode;
import com.fivetrue.fivetrueandroid.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.NotificationDataRequest;
import com.fivetrue.gimpo.ac05.net.request.PublicNotificationDataRequest;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class NotificationDataListActivity extends BaseListDataActivity<NotificationData> {

    private static final String TAG = "NotificationDataListFra";

    private static final int PAGE_COUNT = 30;

    private BasicRequest mRequest = null;

    private String mType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        NetworkManager.getInstance().request(mRequest);
    }

    private void initData() {
        mType = getIntent().getStringExtra("type");
        if(mType.equals("1")){
            mRequest = new PublicNotificationDataRequest(this, baseApiResponse);
        }else{
            mRequest = new NotificationDataRequest(this, baseApiResponse);
        }
        mRequest.setPage(0);
        mRequest.setCount(PAGE_COUNT);
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        mRequest.setPage(0);
        NetworkManager.getInstance().request(mRequest);
    }


    BaseApiResponse.OnResponseListener<ArrayList<NotificationData>> baseApiResponse = new BaseApiResponse.OnResponseListener<ArrayList<NotificationData>>() {
        @Override
        public void onResponse(BaseApiResponse<ArrayList<NotificationData>> response) {
            goneProgressBar();
            onRefreshFinish();
            if (response != null) {
                if (response.getErrorCode() == ErrorCode.OK) {
                    if (mRequest.getPage() == 0) {
                        setData(response.getData());
                    } else {
                        addData(response.getData());
                    }
                } else {
                    Toast.makeText(NotificationDataListActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            Log.w(TAG, "Network error : ", error);
            goneProgressBar();
            onRefreshFinish();
        }
    };

    @Override
    public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, NotificationData data) {
        super.onClickItem(holder, data);
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", data.getUrl());
        intent.putExtra("title", data.getTitle());
        intent.putExtra("subtitle", data.getContent());
        intent.putExtra("image", data.getImageUrl());

        startActivity(intent,
                ActivityOptionsCompat.makeClipRevealAnimation(holder.image
                        , (int) holder.layout.getX(), (int) holder.layout.getY()
                        , holder.layout.getWidth(), holder.layout.getHeight()).toBundle());
    }

    @Override
    protected boolean transitionModeWhenFinish() {
        return true;
    }
}
