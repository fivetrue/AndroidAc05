package com.fivetrue.gimpo.ac05.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseListAdapter;

import java.util.List;

/**
 * Created by Fivetrue on 2016-02-11.
 */
abstract public class BaseListFragment<T> extends BaseFragment {

    private ListView mListView = null;
    private BaseListAdapter mAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = initView(inflater);
        return view;
    }

    /**
     * 뷰 초기화
     */
    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_base_list, null);
        mListView = (ListView) view.findViewById(R.id.lv_base_list);
        initListView(mListView);
        return view;
    }

    protected void setListData(List<T> datas){
        if(mAdapter == null){
            mAdapter = makeAdapter(datas);
            mListView.setAdapter(mAdapter);
        }else{
            mAdapter.setData(datas);
        }
    }

    protected abstract BaseListAdapter makeAdapter(List<T> datas);

    protected void initListView(ListView listVIew){

    }

    public ListView getListView(){
        return mListView;
    }

    public BaseListAdapter getAdapter(){
        return mAdapter;
    }
}
