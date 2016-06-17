package com.fivetrue.gimpo.ac05.ui.fragment.data;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.adapter.pager.PageDataPagerAdapter;
import com.fivetrue.gimpo.ac05.vo.data.PageData;
import com.fivetrue.gimpo.ac05.vo.data.PageDataEntry;


/**
 * Created by kwonojin on 16. 6. 15..
 */
public class PagerPageDataFragment extends BasePageDataFragment {

    private ViewGroup mLayoutLabel = null;
    private TextView mDataTitle = null;
    private TextView mDataDetail = null;
    private ViewPager mViewPager = null;

    private PageDataPagerAdapter mAdapter = null;


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
        View view = inflater.inflate(R.layout.fragment_pager_page_data, null);
        mLayoutLabel = (ViewGroup) view.findViewById(R.id.layout_fragment_pager_data_label);
        mDataTitle = (TextView) view.findViewById(R.id.tv_fragment_pager_page_data_title);
        mDataDetail = (TextView) view.findViewById(R.id.tv_fragment_pager_page_data_detail);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_fragment_pager_page_data);

        mDataDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onClickPageDetail(getPageEntry());
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(getPageEntry() != null && getPageEntry().getPages() != null
                        && getPageEntry().getPages().size() > position){
                    PageData data = getPageEntry().getPages().get(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @Override
    public void setData(PageDataEntry entry){

        if(entry != null){
            mDataTitle.setText(entry.getDataTitle() + String.format("( %s )", entry.getCount()));
            mDataTitle.setTextColor(getPageTitleColor());
            mLayoutLabel.setBackgroundColor(getPageTitleBgColor());
            if(entry.getPages() != null){
                if(mAdapter == null){
                    mAdapter = new PageDataPagerAdapter(entry.getPages());
                    mViewPager.setAdapter(mAdapter);
                }else{
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
