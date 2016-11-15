package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.fivetrue.gimpo.ac05.firebase.database.TownNewsDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.TownNews;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class TownDataListActivity extends BaseListDataActivity<TownNews> {

    private static final String TAG = "TownDataListActivity";

    private TownNewsDatabase mTownDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        loadData();
    }

    private void loadData(){
        showProgressBar();
        mTownDatabase.getReference().orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<TownNews> townNewsArrayList = new ArrayList<TownNews>();
                for(DataSnapshot s : dataSnapshot.getChildren()){
                    TownNews content = s.getValue(TownNews.class);
                    content.key = s.getKey();
                    townNewsArrayList.add(0, content);
                }
                setData(townNewsArrayList);
                onRefreshFinish();
                goneProgressBar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initData() {
        mTownDatabase = new TownNewsDatabase();
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        loadData();
    }

    @Override
    public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, TownNews data) {
        super.onClickItem(holder, data);
        Intent intent = new Intent(this, TownWebViewActivity.class);
        intent.putExtra(TownNews.class.getName(), data);
        startActivity(intent);
    }
}
