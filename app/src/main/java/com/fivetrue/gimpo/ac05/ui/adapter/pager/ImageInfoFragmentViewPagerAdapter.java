package com.fivetrue.gimpo.ac05.ui.adapter.pager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.fivetrue.gimpo.ac05.ui.adapter.BaseFragmentPagerAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.ImageInfomationFragment;
import com.fivetrue.gimpo.ac05.vo.data.ImageInfoEntry;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kwonojin on 16. 6. 29..
 */
public class ImageInfoFragmentViewPagerAdapter extends BaseFragmentPagerAdapter {

    private ArrayList<ImageInfoEntry> mImageInfoDatas = null;

    private HashMap<ImageInfoEntry, BaseFragment> mFragmentMap = null;


    public ImageInfoFragmentViewPagerAdapter(FragmentManager fm, ArrayList<ImageInfoEntry> datas) {
        super(fm);
        mImageInfoDatas = datas;
        mFragmentMap = new HashMap<>();
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
        BaseFragment f = mFragmentMap.get(infoEntry);
        if(f == null){
            Bundle b = new Bundle();
            b.putParcelable(ImageInfoEntry.class.getName(), infoEntry);
            f = new ImageInfomationFragment();
            f.setArguments(b);
            mFragmentMap.put(infoEntry, f);
        }
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
