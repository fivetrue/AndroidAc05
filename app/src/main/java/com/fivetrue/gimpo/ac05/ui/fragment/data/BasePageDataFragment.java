package com.fivetrue.gimpo.ac05.ui.fragment.data;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.data.PageData;
import com.fivetrue.gimpo.ac05.vo.data.PageDataEntry;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 15..
 */
public class BasePageDataFragment extends BaseFragment {

    private static final String TAG = "BasePageDataFragment";

    public interface OnPageDataClickListener{
        void onClickPageData(PageDataEntry entry, PageData pageData, Integer textColor, Integer bgColor);
        void onClickPageDetail(PageDataEntry entry);
    }

    private PageDataEntry mPageEntry = null;

    private OnPageDataClickListener mOnPageDataClickListener = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof OnPageDataClickListener){
            mOnPageDataClickListener = (OnPageDataClickListener) getActivity();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData(mPageEntry);
    }

    protected void initData(){
        mPageEntry = getArguments().getParcelable(PageDataEntry.class.getName());
    }

    public void setData(PageDataEntry entry){

    }

    public PageDataEntry getPageEntry(){
        return mPageEntry;
    }

    protected int getPageTitleColor(){
        int color = Color.BLACK;
        if(mPageEntry != null && mPageEntry.getTitleColor() != null){
            color = parseColor(mPageEntry.getTitleColor());
        }
        return color;
    }

    protected int getPageTitleBgColor(){
        int color = Color.WHITE;
        if(mPageEntry != null && mPageEntry.getTitleBgColor() != null){
            color = parseColor(mPageEntry.getTitleBgColor());
        }
        return color;
    }
    protected int getPageContentColor(){
        int color = Color.WHITE;
        if(mPageEntry != null && mPageEntry.getContentColor() != null){
            color = parseColor(mPageEntry.getContentColor());
        }
        return color;
    }
    protected int getPageContentBgColor(){
        int color = Color.BLACK;
        if(mPageEntry != null && mPageEntry.getContentBgColor() != null){
            color = parseColor(mPageEntry.getContentBgColor());
        }
        return color;
    }

    protected int parseColor(String color){
        int c = getResources().getColor(R.color.colorAccent);
        if(color != null){
            c = Color.parseColor(color);
        }
        return c;
    }

    protected void onClickPageData(PageDataEntry entry, PageData pageData, Integer textColor, Integer bgColor){
        if(mOnPageDataClickListener != null){
            mOnPageDataClickListener.onClickPageData(entry, pageData, textColor, bgColor);
        }
    }

    protected void onClickPageDetail(PageDataEntry data){
        if(mOnPageDataClickListener != null){
            mOnPageDataClickListener.onClickPageDetail(data);
        }
    }



}
