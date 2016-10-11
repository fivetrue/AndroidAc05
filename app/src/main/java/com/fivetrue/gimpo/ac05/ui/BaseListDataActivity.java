package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;
import com.fivetrue.gimpo.ac05.vo.IBaseItem;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class BaseListDataActivity<T extends IBaseItem> extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener<T, BaseItemListAdapter.BaseItemViewHolder> {

    private static final String TAG = "PageDataListFragment";

    private SwipeRefreshLayout mRefreshLayout = null;
    private RecyclerView mRecyclerView = null;

    private ProgressBar mProgressBar = null;

    private BaseItemListAdapter<T> mAdapter = null;

    private String mTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list_data);
        initData();
        initView();
    }

    private void initData(){
        mTitle = getIntent().getStringExtra("title");
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_base_list_data);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_base_list_data);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BaseListDataActivity.this.onRefresh();
            }
        });

        mRefreshLayout.setColorSchemeResources(
                R.color.colorPrimaryDark,
                R.color.colorPrimaryDark,
                R.color.colorPrimaryDark);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_base_list_data);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        SlideInUpAnimator animator = new SlideInUpAnimator();
        mRecyclerView.setItemAnimator(animator);

        getSupportActionBar().setTitle(mTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setData(ArrayList<T> data){
        if(data != null && data.size() > 0){
            if(mAdapter == null){
                mAdapter = new BaseItemListAdapter<>(data, getAdapterItemLayout());
                mAdapter.setOnItemClickListener(this);
                mRecyclerView.setAdapter(mAdapter);
            }else{
                mAdapter.setData(data);
            }
        }
    }

    public void addData(ArrayList<T> data){
        if(data != null && data.size() > 0){
            mAdapter.getData().addAll(data);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setNestedScrollingEnabled(boolean b){
        if(mRecyclerView != null){
            mRecyclerView.setNestedScrollingEnabled(b);
        }
    }


    protected void onClickPageDetail(T data){

    }

    protected void onRefresh(){

    }

    protected void onRefreshFinish(){
        mRefreshLayout.setRefreshing(false);
    }

    protected void showProgressBar(){
        if(mProgressBar != null){
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void goneProgressBar(){
        if(mProgressBar != null){
            mProgressBar.setVisibility(View.GONE);
        }
    }

    protected int getAdapterItemLayout(){
        return R.layout.item_base_list_item_vertical;
    }

    @Override
    public void onClickItem(BaseItemListAdapter.BaseItemViewHolder holder, T data) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
