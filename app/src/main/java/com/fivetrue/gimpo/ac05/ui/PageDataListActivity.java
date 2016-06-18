package com.fivetrue.gimpo.ac05.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.adapter.PageDataRecyclerAdapter;
import com.fivetrue.gimpo.ac05.vo.data.PageDataEntry;

/**
 * Created by kwonojin on 16. 6. 17..
 */
public class PageDataListActivity extends BaseActivity{


    private PageDataEntry mEntry = null;

    private RecyclerView mRecylerView = null;
    private TextView mEmptyText = null;

    private PageDataRecyclerAdapter mAdapter = null;

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
        mEntry = getIntent().getParcelableExtra(PageDataEntry.class.getName());
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

    protected void setData(PageDataEntry entry){
        if(entry != null){
            if(entry.getPages() != null){
                mEmptyText.setVisibility(View.GONE);
                if(mAdapter == null){
                    mAdapter = new PageDataRecyclerAdapter(entry.getPages(), mTitleColor, mTitleBgColor);
                    mRecylerView.setAdapter(mAdapter);
                }else{
                    mAdapter.setData(entry.getPages());
                }
            }

            getFtActionBar().setTitle(entry.getDataTitle());
        }else{
            mEmptyText.setVisibility(View.VISIBLE);
        }
    }
}
