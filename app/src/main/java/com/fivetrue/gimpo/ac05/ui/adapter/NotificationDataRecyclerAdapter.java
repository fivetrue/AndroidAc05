package com.fivetrue.gimpo.ac05.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.rss.FeedMessage;
import com.fivetrue.gimpo.ac05.service.notification.NotificationData;
import com.fivetrue.gimpo.ac05.ui.adapter.pager.NoticeDataPagerAdapter;
import com.fivetrue.gimpo.ac05.utils.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class NotificationDataRecyclerAdapter extends BaseRecyclerAdapter<NotificationData, NotificationDataRecyclerAdapter.PageDataHolder> {

    private static final String TAG = "NotificationDataRecyclerAdapter";


    private NoticeDataPagerAdapter.OnClickNoticeDataListener mOnClickPageDataListener = null;

    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public NotificationDataRecyclerAdapter(List<NotificationData> data, NoticeDataPagerAdapter.OnClickNoticeDataListener ll) {
        super(data, R.layout.item_page_data_list);
        mOnClickPageDataListener = ll;
    }



    @Override
    protected PageDataHolder makeHolder(View view) {
        PageDataHolder holder = new PageDataHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final PageDataHolder holder, int position) {
        final NotificationData data = getItem(position);
        if (data != null) {
            holder.title.setText(data.getTitle());
            holder.title.setTextColor(holder.title.getResources().getColor(R.color.colorAccent));
            holder.layoutTop.setBackgroundColor(holder.title.getResources().getColor(R.color.colorNegative));
            if(data.getImageUrl() != null){
                holder.imageView.setImageUrl(data.getImageUrl(), ImageLoadManager.getImageLoader());
            }
            holder.content.setText(data.getMessage());

            holder.date.setText(holder.date.getResources().getString(R.string.create_date)
                    + " " + mSdf.format(new Date(data.getCreateTime())));
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickPageDataListener != null) {
                        mOnClickPageDataListener.onClick(v, data);
                    }
                }
            });
            holder.container.setVisibility(View.VISIBLE);

        }
    }

    public static class PageDataHolder extends RecyclerView.ViewHolder{

        protected View container = null;
        protected View layoutTop = null;
        protected TextView title = null;
        protected TextView date = null;
        protected NetworkImageView imageView = null;
        protected TextView content = null;

        public PageDataHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.page_data_view);
            title = (TextView) itemView.findViewById(R.id.tv_item_page_data_list_title);
            date = (TextView) itemView.findViewById(R.id.tv_item_page_data_list_date);
            layoutTop = itemView.findViewById(R.id.layout_item_page_data_list_top);
            imageView = (NetworkImageView) itemView.findViewById(R.id.iv_item_page_data_list_image);
            content = (TextView) itemView.findViewById(R.id.tv_item_page_data_list_content);
            TranslateAnimation anim = new TranslateAnimation(0, 0, -100, 0);
            container.setAnimation(anim);
            container.setVisibility(View.GONE);
        }
    }
}
