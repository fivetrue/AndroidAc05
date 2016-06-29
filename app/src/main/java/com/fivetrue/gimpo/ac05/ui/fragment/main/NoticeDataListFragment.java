package com.fivetrue.gimpo.ac05.ui.fragment.main;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;
import com.fivetrue.gimpo.ac05.ui.adapter.NotificationDataRecyclerAdapter;
import com.fivetrue.gimpo.ac05.ui.adapter.pager.NoticeDataPagerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseDataListFragment;
import com.fivetrue.gimpo.ac05.widget.PagerSlidingTabStrip;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class NoticeDataListFragment extends BaseDataListFragment<ArrayList<NotificationData>> implements PagerSlidingTabStrip.PagerTabContent {

    private static final String TAG = "NoticeDataListFragment";

    private NotificationDataRecyclerAdapter mAdapter = null;

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
}
