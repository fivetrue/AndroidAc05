package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.analytics.Event;
import com.fivetrue.gimpo.ac05.analytics.GoogleAnalytics;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.NoticeDataRequest;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;
import com.fivetrue.gimpo.ac05.ui.adapter.NotificationDataRecyclerAdapter;
import com.fivetrue.gimpo.ac05.ui.adapter.pager.NoticeDataPagerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.WebViewFragment;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 17..
 */
public class NoticeListActivity extends DrawerActivity{

    private NoticeDataRequest mRequest = null;
    private RecyclerView mRecylerView = null;
    private TextView mEmptyText = null;

    private ProgressBar mProgress = null;

    private NotificationDataRecyclerAdapter mAdapter = null;
    private ConfigPreferenceManager mPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_data_list);
        initData();
        initView();
        NetworkManager.getInstance().request(mRequest);
        GoogleAnalytics.getInstance().sendLogEventProperties(Event.EnterNoticeActivity);
    }

    private void initData(){
        mRequest = new NoticeDataRequest(this, baseApiResponse);
        mPref = new ConfigPreferenceManager(this);
    }

    private void initView(){
        mRecylerView = (RecyclerView) findViewById(R.id.rv_notice_data_list);
        mEmptyText = (TextView) findViewById(R.id.tv_notice_data_empty);
        mProgress = (ProgressBar) findViewById(R.id.pb_notice_data);

        mRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mEmptyText.setTextColor(getResources().getColor(R.color.colorAccent));

        mProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark)
                , android.graphics.PorterDuff.Mode.MULTIPLY);

        getFtActionBar().setTitle(R.string.notice);
    }

    private void setListData(ArrayList<NotificationData> datas){
        if(datas != null && datas.size() > 0){
            mEmptyText.setVisibility(View.GONE);
            if(mAdapter == null){
                mAdapter = new NotificationDataRecyclerAdapter(datas, onClickNoticeDataListener);
                mRecylerView.setAdapter(mAdapter);
            }else{
                mAdapter.setData(datas);
            }
        }else{
            mEmptyText.setVisibility(View.VISIBLE);
        }
    }

    private NoticeDataPagerAdapter.OnClickNoticeDataListener onClickNoticeDataListener = new NoticeDataPagerAdapter.OnClickNoticeDataListener() {
        @Override
        public void onClick(View view, NotificationData data) {
            Bundle b = new Bundle();
            String url = String.format(Constants.API_CHECK_REDIRECT, data.getUri()
                    , data.getMulticast_id()
                    , mPref.getUserInfo().getEmail());
            b.putString("url", url);
            addFragment(WebViewFragment.class, b, getBaseLayoutContainer().getId(), R.anim.enter_transform, R.anim.exit_transform, true);
            GoogleAnalytics.getInstance().sendLogEventProperties(Event.ClickNoticeData);
        }

        @Override
        public void onShowNewItem(NotificationData data) {

        }
    };

    private BaseApiResponse <ArrayList<NotificationData>> baseApiResponse = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<ArrayList<NotificationData>>() {
        @Override
        public void onResponse(BaseApiResponse<ArrayList<NotificationData>> response) {
            mProgress.setVisibility(View.GONE);
            if(response != null){
                setListData(response.getData());
            }else{
                mEmptyText.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onError(VolleyError error) {
            mProgress.setVisibility(View.GONE);
        }
    }, new TypeToken<ArrayList<NotificationData>>(){}.getType());

}
