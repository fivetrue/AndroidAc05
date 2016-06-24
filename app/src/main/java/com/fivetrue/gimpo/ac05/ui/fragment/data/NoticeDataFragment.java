package com.fivetrue.gimpo.ac05.ui.fragment.data;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.rss.FeedMessage;
import com.fivetrue.gimpo.ac05.service.notification.NotificationData;
import com.fivetrue.gimpo.ac05.ui.adapter.pager.NoticeDataPagerAdapter;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by kwonojin on 16. 6. 15..
 */
public class NoticeDataFragment extends ColorChooserFragment {

    private ViewGroup mLayoutLabel = null;
    private TextView mDataTitle = null;
    private ImageView mDataView = null;
    private ImageView mDataDetail = null;

    private ImageView mArrowRight = null;
    private ImageView mArrowLeft = null;
    private ViewPager mViewPager = null;

    private LinearLayout mIndexLayout = null;

    private NoticeDataPagerAdapter mAdapter = null;

    private ArrayList<NotificationData> mEntry = null;


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
        mEntry = getArguments().getParcelableArrayList(NotificationData.class.getName());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_town_data, null);
        mLayoutLabel = (ViewGroup) view.findViewById(R.id.layout_fragment_town_data_label);
        mDataTitle = (TextView) view.findViewById(R.id.tv_fragment_town_data_title);
        mDataDetail = (ImageView) view.findViewById(R.id.iv_fragment_town_data_detail_info);
        mDataView = (ImageView) view.findViewById(R.id.iv_fragment_town_data_detail_view);

        mArrowLeft = (ImageView) view.findViewById(R.id.iv_fragment_town_data_detail_arrow_left);
        mArrowRight = (ImageView) view.findViewById(R.id.iv_fragment_town_data_detail_arrow_right);

        mViewPager = (ViewPager) view.findViewById(R.id.vp_fragment_town_data);

        mIndexLayout = (LinearLayout) view.findViewById(R.id.layout_fragment_town_data_index);

        mArrowLeft.setVisibility(View.GONE);

        mDataDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    Toast.makeText(getActivity(), "공지사항", Toast.LENGTH_SHORT).show();
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

        mArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewPager != null){
                    if(mViewPager.getCurrentItem() > 0){
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                    }
                }
            }
        });

        mArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewPager != null && mAdapter != null){
                    if(mViewPager.getCurrentItem() < mAdapter.getCount()){
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }


            @Override
            public void onPageSelected(int position) {
                if(mIndexLayout != null && mIndexLayout.getChildCount() > 0){
                    for(int i = 0 ; i < mIndexLayout.getChildCount() ; i++){
                        mIndexLayout.getChildAt(i).setSelected(false);
                    }
                }
                mIndexLayout.getChildAt(position).setSelected(true);
                mArrowLeft.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
                mArrowRight.setVisibility(position < mAdapter.getCount() - 1 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        return view;
    }

    public void setData(ArrayList<NotificationData> entry){
        if(entry != null){
            mDataTitle.setText("공지사항" + String.format("( %s )", entry.size()));
            mDataTitle.setTextColor(getPageTitleColor());
            mLayoutLabel.setBackgroundColor(getPageTitleBgColor());
            if(entry != null){
                if(mAdapter == null){
                    mAdapter = new NoticeDataPagerAdapter(entry, getPageTitleColor(), getPageTitleBgColor(), onClickNoticeDataListener);
                    mViewPager.setAdapter(mAdapter);
                }else{
                    mAdapter.setData(entry);
                    mAdapter.notifyDataSetChanged();
                }
            }
            mIndexLayout.removeAllViews();
            for(int i = 0 ; i < entry.size() ; i ++){
                View v = new ImageView(getActivity());
                v.setBackground(getResources().getDrawable(R.drawable.common_index));
                float density = getResources().getDisplayMetrics().density;
                int size = (int) (10 * density);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                params.leftMargin = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
//                params.rightMargin = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
                mIndexLayout.addView(v, params);
                v.setSelected(false);
            }
            if(mIndexLayout.getChildCount() > 0){
                mIndexLayout.getChildAt(0).setSelected(true);
            }
        }
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

    private NoticeDataPagerAdapter.OnClickNoticeDataListener onClickNoticeDataListener = new NoticeDataPagerAdapter.OnClickNoticeDataListener() {
        @Override
        public void onClick(View view, NotificationData data) {
            FeedMessage message = new FeedMessage();
            message.setAuthor(data.getAuthorNickname());
            message.setDescription(data.getMessage());
            message.setLink(data.getUri());
            message.setPubDate(new Date(data.getCreateTime()).toString());
            message.setTitle(data.getTitle());
            onClickPageData("공지사항", message, getPageTitleColor(), getPageTitleBgColor());
        }
    };
}
