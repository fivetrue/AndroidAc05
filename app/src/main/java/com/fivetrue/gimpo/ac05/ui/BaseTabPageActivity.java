package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseFragmentPagerAdapter;
import com.fivetrue.gimpo.ac05.widget.PagerSlidingTabStrip;


/**
 * Created by So on 2016-01-28.
 */
public abstract class BaseTabPageActivity extends DrawerActivity{

    private PagerSlidingTabStrip mTabStrip = null;
    private ViewPager mViewPager = null;
    private BaseFragmentPagerAdapter mFragmentPagerAdapter = null;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_tab_page);
        initViews();
    }

    protected void initViews(){
        mTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tab_base_strip);
        mViewPager = (ViewPager) findViewById(R.id.pager_base);
        initTabPager(mTabStrip, mViewPager);
    }

    protected void initTabPager(PagerSlidingTabStrip strip, ViewPager pager){
        if(strip != null && pager != null){
            if(mFragmentPagerAdapter == null){
                mFragmentPagerAdapter = makePagerAdapter();
                pager.setAdapter(mFragmentPagerAdapter);
            }
            strip.setViewPager(pager);
        }
    }


    protected abstract BaseFragmentPagerAdapter makePagerAdapter();

}
