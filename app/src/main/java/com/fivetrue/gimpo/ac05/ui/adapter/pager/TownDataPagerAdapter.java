package com.fivetrue.gimpo.ac05.ui.adapter.pager;

import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.ui.adapter.BasePagerAdapter;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.data.PageData;
import com.fivetrue.gimpo.ac05.vo.data.TownData;

import java.util.List;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class TownDataPagerAdapter extends BasePagerAdapter {

    private static final String TAG = "TownDataPagerAdapter";

    public interface OnClickTownDataListener{
        void onClick(View view, TownData data);
    }

    private List<TownData> mData = null;

    private int mContentColor = 0;
    private int mContentBgColor = 0;
    private OnClickTownDataListener mOnClickTownDataListener = null;

    public TownDataPagerAdapter(List<TownData> data, int contentColor, int contentBgColor, OnClickTownDataListener ll){
        mData = data;
        mContentColor = contentColor;
        mContentBgColor = contentBgColor;
        mOnClickTownDataListener = ll;
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
        final TownData data = (TownData) getItem(position);
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_page_data_list, null);
        TextView title = (TextView) view.findViewById(R.id.tv_item_page_data_list_title);
        TextView date = (TextView) view.findViewById(R.id.tv_item_page_data_list_date);
        TextView content = (TextView) view.findViewById(R.id.tv_item_page_data_list_content);
        View top = view.findViewById(R.id.layout_item_page_data_list_top);
        View body = view.findViewById(R.id.page_data_view);
        body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickTownDataListener != null){
                    mOnClickTownDataListener.onClick(v, data);
                }
            }
        });
        top.setBackgroundColor(mContentBgColor);
        title.setTextColor(mContentColor);
        NetworkImageView background = (NetworkImageView) view.findViewById(R.id.iv_item_page_data_list_image);
        title.setText(data.getTitle());
        if(data.getContent() != null){
            content.setText(Html.fromHtml(data.getContent()));
            String token = "src=\"";
            if(data.getContent().contains(token)) {
                int startTokenIndex = data.getContent().indexOf(token);
                String imgUrl = data.getContent().substring(startTokenIndex + token.length());
                imgUrl = imgUrl.substring(0, imgUrl.indexOf("\""));
                Log.i(TAG, "setPageData: " + imgUrl);
                background.setImageUrl(imgUrl, ImageLoadManager.getImageLoader());
            }            }
        if(data.getDate() != null){
            String postDate = date.getResources().getString(R.string.create_date) + " " + data.getDate();
            date.setText(postDate);
        }


//        PageDataView view = new PageDataView(container.getContext());
//        view.setPageData(data);
        container.addView(view, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    public void setData(List<TownData> data){
        mData = data;
        notifyDataSetChanged();
    }
}
