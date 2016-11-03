package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.fivetrue.gimpo.ac05.firebase.database.RssMessageDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.RssMessage;
import com.fivetrue.gimpo.ac05.rss.RSSFeedParser;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;
import com.fivetrue.gimpo.ac05.vo.rss.Feed;
import com.fivetrue.gimpo.ac05.vo.rss.FeedMessage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;


/**
 * Created by kwonojin on 16. 6. 15..
 */
public class FeedListActivity extends BaseListDataActivity<FeedMessage> {

    private static final String TAG = "ScrapContentListActivit";

    private RssMessageDatabase mRssMessageDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        showProgressBar();
        loadData();
    }

    private void initData(){
        mRssMessageDatabase = new RssMessageDatabase();
    }

    private void loadData(){
        mRssMessageDatabase.getReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                RssMessage rssMessage = dataSnapshot.getValue(RssMessage.class);
                if(getContentTitle().equals(rssMessage.name)){
                    new RSSFeedParser(rssMessage.rssPath, new RSSFeedParser.OnLoadFeedListener() {
                        @Override
                        public void onLoad(Feed feed) {
                            if(feed != null && feed.getMessages() != null && feed.getMessages().size() > 0){
                                setData(feed.getMessages());
                                goneProgressBar();
                                onRefreshFinish();
                            }
                        }
                    }).readFeed();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        loadData();
    }


    @Override
    public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, FeedMessage data) {
        super.onClickItem(holder, data);
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("title", data.getTitle());
        intent.putExtra("url", data.getUrl());
        intent.putExtra("image", data.getImageUrl());
        startActivity(intent);
    }
}