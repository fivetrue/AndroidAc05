package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.fivetrue.gimpo.ac05.rss.RSSFeedParser;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;
import com.fivetrue.gimpo.ac05.vo.data.PageData;
import com.fivetrue.gimpo.ac05.vo.data.TownData;
import com.fivetrue.gimpo.ac05.vo.rss.Feed;
import com.fivetrue.gimpo.ac05.vo.rss.FeedMessage;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class PageDataListActivity extends BaseListDataActivity<FeedMessage> {

    private static final String TAG = "NotificationDataListFra";

    private PageData mPageData = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        if(mPageData != null){
            if(mPageData.getFeed() != null && mPageData.getFeed().getMessages() != null){
                setData(mPageData.getFeed().getMessages());
                goneProgressBar();
            }else{
                new RSSFeedParser(mPageData.getUrl(), new RSSFeedParser.OnLoadFeedListener() {
                    @Override
                    public void onLoad(Feed feed) {
                        if(feed != null){
                            setData(feed.getMessages());
                        }
                        goneProgressBar();
                    }
                });
            }
        }
    }

    private void initData() {
        mPageData = getIntent().getParcelableExtra(PageData.class.getName());
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        new RSSFeedParser(mPageData.getUrl(), new RSSFeedParser.OnLoadFeedListener() {
            @Override
            public void onLoad(Feed feed) {
                if (feed != null) {
                    setData(feed.getMessages());
                }
                onRefreshFinish();
            }
        });
    }



    @Override
    public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, FeedMessage data) {
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
