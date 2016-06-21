package com.fivetrue.gimpo.ac05.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.rss.Feed;
import com.fivetrue.gimpo.ac05.rss.FeedMessage;
import com.fivetrue.gimpo.ac05.rss.RSSFeedParser;
import com.fivetrue.gimpo.ac05.ui.adapter.PageDataRecyclerAdapter;
import com.fivetrue.gimpo.ac05.vo.data.PageData;

import java.util.List;

/**
 * Created by kwonojin on 16. 6. 17..
 */
public class PageDataListActivity extends BaseActivity{


    private PageData mEntry = null;

    private RecyclerView mRecylerView = null;
    private TextView mEmptyText = null;

    private PageDataRecyclerAdapter mAdapter = null;

    private Feed mFeed = null;

    private int mTitleColor = 0;
    private int mTitleBgColor = 0;

    private int mContentColor = 0;
    private int mContentBgColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_data_list);
        initData();
        initView();
        setData(mEntry);
    }

    private void initData(){
        mEntry = getIntent().getParcelableExtra(PageData.class.getName());
        mTitleColor = Color.parseColor(mEntry.getTitleColor());
        mTitleBgColor = Color.parseColor(mEntry.getTitleBgColor());
        mContentColor = Color.parseColor(mEntry.getContentColor());
        mContentBgColor = Color.parseColor(mEntry.getContentBgColor());
    }

    private void initView(){
        mRecylerView = (RecyclerView) findViewById(R.id.rv_page_data_list);
        mEmptyText = (TextView) findViewById(R.id.tv_page_data_empty);

        mRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mEmptyText.setTextColor(mContentColor);
        mEmptyText.setBackgroundColor(mContentBgColor);
    }

    protected void setData(PageData entry){
        if(entry != null){

            if(mFeed == null){
                new RSSFeedParser(entry.getPageUrl(), new RSSFeedParser.OnLoadFeedListener() {
                    @Override
                    public void onLoad(Feed feed) {
                        mFeed = feed;
                        setListData(mFeed);
                    }
                }).readFeed();
            }else{
                setListData(mFeed);
            }
        }else{
            mEmptyText.setVisibility(View.VISIBLE);
        }
    }

    private void setListData(Feed feed){
        if(feed != null){
            if(mAdapter == null){
                mAdapter = new PageDataRecyclerAdapter(feed.getMessages(), mTitleColor, mTitleBgColor);
                mRecylerView.setAdapter(mAdapter);
            }else{
                mAdapter.setData(feed.getMessages());
            }

            if(feed.getMessages() != null){
                mEmptyText.setVisibility(View.GONE);

            }
            getFtActionBar().setTitle(feed.getTitle());
        }
    }
}
