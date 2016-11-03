package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.fivetrue.gimpo.ac05.database.TownNewsLocalDB;
import com.fivetrue.gimpo.ac05.firebase.model.TownNews;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class TownDataListActivity extends BaseListDataActivity<TownNews> {

    private static final String TAG = "TownDataListActivity";


    private TownNewsLocalDB mTownNewsLocalDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setData(mTownNewsLocalDB.getTownNews(true));
        goneProgressBar();
    }

    private void initData() {
        mTownNewsLocalDB = new TownNewsLocalDB(this);
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        setData(mTownNewsLocalDB.getTownNews(true));
        onRefreshFinish();
    }



    @Override
    public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, TownNews data) {
        super.onClickItem(holder, data);
        Intent intent = new Intent(this, TownWebViewActivity.class);
        intent.putExtra(TownNews.class.getName(), data);
        startActivity(intent);
    }
}
