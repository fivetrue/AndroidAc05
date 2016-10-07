package com.fivetrue.gimpo.ac05.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.vo.data.TownData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class TownDataRecyclerAdapter extends BaseRecyclerAdapter<TownData, TownDataRecyclerAdapter.PageDataHolder> {

    private static final String TAG = "PageDataRecyclerAdapter";

    public interface OnClickPageDataListener{
        void onClickPageData(View view, TownData data);
        void onShowingNew(TownData data);
    }

    private int mContentColor = 0;
    private int mContentBgColor = 0;

    private OnClickPageDataListener mOnClickPageDataListener = null;

    private SimpleDateFormat mSdf = new SimpleDateFormat("yyyy-MM-dd");

    public TownDataRecyclerAdapter(List<TownData> data, int contentColor, int contentBgColor) {
        super(data, R.layout.item_page_data_list);
        mContentColor = contentColor;
        mContentBgColor = contentBgColor;
    }

    @Override
    protected PageDataHolder makeHolder(View view) {
        PageDataHolder holder = new PageDataHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final PageDataHolder holder, int position) {
        final TownData data = getItem(position);
        if (data != null) {
            holder.title.setText(data.getTitle());
            holder.title.setTextColor(mContentColor);
            holder.layoutTop.setBackgroundColor(mContentBgColor);
            holder.imageView.setImageBitmap(null);
            if(data.getContent() != null){
                holder.content.setText(Html.fromHtml(data.getContent()));
                String token = "src=\"";
                if(data.getContent().contains(token)) {
                    int startTokenIndex = data.getContent().indexOf(token);
                    String imgUrl = data.getContent().substring(startTokenIndex + token.length());
                    imgUrl = imgUrl.substring(0, imgUrl.indexOf("\""));
                    Log.i(TAG, "setPageData: " + imgUrl);
                    holder.imageView.setImageUrl(imgUrl, ImageLoadManager.getImageLoader());
                }
            }
            if(data.getDate() != null){
                String date = holder.date.getResources().getString(R.string.create_date) + " " + data.getDate();
                holder.date.setText(date);
                String currentDate = mSdf.format(new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 7)));
                if(data.getDate().compareTo(currentDate) < 0){
                    holder.newIcon.setVisibility(View.GONE);
                }else{
                    holder.newIcon.setVisibility(View.VISIBLE);
                    mOnClickPageDataListener.onShowingNew(data);
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
