package com.fivetrue.gimpo.ac05.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fivetrue.fivetrueandroid.ui.adapter.BaseListAdapter;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.vo.user.District;

import java.util.List;

/**
 * Created by kwonojin on 16. 6. 25..
 */
public class DistrictSpinnerAdapter extends BaseListAdapter<District, DistrictSpinnerAdapter.Holder> {


    public DistrictSpinnerAdapter(Context context, List<District> data) {
        super(context, data, R.layout.item_simple_spinner);
    }

    @Override
    protected Holder makeHolder(View view, int postion) {
        Holder holder = new Holder();
        holder.text = (TextView) view.findViewById(R.id.tv_item_simple_spinner);
        return holder;
    }

    @Override
    protected void initView(Holder holder, int position, View convertView, ViewGroup parent) {
        District data = getItem(position);
        if(holder != null){
            if(data != null){
                holder.text.setText(data.getDistrictName());
            }else{
                holder.text.setText(R.string.input_user_dong);
            }
        }
    }

    protected static final class Holder{
        TextView text = null;
    }
}
