package com.fivetrue.gimpo.ac05.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.vo.IPageData;
import com.fivetrue.gimpo.ac05.vo.rss.FeedMessage;

/**
 * Created by kwonojin on 16. 6. 15..
 */
abstract public class BaseDataListFragment<T> extends ColorChooserFragment {

    private static final String TAG = "PageDataListFragment";

    public interface IBaseDataListListener {
        void onClickPageData(String title, IPageData data, Integer textColor, Integer bgColor);
        void onScrollStateChanged(RecyclerView recyclerView, int newState);
        void onScrolled(RecyclerView recyclerView, int dx, int dy);
    }

    private IBaseDataListListener mOnPageDataClickListener = null;

    private ViewGroup mLayoutLabel = null;
    private TextView mDataTitle = null;
    private ImageView mDataDetail = null;
    private ImageView mDataViewIcon = null;

    private SwipeRefreshLayout mRefreshLayout = null;
    private RecyclerView mRecyclerView = null;

    private T mPageData = null;

    private boolean mScrollEnable = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof IBaseDataListListener){
            mOnPageDataClickListener = (IBaseDataListListener) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView(inflater);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData(mRecyclerView, mPageData);
        setTitle(mLayoutLabel, mDataTitle, mPageData);
    }

    protected void initData(){
        mPageData = getPageData();
    }

    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_list_page_data, null);
        mLayoutLabel = (ViewGroup) view.findViewById(R.id.layout_fragment_list_label);
        mDataTitle = (TextView) view.findViewById(R.id.tv_fragment_list_page_data_title);
        mDataDetail = (ImageView) view.findViewById(R.id.iv_fragment_list_page_data_detail_info);
        mDataViewIcon = (ImageView) view.findViewById(R.id.iv_fragment_list_page_data_detail_view);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_fragment_list);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BaseDataListFragment.this.onRefresh();
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_fragment_list_page_data);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setNestedScrollingEnabled(mScrollEnable);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(mOnPageDataClickListener != null){
                    mOnPageDataClickListener.onScrollStateChanged(recyclerView, newState);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(mOnPageDataClickListener != null){
                    mOnPageDataClickListener.onScrolled(recyclerView, dx, dy);
                }
            }
        });
        mDataDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    onClickPageDetail(mPageData);
                }
            }
        });
        mLayoutLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecyclerView != null && mDataViewIcon != null) {
                    if (mRecyclerView.isShown()) {
                        mRecyclerView.setVisibility(View.GONE);
                        mDataViewIcon.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mDataViewIcon.setVisibility(View.GONE);
                    }
                }
            }
        });

        return view;
    }

    public void setNestedScrollingEnabled(boolean b){
        mScrollEnable = b;
        if(mRecyclerView != null){
            mRecyclerView.setNestedScrollingEnabled(b);
        }
    }




    @Override
    protected int getPageTitleColor(){
        int color = Color.BLACK;
        return color;
    }

    @Override
    protected int getPageTitleBgColor(){
        int color = Color.WHITE;
        return color;
    }

    @Override
    protected int getPageContentColor(){
        int color = Color.WHITE;
        return color;
    }

    @Override
    protected int getPageContentBgColor(){
        int color = Color.BLACK;
        return color;
    }

    protected void onClickPageData(String title, IPageData data, Integer textColor, Integer bgColor){
        if(mOnPageDataClickListener != null){
            mOnPageDataClickListener.onClickPageData(title, data, textColor, bgColor);
        }
    }

    abstract protected void setData(RecyclerView view, T data);

    abstract protected void setTitle(View parent, TextView textviw, T data);

    abstract public T getPageData();

    protected void onClickPageDetail(T data){

    }

    protected void onRefresh(){


    }

    protected void onRefreshFinish(){
        mRefreshLayout.setRefreshing(false);
    }
}
