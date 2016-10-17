package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.fivetrue.gimpo.ac05.vo.data.TownData;
import com.fivetrue.gimpo.ac05.vo.data.TownDataEntry;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class TownDataListActivity extends BaseListDataActivity<TownData> {

    private static final String TAG = "NotificationDataListFra";

    private TownDataEntry mTownDataEntry;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        if(mTownDataEntry != null){
            setData(mTownDataEntry.getList());
            goneProgressBar();
        }
    }

    private void initData() {
        mTownDataEntry = getIntent().getParcelableExtra(TownDataEntry.class.getName());
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        onRefreshFinish();
    }



    @Override
    public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, TownData data) {
        super.onClickItem(holder, data);
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", data.getUrl());
        intent.putExtra("title", data.getContent());
        intent.putExtra("image", data.getImageUrl());
        startActivityWithClipRevealAnimation(intent, holder.layout);
    }

    @Override
    protected boolean transitionModeWhenFinish() {
        return true;
    }
}
