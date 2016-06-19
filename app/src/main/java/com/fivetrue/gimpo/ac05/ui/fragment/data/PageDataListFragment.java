package com.fivetrue.gimpo.ac05.ui.fragment.data;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.adapter.PageDataRecyclerAdapter;
import com.fivetrue.gimpo.ac05.vo.data.PageData;
import com.fivetrue.gimpo.ac05.vo.data.PageDataEntry;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class PageDataListFragment extends BasePageDataFragment {

    private ViewGroup mLayoutLabel = null;
    private TextView mDataTitle = null;
    private ImageView mDataDetail = null;
    private ImageView mDataViewIcon = null;
    private RecyclerView mRecyclerView = null;
    private PageDataRecyclerAdapter mAdapter = null;

    private boolean mScrollEnable = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView(inflater);
        return view;
    }

    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_list_page_data, null);
        mLayoutLabel = (ViewGroup) view.findViewById(R.id.layout_fragment_list_label);
        mDataTitle = (TextView) view.findViewById(R.id.tv_fragment_list_page_data_title);
        mDataDetail = (ImageView) view.findViewById(R.id.iv_fragment_list_page_data_detail_info);
        mDataViewIcon = (ImageView) view.findViewById(R.id.iv_fragment_list_page_data_detail_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_fragment_list_page_data);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setNestedScrollingEnabled(mScrollEnable);
        mDataDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPageDetail(getPageEntry());
            }
        });
        mLayoutLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRecyclerView != null && mDataViewIcon != null){
                    if(mRecyclerView.isShown()){
                        mRecyclerView.setVisibility(View.GONE);
                        mDataViewIcon.setVisibility(View.VISIBLE);
                    }else{
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mDataViewIcon.setVisibility(View.GONE);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void setData(final PageDataEntry entry){
        if(entry != null){
            mDataTitle.setText(entry.getDataTitle() + String.format("( %s )", entry.getCount()));
            mDataTitle.setTextColor(getPageTitleColor());
            mLayoutLabel.setBackgroundColor(getPageTitleBgColor());
            if(entry.getPages() != null){
                if(mAdapter == null){
                    mAdapter = new PageDataRecyclerAdapter(entry.getPages(), getPageContentColor(), getPageContentBgColor());
                    mAdapter.setOnClickPageDataListener(new PageDataRecyclerAdapter.OnClickPageDataListener() {
                        @Override
                        public void onClickPageData(View view, PageData data) {
                            PageDataListFragment.this.onClickPageData(entry, data, getPageTitleColor(), getPageTitleBgColor());
                        }
                    });
                    mRecyclerView.setAdapter(mAdapter);
                }else{
                    mAdapter.setData(entry.getPages());
                }
            }
        }
    }

    public void setNestedScrollingEnabled(boolean b){
        mScrollEnable = b;
        if(mRecyclerView != null){
            mRecyclerView.setNestedScrollingEnabled(b);
        }
    }
}
