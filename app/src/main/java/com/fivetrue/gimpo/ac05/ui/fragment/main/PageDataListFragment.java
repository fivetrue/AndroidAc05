package com.fivetrue.gimpo.ac05.ui.fragment.main;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.vo.rss.Feed;
import com.fivetrue.gimpo.ac05.vo.rss.FeedMessage;
import com.fivetrue.gimpo.ac05.rss.RSSFeedParser;
import com.fivetrue.gimpo.ac05.ui.adapter.PageDataRecyclerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseDataListFragment;
import com.fivetrue.gimpo.ac05.vo.data.PageData;
import com.fivetrue.fivetrueandroid.widget.PagerSlidingTabStrip;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class PageDataListFragment extends BaseDataListFragment<PageData> implements PagerSlidingTabStrip.PagerTabContent {

    private static final String TAG = "PageDataListFragment";

    private PageDataRecyclerAdapter mAdapter = null;

    private Feed mFeed = null;
    private boolean mShowingNew = false;

    public PageDataListFragment(){

    }

    private void setInternalData(RecyclerView view, PageData data, Feed feed){
        if(feed != null && feed.getMessages() != null){
            if(mAdapter == null){
                mAdapter = new PageDataRecyclerAdapter(feed.getMessages(), getPageContentColor(), getPageContentBgColor());
                mAdapter.setOnClickPageDataListener(new PageDataRecyclerAdapter.OnClickPageDataListener() {
                    @Override
                    public void onClickPageData(View view, FeedMessage data) {
//                        PageDataListFragment.this.onClickPageData(data.getTitle(), data, getPageTitleColor(), getPageTitleBgColor());
                    }

                    @Override
                    public void onShowingNewIcon(FeedMessage data) {
                        if(!mShowingNew){
                            mShowingNew = true;
                            onChangePagerContent(PageDataListFragment.this);
                        }
                    }
                });
                view.setAdapter(mAdapter);
            }else{
                mAdapter.setData(feed.getMessages());
            }
        }
    }


    @Override
    protected int getPageTitleColor(){
        int color = Color.BLACK;
        if(getPageData() != null && getPageData().getTitleColor() != null){
            color = parseColor(getPageData().getTitleColor());
        }
        return color;
    }

    @Override
    protected int getPageTitleBgColor(){
        int color = Color.WHITE;
        if(getPageData() != null && getPageData().getTitleBgColor() != null){
            color = parseColor(getPageData().getTitleBgColor());
        }
        return color;
    }

    @Override
    protected int getPageContentColor(){
        int color = Color.WHITE;
        if(getPageData() != null && getPageData().getContentColor() != null){
            color = parseColor(getPageData().getContentColor());
        }
        return color;
    }

    @Override
    protected int getPageContentBgColor(){
        int color = Color.BLACK;
        if(getPageData() != null && getPageData().getContentBgColor() != null){
            color = parseColor(getPageData().getContentBgColor());
        }
        return color;
    }

    @Override
    protected void setData(final RecyclerView view, final PageData data) {
        if(mFeed == null){
            new RSSFeedParser(data.getPageUrl(), new RSSFeedParser.OnLoadFeedListener() {
                @Override
                public void onLoad(Feed feed) {
                    mFeed = feed;
                    setInternalData(view, data, feed);
                }
            }).readFeed();
        }else{
            setInternalData(view, data, mFeed);
        }

    }

    @Override
    protected void setTitle(View parent, TextView textviw, PageData data) {
        parent.setVisibility(View.GONE);
    }

    @Override
    public PageData getPageData() {
        return getArguments().getParcelable(PageData.class.getName());
    }

    @Override
    public int getStringResource() {
        return R.string.gimpo_journal;
    }

    @Override
    public int getIconResource() {
        return R.drawable.new_noti_icon;
    }

    @Override
    public String getTabName() {
        return getArguments().getString("name");
    }

    @Override
    public boolean isShowingIcon() {
        return mShowingNew;
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        new RSSFeedParser(getPageData().getPageUrl(), new RSSFeedParser.OnLoadFeedListener() {
            @Override
            public void onLoad(Feed feed) {
                mFeed = feed;
                mAdapter.setData(feed.getMessages());
                onRefreshFinish();
            }
        }).readFeed();
    }

}
