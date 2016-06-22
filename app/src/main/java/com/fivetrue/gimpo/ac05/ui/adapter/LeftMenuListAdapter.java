package com.fivetrue.gimpo.ac05.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.view.SmallItemView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.vo.LeftMenu;



import java.util.List;

/**
 * Created by So on 2016-01-31.
 */
public class LeftMenuListAdapter extends BaseListAdapter<LeftMenu, LeftMenuListAdapter.ViewHolder> {


    public LeftMenuListAdapter(Context context, List<LeftMenu> data) {
        super(context, data, R.layout.item_left_menu_list);
    }

    @Override
    protected ViewHolder makeHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.image = (ImageView) view.findViewById(R.id.iv_item_left_menu_list);
        holder.title = (TextView) view.findViewById(R.id.tv_item_left_menu_list);
        return holder;
    }

    @Override
    protected void initView(ViewHolder holder, int position, View convertView, ViewGroup parent) {
        LeftMenu item = getItem(position);
        if(item != null) {
            holder.image.setImageResource(item.getIcon());
            holder.title.setText(item.getName());
        }
    }

    protected static class ViewHolder{
        private ImageView image = null;
        private TextView title = null;
    }
}