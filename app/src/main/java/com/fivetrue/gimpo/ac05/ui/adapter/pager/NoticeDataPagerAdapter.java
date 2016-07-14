package com.fivetrue.gimpo.ac05.ui.adapter.pager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;
import com.fivetrue.gimpo.ac05.ui.adapter.BasePagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class NoticeDataPagerAdapter extends BasePagerAdapter {

    private static final String TAG = "TownDataPagerAdapter";

    public interface OnClickNoticeDataListener{
        void onClick(View view, NotificationData data);

        void onShowNewItem(NotificationData data);
    }

    private List<NotificationData> mData = null;

    private int mContentColor = 0;
    private int mContentBgColor = 0;
    private OnClickNoticeDataListener mOnClickNoticeDataListner = null;
    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    public NoticeDataPagerAdapter(List<NotificationData> data, int contentColor, int contentBgColor, OnClickNoticeDataListener ll){
        mData = data;
        mContentColor = contentColor;
        mContentBgColor = contentBgColor;
        mOnClickNoticeDataListner = ll;
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
        final NotificationData data = (NotificationData) getItem(position);
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_page_data_list, null);
        TextView title = (TextView) view.findViewById(R.id.tv_item_page_data_list_title);
        TextView date = (TextView) view.findViewById(R.id.tv_item_page_data_list_date);
        TextView content = (TextView) view.findViewById(R.id.tv_item_page_data_list_content);
        View top = view.findViewById(R.id.layout_item_page_data_list_top);
        View body = view.findViewById(R.id.page_data_view);
        body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickNoticeDataListner != null) {
                    mOnClickNoticeDataListner.onClick(v, data);
                }
            }
        });
        top.setBackgroundColor(mContentBgColor);
        title.setTextColor(mContentColor);
        NetworkImageView background = (NetworkImageView) view.findViewById(R.id.iv_item_page_data_list_image);
        title.setText(data.getTitle());
        content.setText(data.getMessage());
        if(data.getImageUrl() != null){
            background.setImageUrl(data.getImageUrl(), ImageLoadManager.getImageLoader());
        }
        String postDate = date.getResources().getString(R.string.create_date) + " " + mSdf.format(new Date(data.getCreateTime()));
        date.setText(postDate);
        container.addView(view, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    public void setData(List<NotificationData> data){
        mData = data;
        notifyDataSetChanged();
    }
}
