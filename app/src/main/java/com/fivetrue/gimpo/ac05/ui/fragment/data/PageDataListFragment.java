package com.fivetrue.gimpo.ac05.ui.fragment.data;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.rss.Feed;
import com.fivetrue.gimpo.ac05.rss.FeedMessage;
import com.fivetrue.gimpo.ac05.rss.RSSFeedParser;
import com.fivetrue.gimpo.ac05.ui.adapter.PageDataRecyclerAdapter;
import com.fivetrue.gimpo.ac05.vo.data.PageData;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class PageDataListFragment extends ColorChooserFragment {

    private static final String TAG = "PageDataListFragment";

    private ViewGroup mLayoutLabel = null;
    private TextView mDataTitle = null;
    private ImageView mDataDetail = null;
    private ImageView mDataViewIcon = null;
    private RecyclerView mRecyclerView = null;
    private PageDataRecyclerAdapter mAdapter = null;

    private PageData mPageData = null;
    private Feed mFeed = null;

    private boolean mScrollEnable = true;

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
        setData(mPageData);
    }

    protected void initData(){
        mPageData = getArguments().getParcelable(PageData.class.getName());
    }

    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_list_page_data, null);
        mLayoutLabel = (ViewGroup) view.findViewById(R.id.layout_fragment_list_label);
        mDataTitle = (TextView) view.findViewById(R.id.tv_fragment_list_page_data_title);
        mDataDetail = (ImageView) view.findViewById(R.id.iv_fragment_list_page_data_detail_info);
        mDataViewIcon = (ImageView) view.findViewById(R.id.iv_fragment_list_page_data_detail_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_fragment_list_page_data);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setNestedScrollingEnabled(mScrollEnable);
        mDataDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    Toast.makeText(getActivity(), mPageData.getContentDescription(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mLayoutLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecyclerView != null && mDataViewIcon != null) {
                    if (mRecyclerView.isShown()) {
                        mRecyclerView.setVisibility(View.GONE);
                        mDataViewIcon.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mDataViewIcon.setVisibility(View.GONE);
                    }
                }
            }
        });

        return view;
    }

    public void setData(final PageData data){
        if(data != null){
            if(mFeed == null){
                new RSSFeedParser(data.getPageUrl(), new RSSFeedParser.OnLoadFeedListener() {
                    @Override
                    public void onLoad(Feed feed) {
                        mFeed = feed;
                        setInternalData(data, feed);
                    }
                }).readFeed();
            }else{
                setInternalData(data, mFeed);
            }
        }
    }

    private void setInternalData(PageData data, Feed feed){
        mDataTitle.setText(data.getPageTitle() + String.format("( %s )", feed.getMessages().size()));
        mDataTitle.setTextColor(getPageTitleColor());
        mLayoutLabel.setBackgroundColor(getPageTitleBgColor());
        if(feed.getMessages() != null){
            if(mAdapter == null){
                mAdapter = new PageDataRecyclerAdapter(feed.getMessages(), getPageContentColor(), getPageContentBgColor());
                mAdapter.setOnClickPageDataListener(new PageDataRecyclerAdapter.OnClickPageDataListener() {
                    @Override
                    public void onClickPageData(View view, FeedMessage data) {
                        PageDataListFragment.this.onClickPageData(data, getPageTitleColor(), getPageTitleBgColor());
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.setData(feed.getMessages());
            }
        }
    }

    public void setNestedScrollingEnabled(boolean b){
        mScrollEnable = b;
        if(mRecyclerView != null){
            mRecyclerView.setNestedScrollingEnabled(b);
        }
    }

    public PageData getPageData(){
        return mPageData;
    }


    @Override
    protected int getPageTitleColor(){
        int color = Color.BLACK;
        if(mPageData != null && mPageData.getTitleColor() != null){
            color = parseColor(mPageData.getTitleColor());
        }
        return color;
    }

    @Override
    protected int getPageTitleBgColor(){
        int color = Color.WHITE;
        if(mPageData != null && mPageData.getTitleBgColor() != null){
            color = parseColor(mPageData.getTitleBgColor());
        }
        return color;
    }

    @Override
    protected int getPageContentColor(){
        int color = Color.WHITE;
        if(mPageData != null && mPageData.getContentColor() != null){
            color = parseColor(mPageData.getContentColor());
        }
        return color;
    }

    @Override
    protected int getPageContentBgColor(){
        int color = Color.BLACK;
        if(mPageData != null && mPageData.getContentBgColor() != null){
            color = parseColor(mPageData.getContentBgColor());
        }
        return color;
    }
}
