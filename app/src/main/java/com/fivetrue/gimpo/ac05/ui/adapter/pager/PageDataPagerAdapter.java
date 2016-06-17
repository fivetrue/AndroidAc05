package com.fivetrue.gimpo.ac05.ui.adapter.pager;

import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.gimpo.ac05.ui.adapter.BasePagerAdapter;
import com.fivetrue.gimpo.ac05.view.PageDataView;
import com.fivetrue.gimpo.ac05.vo.data.PageData;

import java.util.List;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class PageDataPagerAdapter extends BasePagerAdapter {

    private List<PageData> mData = null;

    public PageDataPagerAdapter(List<PageData> data){
        mData = data;
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PageData data = (PageData) getItem(position);
        PageDataView view = new PageDataView(container.getContext());
        view.setPageData(data);
        container.addView(view, 0);
        return view;
    }

    public void setData(List<PageData> data){
        mData = data;
        notifyDataSetChanged();
    }
}
