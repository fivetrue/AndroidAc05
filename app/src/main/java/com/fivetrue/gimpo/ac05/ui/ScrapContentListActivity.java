package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.fivetrue.gimpo.ac05.firebase.database.ScrapContentDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.ScrapContent;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class ScrapContentListActivity extends BaseListDataActivity<ScrapContent> {

    private static final String TAG = "ScrapContentListActivit";

    private ScrapContentDatabase mScrapContentDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        showProgressBar();
        loadData();
    }

    private void initData(){
        mScrapContentDatabase = new ScrapContentDatabase();
    }

    private void loadData(){
        mScrapContentDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<ScrapContent> pages = new ArrayList<ScrapContent>();
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    ScrapContent content = s.getValue(ScrapContent.class);
                    content.key = s.getKey();
                    pages.add(0, content);
                }
                setData(pages);
                goneProgressBar();
                onRefreshFinish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                goneProgressBar();
                onRefreshFinish();
            }
        });
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        loadData();
    }


    @Override
    public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, ScrapContent data) {
        super.onClickItem(holder, data);
        Intent intent = new Intent(this, ScrapContentActivity.class);
        intent.putExtra(ScrapContent.class.getName(), data);
        startActivity(intent);
    }
}