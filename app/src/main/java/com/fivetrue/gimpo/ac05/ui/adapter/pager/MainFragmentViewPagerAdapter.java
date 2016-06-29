package com.fivetrue.gimpo.ac05.ui.adapter.pager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseFragmentPagerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.main.NoticeDataListFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.main.PageDataListFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.main.TownDataListFragment;
import com.fivetrue.gimpo.ac05.vo.data.MainDataEntry;
import com.fivetrue.gimpo.ac05.vo.data.PageData;
import com.fivetrue.gimpo.ac05.vo.data.TownDataEntry;

/**
 * Created by kwonojin on 16. 6. 29..
 */
public class MainFragmentViewPagerAdapter extends BaseFragmentPagerAdapter {

    private static final int DEFAULT_COUNT = 2;
    private MainDataEntry mMainDataEntry = null;

    private int mCount = 0;

    public MainFragmentViewPagerAdapter(FragmentManager fm, MainDataEntry mainDataEntry) {
        super(fm);
        mMainDataEntry = mainDataEntry;
        mCount += DEFAULT_COUNT + mMainDataEntry.getPages().size();
    }

    @Override
    public int getRealCount() {
        return mCount;
    }

    @Override
    public int getVirtualPosition(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        Bundle b = new Bundle();
        BaseFragment f = null;
        if(DEFAULT_COUNT > position){
            switch(position){
                case 0 :
                    b.putParcelableArrayList(NotificationData.class.getName(), mMainDataEntry.getNotices());
                    f = new NoticeDataListFragment();
                    f.setArguments(b);
                    break;
                case 1 :
                    b.putParcelable(TownDataEntry.class.getName(), mMainDataEntry.getTown());
                    f = new TownDataListFragment();
                    f.setArguments(b);
                    break;
            }
        }else{
            b.putParcelable(PageData.class.getName(), mMainDataEntry.getPages().get(position - DEFAULT_COUNT));
            b.putString("name", mMainDataEntry.getPages().get(position - DEFAULT_COUNT).getTitle());
            f = new PageDataListFragment();
            f.setArguments(b);
        }
        return f;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    protected boolean ignoreDestroyObject(int position, Object object) {
        return true;
    }
}
