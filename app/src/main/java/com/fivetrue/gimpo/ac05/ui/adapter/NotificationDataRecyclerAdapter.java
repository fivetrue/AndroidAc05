package com.fivetrue.gimpo.ac05.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;
import com.fivetrue.gimpo.ac05.ui.adapter.pager.NoticeDataPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class NotificationDataRecyclerAdapter extends BaseRecyclerAdapter<NotificationData, NotificationDataRecyclerAdapter.PageDataHolder> {

    private static final String TAG = "NotificationDataRecyclerAdapter";


    private NoticeDataPagerAdapter.OnClickNoticeDataListener mOnClickPageDataListener = null;

    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public NotificationDataRecyclerAdapter(List<NotificationData> data, NoticeDataPagerAdapter.OnClickNoticeDataListener ll) {
        super(data, R.layout.item_notification_data_list);
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
            holder.layoutTop.setBackgroundColor(holder.title.getResources().getColor(R.color.colorPrimaryDark));
            if(!TextUtils.isEmpty(data.getImageUrl())){
                holder.imageView.setVisibility(View.VISIBLE);
                holder.imageView.setImageUrl(data.getImageUrl(), ImageLoadManager.getImageLoader());
            }else{
                holder.imageView.setVisibility(View.GONE);
            }
            holder.content.setText(data.getMessage());

            holder.date.setText(holder.date.getResources().getString(R.string.create_date)
                    + " " + mSdf.format(new Date(data.getCreateTime())));

            if(data.getCreateTime() > System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 3)){
                holder.newIcon.setVisibility(View.VISIBLE);
                mOnClickPageDataListener.onShowNewItem(data);
            }else{
                holder.newIcon.setVisibility(View.GONE);
            }
            holder.date.setTextColor(holder.date.getResources().getColor(R.color.white));
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
        protected ImageView newIcon = null;
        protected TextView content = null;

        public PageDataHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.page_data_view);
            title = (TextView) itemView.findViewById(R.id.tv_item_notificaiton_data_list_title);
            date = (TextView) itemView.findViewById(R.id.tv_item_notificaiton_data_list_date);
            layoutTop = itemView.findViewById(R.id.layout_item_notificaiton_data_list_top);
            imageView = (NetworkImageView) itemView.findViewById(R.id.iv_item_notification_data_list_image);
            newIcon = (ImageView) itemView.findViewById(R.id.iv_item_notification_data_list_new);
            content = (TextView) itemView.findViewById(R.id.tv_item_notification_data_list_content);
        }
    }

}
