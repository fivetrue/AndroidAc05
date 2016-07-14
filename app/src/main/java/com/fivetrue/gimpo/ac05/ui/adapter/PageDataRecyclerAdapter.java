package com.fivetrue.gimpo.ac05.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.vo.rss.FeedMessage;
import com.fivetrue.gimpo.ac05.utils.Log;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class PageDataRecyclerAdapter extends BaseRecyclerAdapter<FeedMessage, PageDataRecyclerAdapter.PageDataHolder> {

    private static final String TAG = "PageDataRecyclerAdapter";

    public interface OnClickPageDataListener{
        void onClickPageData(View view, FeedMessage data);
        void onShowingNewIcon(FeedMessage data);
    }

    private int mContentColor = 0;
    private int mContentBgColor = 0;

    private Comparator<FeedMessage> mComparator = null;

    private OnClickPageDataListener mOnClickPageDataListener = null;

    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd");

    public PageDataRecyclerAdapter(List<FeedMessage> data, int contentColor, int contentBgColor) {
        super(data, R.layout.item_page_data_list);
        mContentColor = contentColor;
        mContentBgColor = contentBgColor;
        mComparator = new Comparator<FeedMessage>() {
            @Override
            public int compare(FeedMessage lhs, FeedMessage rhs) {
                return rhs.getPubDate().compareTo(lhs.getPubDate());
            }
        };
        Collections.sort(data, mComparator);
        notifyDataSetChanged();
    }

    @Override
    protected PageDataHolder makeHolder(View view) {
        PageDataHolder holder = new PageDataHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final PageDataHolder holder, int position) {
        final FeedMessage data = getItem(position);
        if (data != null) {
            holder.title.setText(data.getTitle());
            holder.title.setTextColor(mContentColor);
            holder.layoutTop.setBackgroundColor(mContentBgColor);
            holder.imageView.setImageBitmap(null);
            if(data.getDescription() != null){
                holder.content.setText(Html.fromHtml(data.getDescription()));
                String token = "src=\"";
                if(data.getDescription().contains(token)) {
                    int startTokenIndex = data.getDescription().indexOf(token);
                    String imgUrl = data.getDescription().substring(startTokenIndex + token.length());
                    imgUrl = imgUrl.substring(0, imgUrl.indexOf("\""));
                    Log.i(TAG, "setPageData: " + imgUrl);
                    holder.imageView.setImageUrl(imgUrl, ImageLoadManager.getImageLoader());
                }            }
            if(data.getPubDate() != null){
                holder.date.setText(holder.date.getResources().getString(R.string.create_date) + " " + data.getPubDate());
                String date = data.getPubDate().substring(0, data.getPubDate().lastIndexOf(" ")).trim();
                String currentDate = mSdf.format(new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 1)));
                if(date.compareTo(currentDate) < 0){
                    holder.newIcon.setVisibility(View.GONE);
                }else{
                    holder.newIcon.setVisibility(View.VISIBLE);
                    mOnClickPageDataListener.onShowingNewIcon(data);
                }
            }
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickPageDataListener != null) {
                        mOnClickPageDataListener.onClickPageData(v, data);
                    }
                }
            });
            holder.container.setVisibility(View.VISIBLE);

        }
    }

    public void setOnClickPageDataListener(OnClickPageDataListener ll){
        mOnClickPageDataListener = ll;
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
            title = (TextView) itemView.findViewById(R.id.tv_item_page_data_list_title);
            date = (TextView) itemView.findViewById(R.id.tv_item_page_data_list_date);
            layoutTop = itemView.findViewById(R.id.layout_item_page_data_list_top);
            imageView = (NetworkImageView) itemView.findViewById(R.id.iv_item_page_data_list_image);
            newIcon = (ImageView) itemView.findViewById(R.id.iv_item_page_data_list_new);
            content = (TextView) itemView.findViewById(R.id.tv_item_page_data_list_content);
            TranslateAnimation anim = new TranslateAnimation(0, 0, -100, 0);
            container.setAnimation(anim);
            container.setVisibility(View.GONE);
        }
    }
}
