package com.fivetrue.gimpo.ac05.ui.adapter.pager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.fivetrue.gimpo.ac05.ui.adapter.BaseFragmentPagerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.ImageInfomationFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.main.NoticeDataListFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.main.PageDataListFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.main.TownDataListFragment;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfo;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfoEntry;
import com.fivetrue.gimpo.ac05.vo.data.MainDataEntry;
import com.fivetrue.gimpo.ac05.vo.data.PageData;
import com.fivetrue.gimpo.ac05.vo.data.TownDataEntry;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 29..
 */
public class ImageInfoFragmentViewPagerAdapter extends BaseFragmentPagerAdapter {

    private ArrayList<ImageInfoEntry> mImageInfoDatas = null;


    public ImageInfoFragmentViewPagerAdapter(FragmentManager fm, ArrayList<ImageInfoEntry> datas) {
        super(fm);
        mImageInfoDatas = datas;
    }

    @Override
    public int getRealCount() {
        return mImageInfoDatas.size();
    }

    @Override
    public int getVirtualPosition(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        ImageInfoEntry infoEntry = mImageInfoDatas.get(position);
        Bundle b = new Bundle();
        b.putParcelable(ImageInfoEntry.class.getName(), infoEntry);
        BaseFragment f = new ImageInfomationFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public int getCount() {
        return mImageInfoDatas.size();
    }

    public void setData(ArrayList<ImageInfoEntry> data){
        mImageInfoDatas = data;
        notifyDataSetChanged();
    }

    @Override
    protected boolean ignoreDestroyObject(int position, Object object) {
        return true;
    }
}
