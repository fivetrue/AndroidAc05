package com.fivetrue.gimpo.ac05.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.image.ImageLoadManager;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.data.PageData;

import java.util.List;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class PageDataRecyclerAdapter extends BaseRecyclerAdapter<PageData, PageDataRecyclerAdapter.PageDataHolder> {

    private static final String TAG = "PageDataRecyclerAdapter";

    public interface OnClickPageDataListener{
        void onClickPageData(View view, PageData data);
    }

    private int mContentColor = 0;
    private int mContentBgColor = 0;

    private OnClickPageDataListener mOnClickPageDataListener = null;

    public PageDataRecyclerAdapter(List<PageData> data, int contentColor, int contentBgColor) {
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
        final PageData data = getItem(position);
        if (data != null) {
            holder.title.setText(data.getPageTitle());
            holder.title.setTextColor(mContentColor);
            holder.layoutTop.setBackgroundColor(mContentBgColor);
            holder.imageView.setImageBitmap(null);
            if(data.getPageContent() != null){
                holder.content.setText(Html.fromHtml(data.getPageContent()));
                String token = "src=\"";
                if(data.getPageContent().contains(token)) {
                    int startTokenIndex = data.getPageContent().indexOf(token);
                    String imgUrl = data.getPageContent().substring(startTokenIndex + token.length());
                    imgUrl = imgUrl.substring(0, imgUrl.indexOf("\""));
                    Log.i(TAG, "setPageData: " + imgUrl);
                    holder.imageView.setImageUrl(imgUrl, ImageLoadManager.getImageLoader());
                }            }
            if(data.getPageDate() != null){
                String date = holder.date.getResources().getString(R.string.create_date) + " " + data.getPageDate();
                holder.date.setText(date);
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
