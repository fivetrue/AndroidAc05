package com.fivetrue.gimpo.ac05.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.vo.IBaseItem;

import java.util.List;

/**
 * Created by kwonojin on 2016. 9. 28..
 */

public class BaseItemListAdapter<T extends IBaseItem> extends BaseRecyclerAdapter<T, BaseItemListAdapter.BaseItemViewHolder> {

    private static final String TAG = "BaseItemListAdapter";

    public BaseItemListAdapter(List<T> data, int layoutResource) {
        super(data, layoutResource);
    }

    @Override
    protected BaseItemViewHolder makeHolder(View view) {
        BaseItemViewHolder holder = new BaseItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final BaseItemViewHolder holder, int position) {
        if(holder != null){
            final T item = getItem(position);

            holder.image.setImageUrl(item.getImageUrl(), ImageLoadManager.getImageLoader());
            holder.newIcon.setVisibility(item.isShowingNew() ? View.VISIBLE : View.GONE);
            holder.title.setText(item.getTitle());
            holder.title.setVisibility(TextUtils.isEmpty(item.getTitle()) ? View.GONE : View.VISIBLE);
            holder.content.setText(item.getContent());
            holder.content.setVisibility(TextUtils.isEmpty(item.getContent()) ? View.GONE : View.VISIBLE);
            holder.subcontent.setText(item.getSubContent());
            holder.subcontent.setVisibility(TextUtils.isEmpty(item.getSubContent()) ? View.GONE : View.VISIBLE);
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem(holder, item);
                }
            });
        }

    }

    public static final class BaseItemViewHolder extends RecyclerView.ViewHolder{

        public final View layout;
        public final NetworkImageView image;
        public final ImageView newIcon;
        public final View textLayout;
        public final TextView title;
        public final TextView content;
        public final TextView subcontent;

        public BaseItemViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_item_base_content);
            textLayout = itemView.findViewById(R.id.layout_item_base_text);
            image = (NetworkImageView) itemView.findViewById(R.id.iv_item_base_image);
            newIcon = (ImageView) itemView.findViewById(R.id.iv_item_base_new);
            title = (TextView) itemView.findViewById(R.id.tv_item_base_item_title);
            content = (TextView) itemView.findViewById(R.id.tv_item_base_item_content);
            subcontent = (TextView) itemView.findViewById(R.id.tv_item_base_item_subcontent);
        }
    }
}
