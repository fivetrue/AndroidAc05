package com.fivetrue.gimpo.ac05.ui.fragment.main;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.vo.rss.FeedMessage;
import com.fivetrue.gimpo.ac05.ui.adapter.TownDataRecyclerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseDataListFragment;
import com.fivetrue.gimpo.ac05.vo.data.TownData;
import com.fivetrue.gimpo.ac05.vo.data.TownDataEntry;
import com.fivetrue.gimpo.ac05.widget.PagerSlidingTabStrip;


/**
 * Created by kwonojin on 16. 6. 15..
 */
public class TownDataListFragment extends BaseDataListFragment<TownDataEntry> implements PagerSlidingTabStrip.PagerTabContent {

    private TownDataRecyclerAdapter mAdapter = null;

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
    protected void setData(RecyclerView view, TownDataEntry data) {
        if(mAdapter == null){
            mAdapter = new TownDataRecyclerAdapter(data.getList(), getPageTitleColor(), getPageTitleBgColor());
            view.setAdapter(mAdapter);
            mAdapter.setOnClickPageDataListener(onClickTownDataListener);
        }else{
            mAdapter.setData(data.getList());
        }
    }

    @Override
    protected void setTitle(View parent, TextView textviw, TownDataEntry data) {
        parent.setVisibility(View.GONE);
    }

    @Override
    public TownDataEntry getPageData() {
        return getArguments().getParcelable(TownDataEntry.class.getName());
    }

    private TownDataRecyclerAdapter.OnClickPageDataListener onClickTownDataListener = new TownDataRecyclerAdapter.OnClickPageDataListener() {
        @Override
        public void onClickPageData(View view, TownData data) {
            TownDataListFragment.this.onClickPageData(data.getTitle(), data, getPageTitleColor(), getPageTitleBgColor());
        }
    };

    @Override
    public int getStringResource() {
        return R.string.town_news;
    }

    @Override
    public int getIconResource() {
        return 0;
    }

    @Override
    public String getTabName() {
        String name = null;
        if(getPageData() != null){
            name = getPageData().getTitle();
        }
        return name;
    }

    @Override
    public boolean isShowingIcon() {
        return false;
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        onRefreshFinish();
    }
}
