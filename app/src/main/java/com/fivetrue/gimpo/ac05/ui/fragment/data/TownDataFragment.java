package com.fivetrue.gimpo.ac05.ui.fragment.data;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.rss.FeedMessage;
import com.fivetrue.gimpo.ac05.ui.adapter.pager.TownDataPagerAdapter;
import com.fivetrue.gimpo.ac05.vo.data.TownData;
import com.fivetrue.gimpo.ac05.vo.data.TownDataEntry;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by kwonojin on 16. 6. 15..
 */
public class TownDataFragment extends ColorChooserFragment {

    private ViewGroup mLayoutLabel = null;
    private TextView mDataTitle = null;
    private ImageView mDataView = null;
    private ImageView mDataDetail = null;
    private ViewPager mViewPager = null;

    private TownDataPagerAdapter mAdapter = null;

    private TownDataEntry mEntry = null;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView(inflater);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData(mEntry);
    }

    private void initData(){
        mEntry = getArguments().getParcelable(TownDataEntry.class.getName());
    }

    @Override
    public void onStart() {
        super.onStart();
        registerTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterTimer();
    }


    private void registerTimer(){
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mViewPager != null && mEntry != null){
                            int index = mViewPager.getCurrentItem() + 1;
                            if(index >= mEntry.getCount()){
                                index = 0;
                            }
                            mViewPager.setCurrentItem(index);
                        }
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask, 5000L, 5000L);
    }

    private void unregisterTimer(){
        if(mTimer != null){
            mTimer.cancel();
        }
    }

    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_town_data, null);
        mLayoutLabel = (ViewGroup) view.findViewById(R.id.layout_fragment_town_data_label);
        mDataTitle = (TextView) view.findViewById(R.id.tv_fragment_town_data_title);
        mDataDetail = (ImageView) view.findViewById(R.id.iv_fragment_town_data_detail_info);
        mDataView = (ImageView) view.findViewById(R.id.iv_fragment_town_data_detail_view);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_fragment_town_data);

        mDataDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    Toast.makeText(getActivity(), mEntry.getDescription(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mLayoutLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewPager.isShown()){
                    mViewPager.setVisibility(View.GONE);
                    mDataView.setVisibility(View.VISIBLE);
                }else{
                    mViewPager.setVisibility(View.VISIBLE);
                    mDataView.setVisibility(View.GONE);
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }


            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                if(state == ViewPager.SCROLL_STATE_DRAGGING){
//                    unregisterTimer();
//                }else if(state == ViewPager.SCROLL_STATE_IDLE){
//                    registerTimer();
//                }

            }
        });
        return view;
    }

    public void setData(TownDataEntry entry){
        if(entry != null){
            mDataTitle.setText(entry.getTitle() + String.format("( %s )", entry.getCount()));
            mDataTitle.setTextColor(getPageTitleColor());
            mLayoutLabel.setBackgroundColor(getPageTitleBgColor());
            if(entry.getList() != null){
                if(mAdapter == null){
                    mAdapter = new TownDataPagerAdapter(entry.getList(), getPageTitleColor(), getPageTitleBgColor(), onClickTownDataListener);
                    mViewPager.setAdapter(mAdapter);
                }else{
                    mAdapter.setData(entry.getList());
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected int getPageTitleColor(){
        int color = Color.BLACK;
        if(mEntry != null && mEntry.getTitleColor() != null){
            color = parseColor(mEntry.getTitleColor());
        }
        return color;
    }

    @Override
    protected int getPageTitleBgColor(){
        int color = Color.WHITE;
        if(mEntry != null && mEntry.getTitleBgColor() != null){
            color = parseColor(mEntry.getTitleBgColor());
        }
        return color;
    }

    @Override
    protected int getPageContentColor(){
        int color = Color.WHITE;
        if(mEntry != null && mEntry.getContentColor() != null){
            color = parseColor(mEntry.getContentColor());
        }
        return color;
    }

    @Override
    protected int getPageContentBgColor(){
        int color = Color.BLACK;
        if(mEntry != null && mEntry.getContentBgColor() != null){
            color = parseColor(mEntry.getContentBgColor());
        }
        return color;
    }

    private TownDataPagerAdapter.OnClickTownDataListener onClickTownDataListener = new TownDataPagerAdapter.OnClickTownDataListener() {
        @Override
        public void onClick(View view, TownData data) {
            FeedMessage message = new FeedMessage();
            message.setAuthor(data.getAuthor());
            message.setDescription(data.getContent());
            message.setLink(data.getUrl());
            message.setPubDate(data.getDate());
            message.setTitle(data.getTitle());
            onClickPageData(message, getPageTitleColor(), getPageTitleBgColor());
        }
    };
}
