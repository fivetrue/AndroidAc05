package com.fivetrue.gimpo.ac05.ui.fragment.main;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.NoticeDataRequest;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;
import com.fivetrue.gimpo.ac05.ui.adapter.NotificationDataRecyclerAdapter;
import com.fivetrue.gimpo.ac05.ui.adapter.pager.NoticeDataPagerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseDataListFragment;
import com.fivetrue.gimpo.ac05.widget.PagerSlidingTabStrip;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class NoticeDataListFragment extends BaseDataListFragment<ArrayList<NotificationData>> implements PagerSlidingTabStrip.PagerTabContent {

    private static final String TAG = "NoticeDataListFragment";

    private NotificationDataRecyclerAdapter mAdapter = null;

    private NoticeDataRequest mRequest = null;

    @Override
    protected void initData() {
        super.initData();
        mRequest = new NoticeDataRequest(getActivity(), baseApiResponse);
    }

    @Override
    protected int getPageTitleColor(){
        int color = Color.BLACK;
        if(getActivity() != null){
            color = getResources().getColor(R.color.colorAccent);
        }
        return color;
    }

    @Override
    protected int getPageTitleBgColor(){
        int color = Color.WHITE;
        if(getActivity() != null){
            color = getResources().getColor(R.color.colorPrimary);
        }
        return color;
    }

    @Override
    protected int getPageContentColor(){
        int color = Color.BLACK;
        if(getActivity() != null){
            color = getResources().getColor(R.color.colorAccent);
        }
        return color;
    }

    @Override
    protected int getPageContentBgColor(){
        int color = Color.WHITE;
        if(getActivity() != null){
            color = getResources().getColor(R.color.colorPrimary);
        }
        return color;
    }

    @Override
    protected void setData(RecyclerView view, ArrayList<NotificationData> data) {
        if(mAdapter == null){
            mAdapter = new NotificationDataRecyclerAdapter(data, onClickNoticeDataListener);
            view.setAdapter(mAdapter);
        }else{
            mAdapter.setData(data);
        }
    }

    @Override
    protected void setTitle(View view, TextView textviw, ArrayList<NotificationData> data) {
        view.setVisibility(View.GONE);
        textviw.setText(R.string.public_notice);
    }

    @Override
    public ArrayList<NotificationData> getPageData() {
        return getArguments().getParcelableArrayList(NotificationData.class.getName());
    }


    private NoticeDataPagerAdapter.OnClickNoticeDataListener onClickNoticeDataListener = new NoticeDataPagerAdapter.OnClickNoticeDataListener() {
        @Override
        public void onClick(View view, NotificationData data) {
            if(getActivity() != null){
                onClickPageData(getString(getStringResource()), data, getPageTitleColor(), getPageTitleBgColor());
            }
        }
    };

    @Override
    public int getStringResource() {
        return R.string.public_notice;
    }

    @Override
    public int getIconResource() {
        return 0;
    }

    @Override
    public String getTabName() {
        return null;
    }

    @Override
    public boolean isShowingIcon() {
        return false;
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        mRequest.setType("1");
        NetworkManager.getInstance().request(mRequest);
    }

    BaseApiResponse<List<NotificationData>> baseApiResponse = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<List<NotificationData>>() {
        @Override
        public void onResponse(BaseApiResponse<List<NotificationData>> response) {
            onRefreshFinish();
            if(response != null){
                if(mAdapter != null){
                    mAdapter.setData(response.getData());
                }
            }
        }

        @Override
        public void onError(VolleyError error) {
            onRefreshFinish();
        }
    }, new TypeToken<List<NotificationData>>(){}.getType());
}
