package com.fivetrue.gimpo.ac05.ui.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.vo.IBaseItem;
import com.fivetrue.gimpo.ac05.vo.data.MainItem;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

/**
 * Created by kwonojin on 2016. 9. 28..
 */

public class MainItemListAdapter extends BaseRecyclerAdapter<MainItem, MainItemListAdapter.BaseItemViewHolder> {

    private static final String TAG = "BaseItemListAdapter";

    public MainItemListAdapter(List<MainItem> data) {
        super(data, R.layout.item_main_list);
    }

    @Override
    protected BaseItemViewHolder makeHolder(View view) {
        BaseItemViewHolder holder = new BaseItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final BaseItemViewHolder holder, int position) {
        if(holder != null){
            final MainItem item = getItem(position);

            holder.textLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(holder, item);
                }
            });
            holder.title.setText(item.title);
            holder.subTitle.setVisibility(TextUtils.isEmpty(item.subTitle) ? View.GONE : View.VISIBLE);
            holder.subTitle.setText(item.subTitle);
            if(holder.subList.getAdapter() == null){
                holder.subList.setAdapter(item.adapter);
            }
        }
    }

    public static final class BaseItemViewHolder extends RecyclerView.ViewHolder{

        public final View layout;
        public final View textLayout;

        public final View textReadMore;

        public final TextView title;
        public final TextView subTitle;
        public final RecyclerView subList;

        public BaseItemViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_main_list);
            textLayout = itemView.findViewById(R.id.layout_item_main_list_title);
            textReadMore = itemView.findViewById(R.id.tv_item_main_list_read_more);

            title = (TextView) itemView.findViewById(R.id.tv_item_main_list_title);
            subTitle = (TextView) itemView.findViewById(R.id.tv_item_main_list_subtitle);
            subList = (RecyclerView) itemView.findViewById(R.id.rv_item_main_list_sub_list);
            subList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            subList.setItemAnimator(new SlideInRightAnimator());
        }
    }
}
