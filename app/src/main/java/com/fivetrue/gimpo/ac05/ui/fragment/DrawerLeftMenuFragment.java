package com.fivetrue.gimpo.ac05.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.CafeActivity;
import com.fivetrue.gimpo.ac05.ui.MainActivity;
import com.fivetrue.gimpo.ac05.ui.SettingActivity;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseListAdapter;
import com.fivetrue.gimpo.ac05.ui.adapter.LeftMenuListAdapter;
import com.fivetrue.gimpo.ac05.vo.LeftMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fivetrue on 2016-02-15.
 */
public class DrawerLeftMenuFragment extends  BaseListFragment<LeftMenu> {

    public interface OnMenuClickItemListener{
        void onClickMenu(LeftMenu menu);
    }

    public static final String TAG = DrawerLeftMenuFragment.class.getSimpleName();
    private ArrayList<LeftMenu> mLeftMenu = null;
    private OnMenuClickItemListener mOnMenuClickItemListener = null;

    public DrawerLeftMenuFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    protected BaseListAdapter makeAdapter(List<LeftMenu> datas) {
        return new LeftMenuListAdapter(getActivity(), datas);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setListData(mLeftMenu);
        return view;
    }

    private void initData(){
        mLeftMenu = new ArrayList<>();
        mLeftMenu.add(new LeftMenu(getString(R.string.main), MainActivity.class));
        mLeftMenu.add(new LeftMenu(getString(R.string.cafe), CafeActivity.class));
        mLeftMenu.add(new LeftMenu(getString(R.string.setting), SettingActivity.class));
    }

    @Override
    protected void initListView(ListView listVIew) {
        super.initListView(listVIew);
        getListView().setVerticalScrollBarEnabled(false);
        listVIew.setBackgroundColor(getResources().getColor(android.R.color.white));
        listVIew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(getAdapter() != null){
                    LeftMenu menu = (LeftMenu) getAdapter().getItem(i);
                    onMenuClickItemListener.onClickMenu(menu);
                }
            }
        });
    }

    private OnMenuClickItemListener onMenuClickItemListener = new OnMenuClickItemListener() {
        @Override
        public void onClickMenu(LeftMenu menu) {
            if(mOnMenuClickItemListener != null){
                mOnMenuClickItemListener.onClickMenu(menu);
            }
        }
    };

    public void setOnMenuClickItemListener(OnMenuClickItemListener ll){
        mOnMenuClickItemListener = ll;
    }
}
