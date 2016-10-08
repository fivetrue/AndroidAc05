package com.fivetrue.gimpo.ac05.ui.adapter.pager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.fivetrue.fivetrueandroid.ui.adapter.BaseFragmentPagerAdapter;
import com.fivetrue.fivetrueandroid.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;
import com.fivetrue.gimpo.ac05.ui.NotificationDataListActivity;
import com.fivetrue.gimpo.ac05.vo.data.MainDataEntry;
import com.fivetrue.gimpo.ac05.vo.data.PageData;
import com.fivetrue.gimpo.ac05.vo.data.TownDataEntry;

import java.util.HashMap;

/**
 * Created by kwonojin on 16. 6. 29..
 */
public class MainFragmentViewPagerAdapter extends BaseFragmentPagerAdapter {

    private static final int DEFAULT_COUNT = 2;
    private MainDataEntry mMainDataEntry = null;

    private int mCount = 0;

    private HashMap<Integer, BaseFragment> mFragmentMap = null;

    public MainFragmentViewPagerAdapter(FragmentManager fm, MainDataEntry mainDataEntry) {
        super(fm);
        mMainDataEntry = mainDataEntry;
        mCount += DEFAULT_COUNT + mMainDataEntry.getPages().size();
        mFragmentMap = new HashMap<>();
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
        if(mFragmentMap.get(position) != null){
            return mFragmentMap.get(position);
        }else{
            Bundle b = new Bundle();
            BaseFragment f = null;
            if(DEFAULT_COUNT > position){
                switch(position){
                    case 0 :
                        b.putParcelableArrayList(NotificationData.class.getName(), mMainDataEntry.getNotices());
//                        f = new NotificationDataListActivity();
//                        f.setArguments(b);
                        break;
                    case 1 :
                        b.putParcelable(TownDataEntry.class.getName(), mMainDataEntry.getTown());
                        break;
                }
            }else{
                b.putParcelable(PageData.class.getName(), mMainDataEntry.getPages().get(position - DEFAULT_COUNT));
                b.putString("name", mMainDataEntry.getPages().get(position - DEFAULT_COUNT).getTitle());
                f.setArguments(b);
            }
            mFragmentMap.put(position, f);
            return f;
        }
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
